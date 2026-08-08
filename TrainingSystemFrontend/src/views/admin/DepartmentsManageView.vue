<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import departmentService from '@/api/departmentService'
import userService from '@/api/userService'
import courseService from '@/api/courseService'
import { confirmDialog, showError } from '@/utils/alerts'

const { data: departments, loading, refresh } = useAsyncData(() => departmentService.getAll())
const { data: users } = useAsyncData(() => userService.getAll())
const { data: courses } = useAsyncData(() => courseService.getCourses())

function employeeCountFor(deptId) {
  return (users.value || []).filter((u) => u.departmentId && u.departmentId.id === deptId).length
}
function courseCountFor(deptId) {
  return (courses.value || []).filter((c) => c.departmentId && c.departmentId.id === deptId).length
}

const editingId = ref(null)
const form = ref({ name: '' })
const saving = ref(false)
const errorMsg = ref('')
const showForm = ref(false)

function openCreateForm() {
  editingId.value = null
  form.value = { name: '' }
  errorMsg.value = ''
  showForm.value = true
}

function openEditForm(d) {
  editingId.value = d.id
  form.value = { name: d.name }
  errorMsg.value = ''
  showForm.value = true
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  try {
    if (editingId.value) {
      await departmentService.update(editingId.value, { name: form.value.name })
    } else {
      await departmentService.create({ name: form.value.name })
    }
    showForm.value = false
    refresh()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function remove(d) {
  if (!(await confirmDialog(`Xoá phòng ban "${d.name}"? Mọi khoá học/nhân viên đang thuộc phòng ban này sẽ chuyển về "Toàn công ty", không bị xoá.`))) return
  try {
    await departmentService.remove(d.id)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá phòng ban này.')
  }
}
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="manage-header">
        <h1>Phòng ban</h1>
        <button class="btn btn-primary" @click="openCreateForm">+ Thêm phòng ban</button>
      </div>

      <div v-if="showForm" class="card inline-form">
        <h2>{{ editingId ? 'Sửa phòng ban' : 'Thêm phòng ban mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Tên phòng ban</label>
          <input v-model="form.name" type="text" class="input" placeholder="VD: Phòng Nhân sự" />
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">Lưu</button>
          <button class="btn btn-secondary" @click="showForm = false">Huỷ</button>
        </div>
      </div>

      <p v-if="loading" class="state-text">Đang tải...</p>
      <div v-else-if="departments.length === 0" class="empty-state">Chưa có phòng ban nào.</div>
      <div v-else class="dept-grid">
        <div v-for="d in departments" :key="d.id" class="card dept-card">
          <div class="dept-name">{{ d.name }}</div>
          <div class="dept-stats">
            <div>
              <div class="dept-stat-label">Nhân viên</div>
              <div class="dept-stat-value">{{ employeeCountFor(d.id) }}</div>
            </div>
            <div>
              <div class="dept-stat-label">Khoá học</div>
              <div class="dept-stat-value">{{ courseCountFor(d.id) }}</div>
            </div>
          </div>
          <div class="dept-actions">
            <span class="manage-action" @click="openEditForm(d)">Sửa</span>
            <span class="manage-action manage-action--danger" @click="remove(d)">Xoá</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./DepartmentsManageView.css"></style>
