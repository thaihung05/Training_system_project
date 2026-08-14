<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import courseService from '@/api/courseService'
import testService from '@/api/testService'
import { showError } from '@/utils/alerts'
import TrainerCourseNav from '@/components/trainer/TrainerCourseNav.vue'
import FormModal from '@/components/common/FormModal.vue'
import { BarChart3, ListChecks, Pencil, Plus, Power } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const courseId = Number(route.params.courseId)

const { data: course } = useAsyncData(() => courseService.getCourseById(courseId))
const { data: tests, loading, refresh } = useAsyncData(() => testService.getByCourse(courseId))

const editingId = ref(null)
const formOpen = ref(false)
const form = ref({ title: '', passScore: 70, maxAttempts: 3 })
const saving = ref(false)
const errorMsg = ref('')

function closeForm() {
  editingId.value = null
  form.value = { title: '', passScore: 70, maxAttempts: 3 }
  errorMsg.value = ''
  formOpen.value = false
}

function openCreateForm() {
  closeForm()
  formOpen.value = true
}

function editTest(t) {
  editingId.value = t.id
  form.value = { title: t.title, passScore: t.passScore, maxAttempts: t.maxAttempts }
  errorMsg.value = ''
  formOpen.value = true
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  try {
    if (editingId.value) {
      const existing = tests.value.find((t) => t.id === editingId.value)
      await testService.update(editingId.value, {
        title: form.value.title,
        passScore: Number(form.value.passScore),
        maxAttempts: Number(form.value.maxAttempts),
        isActive: existing.isActive,
      })
    } else {
      await testService.create(courseId, {
        title: form.value.title,
        passScore: Number(form.value.passScore),
        maxAttempts: Number(form.value.maxAttempts),
      })
    }
    closeForm()
    refresh()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function toggleActive(t) {
  try {
    await testService.update(t.id, {
      title: t.title,
      passScore: t.passScore,
      maxAttempts: t.maxAttempts,
      isActive: !t.isActive,
    })
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Có lỗi xảy ra.')
  }
}

function goQuestions(t) {
  router.push({ name: 'manage-questions', params: { courseId, testId: t.id } })
}

function goResults(t) {
  router.push({ name: 'test-attempts-roster', params: { courseId, testId: t.id } })
}
</script>

<template>
  <div class="page trainer-page">
    <TrainerCourseNav :course="course" active="tests" />
    <div class="trainer-context-header">
      <div>
        <h2>Bài kiểm tra</h2>
        <p>{{ tests?.length ?? 0 }} bài kiểm tra đang được thiết lập.</p>
      </div>
      <button class="btn btn-primary" @click="openCreateForm"><Plus :size="16" /> Thêm bài kiểm tra</button>
    </div>

    <div class="manage-table-wrap trainer-panel">
        <div class="test-table-header">
          <div>TÊN BÀI KIỂM TRA</div>
          <div>ĐIỂM ĐẠT</div>
          <div>SỐ LẦN LÀM</div>
          <div>TRẠNG THÁI</div>
          <div>HÀNH ĐỘNG</div>
        </div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="tests.length === 0" class="empty-state">Chưa có bài kiểm tra nào.</div>
        <template v-else>
          <div v-for="t in tests" :key="t.id" class="test-row">
            <div class="test-row-title">{{ t.title }}</div>
            <div class="test-row-mono">{{ t.passScore }}%</div>
            <div class="test-row-mono">{{ t.maxAttempts }}</div>
            <div>
              <span class="badge" :class="t.isActive ? 'badge-success' : 'badge-neutral'">
                {{ t.isActive ? 'Đang mở' : 'Nháp' }}
              </span>
            </div>
            <div class="row-action-group">
              <button class="row-action-btn" @click="editTest(t)"><Pencil :size="13" /> Sửa</button>
              <button class="row-action-btn" @click="goQuestions(t)"><ListChecks :size="13" /> Soạn câu hỏi</button>
              <button class="row-action-btn" @click="goResults(t)"><BarChart3 :size="13" /> Kết quả</button>
              <button class="row-action-btn" @click="toggleActive(t)"><Power :size="13" /> {{ t.isActive ? 'Tắt' : 'Kích hoạt' }}</button>
            </div>
          </div>
        </template>
    </div>

    <FormModal
      v-if="formOpen"
      :title="editingId ? 'Chỉnh sửa bài kiểm tra' : 'Thêm bài kiểm tra mới'"
      description="Thiết lập tên, điểm đạt và số lần nhân viên được phép làm bài."
      :submit-label="editingId ? 'Lưu thay đổi' : 'Thêm bài kiểm tra'"
      :saving="saving"
      :disabled="!form.title.trim() || form.passScore < 0 || form.passScore > 100 || form.maxAttempts < 1"
      @close="closeForm"
      @submit="submit"
    >
      <section class="test-form-section">
        <div class="test-form-heading"><h3>Thiết lập bài kiểm tra</h3><p>Các thông số này được áp dụng cho mọi nhân viên làm bài.</p></div>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label for="test-title">Tên bài kiểm tra</label>
          <input id="test-title" v-model="form.title" type="text" class="input" placeholder="Ví dụ: Kiểm tra cuối khóa" required />
        </div>
        <div class="test-form-grid">
          <div class="form-field">
            <label for="test-score">Điểm đạt (%)</label>
            <input id="test-score" v-model="form.passScore" type="number" min="0" max="100" class="input" required />
          </div>
          <div class="form-field">
            <label for="test-attempts">Số lần làm tối đa</label>
            <input id="test-attempts" v-model="form.maxAttempts" type="number" min="1" class="input" required />
          </div>
        </div>
      </section>
    </FormModal>
  </div>
</template>

<style scoped src="./TestManageView.css"></style>
