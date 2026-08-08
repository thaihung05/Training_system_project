<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useAsyncData } from '@/composables/useAsyncData'
import { useLazyList } from '@/composables/useLazyList'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import userService from '@/api/userService'
import departmentService from '@/api/departmentService'
import pointService from '@/api/pointService'
import { confirmDialog, showError } from '@/utils/alerts'

const auth = useAuthStore()

const keyword = ref('')
const { items: users, loading, loadingMore, hasMore, loadMore, reload } = useLazyList(
  (page, size) => userService.getAll(keyword.value, page, size),
)
const { data: departments } = useAsyncData(() => departmentService.getAll())

let debounceTimer = null
function onSearchInput() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(reload, 400)
}

const deptFilter = ref(null)
const filteredUsers = computed(() => {
  const list = users.value || []
  if (deptFilter.value === null) return list
  if (deptFilter.value === 'none') return list.filter((u) => !u.departmentId)
  return list.filter((u) => u.departmentId && u.departmentId.id === deptFilter.value)
})

const editingId = ref(null)
const showForm = ref(false)
const form = ref({ name: '', username: '', email: '', password: '', role: 'EMPLOYEE', departmentId: null })
const saving = ref(false)
const errorMsg = ref('')

function openCreateForm() {
  editingId.value = null
  form.value = { name: '', username: '', email: '', password: '', role: 'EMPLOYEE', departmentId: null }
  errorMsg.value = ''
  showForm.value = true
}

function openEditForm(u) {
  editingId.value = u.id
  form.value = {
    name: u.name,
    username: u.username,
    email: u.email,
    password: '',
    role: u.role,
    departmentId: u.departmentId ? u.departmentId.id : null,
  }
  errorMsg.value = ''
  showForm.value = true
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  try {
    if (editingId.value) {
      const payload = {
        name: form.value.name,
        email: form.value.email,
        role: form.value.role,
        departmentId: form.value.departmentId ? { id: form.value.departmentId } : null,
      }
      await userService.update(editingId.value, payload)
    } else {
      const payload = {
        name: form.value.name,
        username: form.value.username,
        email: form.value.email,
        password: form.value.password,
        role: form.value.role,
        departmentId: form.value.departmentId ? { id: form.value.departmentId } : null,
      }
      await userService.create(payload)
    }
    showForm.value = false
    reload()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function deactivate(u) {
  if (!(await confirmDialog(`Khoá tài khoản "${u.name}"?`))) return
  await userService.deactivate(u.id)
  reload()
}

async function reactivate(u) {
  if (!(await confirmDialog(`Kích hoạt lại tài khoản "${u.name}"?`))) return
  await userService.reactivate(u.id)
  reload()
}

const importInput = ref(null)
const importing = ref(false)
const importResults = ref(null)

function openImportPicker() {
  importInput.value.click()
}

async function onImportFileChange(e) {
  const file = e.target.files[0]
  e.target.value = ''
  if (!file) return
  importing.value = true
  importResults.value = null
  try {
    const res = await userService.bulkImport(file)
    importResults.value = res.data
    reload()
  } catch (err) {
    await showError(err.response?.data || 'Nhập file thất bại.')
  } finally {
    importing.value = false
  }
}

const pointsUser = ref(null)
const pointsData = ref(null)
const pointsLoading = ref(false)

async function viewPoints(u) {
  pointsUser.value = u
  pointsData.value = null
  pointsLoading.value = true
  try {
    const res = await pointService.getUserPoints(u.id)
    pointsData.value = res.data
  } catch (err) {
    showError(err.response?.data || 'Không xem được điểm của người này.')
    pointsUser.value = null
  } finally {
    pointsLoading.value = false
  }
}

function formatDate(ms) {
  if (!ms) return ''
  return new Date(ms).toLocaleString('vi-VN')
}
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="manage-header">
        <h1>Quản lý người dùng</h1>
        <div class="manage-header-actions">
          <button v-if="auth.isAdmin" class="btn btn-secondary" :disabled="importing" @click="openImportPicker">
            {{ importing ? 'Đang nhập...' : 'Nhập từ Excel' }}
          </button>
          <input ref="importInput" type="file" accept=".xlsx" class="users-import-input" @change="onImportFileChange" />
          <button v-if="auth.isAdmin" class="btn btn-primary" @click="openCreateForm">+ Thêm người dùng</button>
        </div>
      </div>

      <div v-if="importResults" class="card users-import-results">
        <h2>Kết quả nhập file</h2>
        <div v-for="r in importResults" :key="r.row" class="users-import-row">
          <span class="users-import-row-num">Dòng {{ r.row }}</span>
          <span class="users-import-row-user">{{ r.username }}</span>
          <span class="badge" :class="r.status === 'success' ? 'badge-success' : 'badge-danger'">
            {{ r.status === 'success' ? 'Thành công' : r.message }}
          </span>
        </div>
        <button class="btn btn-secondary btn-sm" @click="importResults = null">Đóng</button>
      </div>

      <div class="users-toolbar">
        <div class="search-bar">
          <input v-model="keyword" type="text" placeholder="Tìm theo tên hoặc email..." @input="onSearchInput" />
        </div>
        <select v-model="deptFilter" class="input users-dept-select">
          <option :value="null">Tất cả phòng ban</option>
          <option value="none">Không thuộc phòng ban</option>
          <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
        </select>
      </div>

      <div v-if="showForm && auth.isAdmin" class="card inline-form">
        <h2>{{ editingId ? 'Sửa người dùng' : 'Thêm người dùng mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="users-form-grid">
          <div class="form-field">
            <label>Họ tên</label>
            <input v-model="form.name" type="text" class="input" />
          </div>
          <div class="form-field">
            <label>Username</label>
            <input v-model="form.username" type="text" class="input" :disabled="!!editingId" />
          </div>
          <div class="form-field">
            <label>Email</label>
            <input v-model="form.email" type="email" class="input" />
          </div>
          <div v-if="!editingId" class="form-field">
            <label>Mật khẩu</label>
            <input v-model="form.password" type="password" class="input" />
          </div>
          <div class="form-field">
            <label>Vai trò</label>
            <select v-model="form.role" class="input">
              <option value="EMPLOYEE">EMPLOYEE</option>
              <option value="TRAINER">TRAINER</option>
            </select>
          </div>
          <div class="form-field">
            <label>Phòng ban</label>
            <select v-model="form.departmentId" class="input">
              <option :value="null">Không thuộc phòng ban</option>
              <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
            </select>
          </div>
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">{{ editingId ? 'Lưu thay đổi' : 'Lưu người dùng' }}</button>
          <button class="btn btn-secondary" @click="showForm = false">Huỷ</button>
        </div>
      </div>

      <div v-if="pointsUser" class="card users-points-panel">
        <h2>Điểm của {{ pointsUser.name }}</h2>
        <p v-if="pointsLoading" class="state-text">Đang tải...</p>
        <template v-else-if="pointsData">
          <div class="users-points-total">Tổng điểm: <strong>{{ pointsData.totalPoints }}</strong></div>
          <div v-if="pointsData.transactions.length === 0" class="empty-state">Chưa có giao dịch điểm nào.</div>
          <div v-else class="users-points-list">
            <div v-for="t in pointsData.transactions" :key="t.id" class="users-points-row">
              <span class="users-points-reason">{{ t.reason }}</span>
              <span class="users-points-date">{{ formatDate(t.createdAt) }}</span>
              <span class="users-points-value">+{{ t.points }}</span>
            </div>
          </div>
        </template>
        <button class="btn btn-secondary btn-sm" @click="pointsUser = null">Đóng</button>
      </div>

      <div class="manage-table-wrap">
        <div class="users-table-header">
          <div>HỌ TÊN</div><div>EMAIL</div><div>PHÒNG BAN</div><div>VAI TRÒ</div><div>TRẠNG THÁI</div><div>HÀNH ĐỘNG</div>
        </div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="filteredUsers.length === 0" class="empty-state">Không tìm thấy người dùng nào.</div>
        <template v-else>
          <div v-for="u in filteredUsers" :key="u.id" class="users-row">
            <div class="users-row-name">{{ u.name }}</div>
            <div class="users-row-email">{{ u.email }}</div>
            <div class="users-row-dept">{{ u.departmentId ? u.departmentId.name : '—' }}</div>
            <div class="users-row-role">{{ u.role }}</div>
            <div>
              <span class="badge" :class="u.isActive ? 'badge-success' : 'badge-neutral'">
                {{ u.isActive ? 'Đang hoạt động' : 'Đã khoá' }}
              </span>
            </div>
            <div class="manage-row-actions">
              <span class="manage-action" @click="viewPoints(u)">Xem điểm</span>
              <span v-if="auth.isAdmin && u.role !== 'ADMIN'" class="manage-action" @click="openEditForm(u)">Sửa</span>
              <span v-if="auth.isAdmin && u.isActive" class="manage-action manage-action--danger" @click="deactivate(u)">Khoá</span>
              <span v-if="auth.isAdmin && !u.isActive" class="manage-action" @click="reactivate(u)">Kích hoạt lại</span>
            </div>
          </div>
        </template>
      </div>
      <button v-if="hasMore" class="btn btn-secondary users-load-more" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
      </button>
    </div>
  </div>
</template>

<style scoped src="./UsersManageView.css"></style>
