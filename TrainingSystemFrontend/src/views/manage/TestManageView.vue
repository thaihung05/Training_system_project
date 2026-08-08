<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import courseService from '@/api/courseService'
import testService from '@/api/testService'
import { showError } from '@/utils/alerts'
import { ChevronLeft } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const courseId = Number(route.params.courseId)

const { data: course } = useAsyncData(() => courseService.getCourseById(courseId))
const { data: tests, loading, refresh } = useAsyncData(() => testService.getByCourse(courseId))

const editingId = ref(null)
const form = ref({ title: '', passScore: 70, maxAttempts: 3 })
const saving = ref(false)
const errorMsg = ref('')

function resetForm() {
  editingId.value = null
  form.value = { title: '', passScore: 70, maxAttempts: 3 }
  errorMsg.value = ''
}

function editTest(t) {
  editingId.value = t.id
  form.value = { title: t.title, passScore: t.passScore, maxAttempts: t.maxAttempts }
  errorMsg.value = ''
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
    resetForm()
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
  <div class="page">
    <div class="detail-header">
      <button class="back-btn" @click="router.push({ name: 'manage-courses' })"><ChevronLeft :size="18" /></button>
      <div class="detail-heading">
        <h1>Bài kiểm tra — {{ course?.title }}</h1>
        <div class="detail-meta">{{ tests?.length ?? 0 }} bài kiểm tra</div>
      </div>
    </div>

    <div class="manage-layout">
      <div class="manage-table-wrap">
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
            <div class="manage-row-actions">
              <span class="manage-action" @click="editTest(t)">Sửa</span>
              <span class="manage-action" @click="goQuestions(t)">Soạn câu hỏi</span>
              <span class="manage-action" @click="goResults(t)">Kết quả</span>
              <span class="manage-action" @click="toggleActive(t)">{{ t.isActive ? 'Tắt' : 'Kích hoạt' }}</span>
            </div>
          </div>
        </template>
      </div>

      <div class="manage-form card">
        <h2>{{ editingId ? 'Sửa bài kiểm tra' : 'Thêm bài kiểm tra mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Tên bài kiểm tra</label>
          <input v-model="form.title" type="text" class="input" placeholder="VD: Kiểm tra chương 1" />
        </div>
        <div class="form-field">
          <label>Điểm đạt (%)</label>
          <input v-model="form.passScore" type="number" min="0" max="100" class="input" />
        </div>
        <div class="form-field">
          <label>Số lần làm tối đa</label>
          <input v-model="form.maxAttempts" type="number" min="1" class="input" />
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">
            {{ editingId ? 'Lưu thay đổi' : 'Lưu bài kiểm tra' }}
          </button>
          <button v-if="editingId" class="btn btn-secondary" @click="resetForm">Huỷ</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./TestManageView.css"></style>
