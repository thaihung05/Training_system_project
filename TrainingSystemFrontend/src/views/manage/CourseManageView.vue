<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLazyList } from '@/composables/useLazyList'
import { useAsyncData } from '@/composables/useAsyncData'
import courseService from '@/api/courseService'
import departmentService from '@/api/departmentService'
import { confirmDialog } from '@/utils/alerts'

const router = useRouter()

const { items: courses, loading, loadingMore, hasMore, loadMore, reload } = useLazyList(
  (page, size) => courseService.getMyCourses(page, size),
  15,
)
const { data: departments } = useAsyncData(() => departmentService.getAll())

const editingId = ref(null)
const form = ref({ title: '', description: '', departmentId: null, isActive: true })
const saving = ref(false)
const errorMsg = ref('')

function resetForm() {
  editingId.value = null
  form.value = { title: '', description: '', departmentId: null, isActive: true }
  errorMsg.value = ''
}

function editCourse(c) {
  editingId.value = c.id
  form.value = {
    title: c.title,
    description: c.description,
    departmentId: c.departmentId ? c.departmentId.id : null,
    isActive: c.isActive,
  }
  errorMsg.value = ''
}

function buildPayload() {
  return {
    title: form.value.title,
    description: form.value.description,
    departmentId: form.value.departmentId ? { id: form.value.departmentId } : null,
    isActive: form.value.isActive,
  }
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  try {
    if (editingId.value) {
      await courseService.update(editingId.value, buildPayload())
    } else {
      await courseService.create(buildPayload())
    }
    resetForm()
    reload()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function deactivate(c) {
  if (!(await confirmDialog(`Ẩn khoá học "${c.title}"?`))) return
  await courseService.remove(c.id)
  reload()
}

async function reactivate(c) {
  await courseService.update(c.id, {
    title: c.title,
    description: c.description,
    departmentId: c.departmentId ? { id: c.departmentId.id } : null,
    isActive: true,
  })
  reload()
}

function goEnroll(c) {
  router.push({ name: 'course-enrollments', params: { courseId: c.id } })
}

function goLessons(c) {
  router.push({ name: 'manage-lessons', params: { courseId: c.id } })
}

function goTests(c) {
  router.push({ name: 'manage-tests', params: { courseId: c.id } })
}

function goCertificates(c) {
  router.push({ name: 'course-certificates', params: { courseId: c.id } })
}

</script>

<template>
  <div class="page">
    <div class="manage-header">
      <h1>Quản lý khoá học</h1>
    </div>

    <div class="manage-layout">
      <div class="manage-table-wrap">
        <div class="manage-table-header">
          <div>TÊN KHOÁ HỌC</div>
          <div>PHÒNG BAN</div>
          <div>TRẠNG THÁI</div>
          <div>HÀNH ĐỘNG</div>
        </div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="courses.length === 0" class="empty-state">Bạn chưa tạo khoá học nào.</div>
        <template v-else>
          <div v-for="c in courses" :key="c.id" class="manage-row">
            <div class="manage-row-title">{{ c.title }}</div>
            <div class="manage-row-dept">{{ c.departmentId ? c.departmentId.name : 'Toàn công ty' }}</div>
            <div>
              <span class="badge" :class="c.isActive ? 'badge-success' : 'badge-neutral'">
                {{ c.isActive ? 'Đang mở' : 'Đã ẩn' }}
              </span>
            </div>
            <div class="manage-row-actions">
              <span class="manage-action" @click="editCourse(c)">Sửa</span>
              <span class="manage-action" @click="goLessons(c)">Bài học</span>
              <span class="manage-action" @click="goTests(c)">Bài kiểm tra</span>
              <span class="manage-action" @click="goEnroll(c)">Ghi danh</span>
              <span class="manage-action" @click="goCertificates(c)">Chứng chỉ</span>
              <span v-if="c.isActive" class="manage-action manage-action--danger" @click="deactivate(c)">Ẩn</span>
              <span v-else class="manage-action" @click="reactivate(c)">Mở lại</span>
            </div>
          </div>
        </template>
        <button v-if="hasMore" class="btn btn-secondary courses-load-more" :disabled="loadingMore" @click="loadMore">
          {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
        </button>
      </div>

      <div class="manage-form card">
        <h2>{{ editingId ? 'Sửa khoá học' : 'Thêm khoá học mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Tên khoá học</label>
          <input v-model="form.title" type="text" class="input" placeholder="VD: Kỹ năng bán hàng nâng cao" />
        </div>
        <div class="form-field">
          <label>Phòng ban</label>
          <select v-model="form.departmentId" class="input">
            <option :value="null">Toàn công ty</option>
            <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
          </select>
        </div>
        <div class="form-field">
          <label>Mô tả ngắn</label>
          <textarea v-model="form.description" class="input" rows="3" placeholder="Mô tả nội dung khoá học..."></textarea>
        </div>
        <label class="manage-checkbox">
          <input type="checkbox" v-model="form.isActive" />
          Đang mở (nhân viên có thể xem và được ghi danh)
        </label>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">
            {{ editingId ? 'Lưu thay đổi' : 'Lưu khoá học' }}
          </button>
          <button v-if="editingId" class="btn btn-secondary" @click="resetForm">Huỷ</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./CourseManageView.css"></style>
