<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import courseService from '@/api/courseService'
import lessonService from '@/api/lessonService'
import uploadService from '@/api/uploadService'
import enrollmentService from '@/api/enrollmentService'
import { confirmDialog, showError } from '@/utils/alerts'
import TrainerCourseNav from '@/components/trainer/TrainerCourseNav.vue'
import FormModal from '@/components/common/FormModal.vue'
import { ChevronUp, ChevronDown, FileText, Pencil, Plus, Trash2, Upload } from '@lucide/vue'

const route = useRoute()
const courseId = Number(route.params.courseId)

const { data: course } = useAsyncData(() => courseService.getCourseById(courseId))
const { data: lessons, loading, refresh } = useAsyncData(() => lessonService.getByCourse(courseId))
const { data: roster } = useAsyncData(() => enrollmentService.getRoster(courseId))

const locked = computed(() => (roster.value || []).length > 0)

const editingId = ref(null)
const formOpen = ref(false)
const form = ref({ title: '', slidePdfUrl: '' })
const uploading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const pdfInput = ref(null)

function openPdfPicker() {
  pdfInput.value.click()
}

function closeForm() {
  editingId.value = null
  form.value = { title: '', slidePdfUrl: '' }
  errorMsg.value = ''
  formOpen.value = false
}

function openCreateForm() {
  closeForm()
  formOpen.value = true
}

function editLesson(l) {
  editingId.value = l.id
  form.value = { title: l.title, slidePdfUrl: l.slidePdfUrl || '' }
  errorMsg.value = ''
  formOpen.value = true
}

async function handleFileChange(event) {
  const file = event.target.files[0]
  event.target.value = ''
  if (!file) return
  uploading.value = true
  errorMsg.value = ''
  try {
    const res = await uploadService.uploadPdf(file)
    form.value.slidePdfUrl = res.data.url
  } catch (err) {
    errorMsg.value = err.response?.data || 'Tải file thất bại.'
  } finally {
    uploading.value = false
  }
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  const payload = { title: form.value.title, slidePdfUrl: form.value.slidePdfUrl || null }
  try {
    if (editingId.value) {
      await lessonService.update(editingId.value, payload)
    } else {
      await lessonService.create(courseId, payload)
    }
    closeForm()
    refresh()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function removeLesson(l) {
  if (!(await confirmDialog(`Xoá bài học "${l.title}"?`))) return
  try {
    await lessonService.remove(l.id)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá bài học này.')
  }
}

async function move(index, direction) {
  const list = [...lessons.value]
  const target = index + direction
  if (target < 0 || target >= list.length) return
  ;[list[index], list[target]] = [list[target], list[index]]
  await lessonService.reorder(courseId, list.map((l) => l.id))
  refresh()
}
</script>

<template>
  <div class="page trainer-page">
    <TrainerCourseNav :course="course" active="lessons" />
    <div class="trainer-context-header">
      <div>
        <h2>Bài học và tài liệu</h2>
        <p>{{ lessons?.length ?? 0 }} bài học, có thể kéo thứ tự bằng nút lên và xuống.</p>
      </div>
      <button class="btn btn-primary" :disabled="locked" :title="locked ? 'Khóa học đã có người ghi danh' : ''" @click="openCreateForm"><Plus :size="16" /> Thêm bài học</button>
    </div>

    <p v-if="locked" class="alert alert-error lessonmg-locked-alert">
      Khóa học đã có người ghi danh, không thể thay đổi bài học. Hãy tạo khóa học mới nếu cần cập nhật nội dung.
    </p>

    <div class="manage-table-wrap trainer-panel lessonmg-list-panel">
        <div class="trainer-panel-heading">
          <div><h2>Lộ trình bài học</h2><p>Sắp xếp nội dung theo thứ tự nhân viên sẽ học.</p></div>
          <FileText :size="19" />
        </div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="lessons.length === 0" class="empty-state">Chưa có bài học nào.</div>
        <template v-else>
          <div v-for="(l, index) in lessons" :key="l.id" class="lessonmg-row">
            <div class="lessonmg-order">
              <button class="lessonmg-move" :disabled="locked || index === 0" @click="move(index, -1)"><ChevronUp :size="13" /></button>
              <button class="lessonmg-move" :disabled="locked || index === lessons.length - 1" @click="move(index, 1)"><ChevronDown :size="13" /></button>
            </div>
            <div class="lessonmg-info">
              <div class="lessonmg-title">Bài {{ index + 1 }}: {{ l.title }}</div>
              <div class="lessonmg-pdf">{{ l.slidePdfUrl ? 'Đã có tài liệu PDF' : 'Chưa có tài liệu' }}</div>
            </div>
            <div class="row-action-group">
              <button class="row-action-btn" :disabled="locked" @click="editLesson(l)"><Pencil :size="13" /> Sửa</button>
              <button class="row-action-btn row-action-btn--danger" :disabled="locked" @click="removeLesson(l)"><Trash2 :size="13" /> Xoá</button>
            </div>
          </div>
        </template>
    </div>

    <FormModal
      v-if="formOpen"
      :title="editingId ? 'Chỉnh sửa bài học' : 'Thêm bài học mới'"
      description="Đặt tên dễ nhận biết và đính kèm tài liệu PDF để nhân viên học theo đúng thứ tự."
      :submit-label="editingId ? 'Lưu thay đổi' : 'Thêm bài học'"
      :saving="saving"
      :disabled="uploading || !form.title.trim()"
      @close="closeForm"
      @submit="submit"
    >
      <section class="lessonmg-form-section">
        <div class="lessonmg-section-heading">
          <h3>Thông tin bài học</h3>
          <p>Tên bài học sẽ xuất hiện trong lộ trình của nhân viên.</p>
        </div>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label for="lesson-title">Tên bài học</label>
          <input id="lesson-title" v-model="form.title" type="text" class="input" placeholder="Ví dụ: Quy trình tư vấn tại quầy" required />
        </div>
      </section>

      <section class="lessonmg-form-section">
        <div class="lessonmg-section-heading">
          <h3>Tài liệu học tập</h3>
          <p>Chấp nhận tệp PDF. Có thể bổ sung hoặc thay tệp sau.</p>
        </div>
        <div class="form-field">
          <label>Tệp PDF</label>
          <div class="lessonmg-file-picker">
            <FileText :size="22" />
            <div>
              <strong>{{ form.slidePdfUrl ? 'Tài liệu đã sẵn sàng' : 'Chưa có tài liệu' }}</strong>
              <span>{{ form.slidePdfUrl ? 'Chọn tệp khác nếu cần thay thế.' : 'Tải lên slide hoặc tài liệu hướng dẫn.' }}</span>
            </div>
            <button type="button" class="btn btn-secondary btn-sm" :disabled="uploading" @click="openPdfPicker">
              <Upload :size="14" /> {{ uploading ? 'Đang tải…' : form.slidePdfUrl ? 'Đổi tệp' : 'Chọn PDF' }}
            </button>
          </div>
          <input ref="pdfInput" type="file" accept="application/pdf" class="hidden-file-input" @change="handleFileChange" />
          <div v-if="form.slidePdfUrl && !uploading" class="lessonmg-uploaded">Đã tải tài liệu lên hệ thống.</div>
        </div>
      </section>
    </FormModal>
  </div>
</template>

<style scoped src="./LessonManageView.css"></style>
