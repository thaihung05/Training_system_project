<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import courseService from '@/api/courseService'
import lessonService from '@/api/lessonService'
import uploadService from '@/api/uploadService'
import { confirmDialog, showError } from '@/utils/alerts'
import { ChevronLeft, ChevronUp, ChevronDown } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const courseId = Number(route.params.courseId)

const { data: course } = useAsyncData(() => courseService.getCourseById(courseId))
const { data: lessons, loading, refresh } = useAsyncData(() => lessonService.getByCourse(courseId))

const editingId = ref(null)
const form = ref({ title: '', slidePdfUrl: '' })
const uploading = ref(false)
const saving = ref(false)
const errorMsg = ref('')

function resetForm() {
  editingId.value = null
  form.value = { title: '', slidePdfUrl: '' }
  errorMsg.value = ''
}

function editLesson(l) {
  editingId.value = l.id
  form.value = { title: l.title, slidePdfUrl: l.slidePdfUrl || '' }
  errorMsg.value = ''
}

async function handleFileChange(event) {
  const file = event.target.files[0]
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
    resetForm()
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
  <div class="page">
    <div class="detail-header">
      <button class="back-btn" @click="router.push({ name: 'manage-courses' })"><ChevronLeft :size="18" /></button>
      <div class="detail-heading">
        <h1>Bài học — {{ course?.title }}</h1>
        <div class="detail-meta">{{ lessons?.length ?? 0 }} bài</div>
      </div>
    </div>

    <div class="manage-layout">
      <div class="manage-table-wrap">
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="lessons.length === 0" class="empty-state">Chưa có bài học nào.</div>
        <template v-else>
          <div v-for="(l, index) in lessons" :key="l.id" class="lessonmg-row">
            <div class="lessonmg-order">
              <button class="lessonmg-move" :disabled="index === 0" @click="move(index, -1)"><ChevronUp :size="13" /></button>
              <button class="lessonmg-move" :disabled="index === lessons.length - 1" @click="move(index, 1)"><ChevronDown :size="13" /></button>
            </div>
            <div class="lessonmg-info">
              <div class="lessonmg-title">Bài {{ index + 1 }}: {{ l.title }}</div>
              <div class="lessonmg-pdf">{{ l.slidePdfUrl ? 'Đã có tài liệu PDF' : 'Chưa có tài liệu' }}</div>
            </div>
            <div class="manage-row-actions">
              <span class="manage-action" @click="editLesson(l)">Sửa</span>
              <span class="manage-action manage-action--danger" @click="removeLesson(l)">Xoá</span>
            </div>
          </div>
        </template>
      </div>

      <div class="manage-form card">
        <h2>{{ editingId ? 'Sửa bài học' : 'Thêm bài học mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Tên bài học</label>
          <input v-model="form.title" type="text" class="input" placeholder="VD: Bài 1: Giới thiệu" />
        </div>
        <div class="form-field">
          <label>Tài liệu PDF</label>
          <input type="file" accept="application/pdf" @change="handleFileChange" />
          <div v-if="uploading" class="state-text">Đang tải file lên...</div>
          <div v-else-if="form.slidePdfUrl" class="lessonmg-uploaded">Đã tải lên xong.</div>
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving || uploading" @click="submit">
            {{ editingId ? 'Lưu thay đổi' : 'Thêm bài học' }}
          </button>
          <button v-if="editingId" class="btn btn-secondary" @click="resetForm">Huỷ</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./LessonManageView.css"></style>
