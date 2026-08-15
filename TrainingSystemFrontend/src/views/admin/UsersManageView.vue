<script setup>
import { ref, computed, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useAsyncData } from '@/composables/useAsyncData'
import { useLazyList } from '@/composables/useLazyList'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import FormModal from '@/components/common/FormModal.vue'
import ImportProgressOverlay from '@/components/common/ImportProgressOverlay.vue'
import userService from '@/api/userService'
import storeService from '@/api/storeService'
import chainService from '@/api/chainService'
import regionService from '@/api/regionService'
import pointService from '@/api/pointService'
import { confirmDialog, showError } from '@/utils/alerts'
import { formatDateTime as formatDate } from '@/utils/formatDate'
import { Coins, FileSpreadsheet, Lock, Pencil, Plus, Unlock } from '@lucide/vue'

const auth = useAuthStore()

const keyword = ref('')
const { items: users, loading, loadingMore, hasMore, loadMore, reload } = useLazyList(
  (page, size) => userService.getAll(keyword.value, page, size),
  20,
)
const { data: stores } = useAsyncData(() => storeService.getAll())
const { data: chains } = useAsyncData(() => chainService.getAll())
const { data: regions } = useAsyncData(() => regionService.getAll())

let debounceTimer = null
function onSearchInput() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(reload, 400)
}

const filterChainId = ref(null)
const filterRegionId = ref(null)
const filterStoreId = ref(null)

const filterRegions = computed(() => {
  const matching = filterChainId.value
    ? (stores.value || []).filter((s) => s.chainId && s.chainId.id === filterChainId.value)
    : stores.value || []
  const ids = new Set(matching.map((s) => s.regionId && s.regionId.id))
  return (regions.value || []).filter((r) => ids.has(r.id))
})

const filterStores = computed(() => {
  return (stores.value || []).filter((s) => {
    if (filterChainId.value && (!s.chainId || s.chainId.id !== filterChainId.value)) return false
    if (filterRegionId.value && (!s.regionId || s.regionId.id !== filterRegionId.value)) return false
    return true
  })
})

watch(filterChainId, () => {
  if (!filterRegions.value.some((r) => r.id === filterRegionId.value)) filterRegionId.value = null
  filterStoreId.value = null
})
watch(filterRegionId, () => {
  filterStoreId.value = null
})

const anyStoreFilterActive = computed(() => !!(filterChainId.value || filterRegionId.value || filterStoreId.value))
watch(anyStoreFilterActive, async (active) => {
  if (!active) return
  while (hasMore.value) {
    await loadMore()
  }
})

const filteredUsers = computed(() => {
  const list = users.value || []
  if (filterStoreId.value === 'none') return list.filter((u) => !u.storeId)
  if (filterStoreId.value) return list.filter((u) => u.storeId && u.storeId.id === filterStoreId.value)
  return list.filter((u) => {
    if (filterChainId.value && (!u.storeId || !u.storeId.chainId || u.storeId.chainId.id !== filterChainId.value)) return false
    if (filterRegionId.value && (!u.storeId || !u.storeId.regionId || u.storeId.regionId.id !== filterRegionId.value)) return false
    return true
  })
})

const editingId = ref(null)
const showForm = ref(false)
const form = ref({ name: '', username: '', email: '', password: '', role: 'EMPLOYEE', storeId: null })
const saving = ref(false)
const errorMsg = ref('')

const formChainId = ref(null)
const formRegionId = ref(null)

const storesForForm = computed(() => {
  return (stores.value || []).filter((s) => {
    if (formChainId.value && (!s.chainId || s.chainId.id !== formChainId.value)) return false
    if (formRegionId.value && (!s.regionId || s.regionId.id !== formRegionId.value)) return false
    return true
  })
})

watch(formChainId, () => {
  if (form.value.storeId && !storesForForm.value.some((s) => s.id === form.value.storeId)) {
    form.value.storeId = null
  }
})
watch(formRegionId, () => {
  if (form.value.storeId && !storesForForm.value.some((s) => s.id === form.value.storeId)) {
    form.value.storeId = null
  }
})

function openCreateForm() {
  editingId.value = null
  formChainId.value = null
  formRegionId.value = null
  form.value = { name: '', username: '', email: '', password: '', role: 'EMPLOYEE', storeId: null }
  errorMsg.value = ''
  showForm.value = true
}

function openEditForm(u) {
  editingId.value = u.id
  form.value = { name: '', username: '', email: '', password: '', role: 'EMPLOYEE', storeId: null }
  formChainId.value = u.storeId && u.storeId.chainId ? u.storeId.chainId.id : null
  formRegionId.value = u.storeId && u.storeId.regionId ? u.storeId.regionId.id : null
  form.value = {
    name: u.name,
    username: u.username,
    email: u.email,
    password: '',
    role: u.role,
    storeId: u.storeId ? u.storeId.id : null,
  }
  errorMsg.value = ''
  showForm.value = true
}

function closeForm() {
  showForm.value = false
  errorMsg.value = ''
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
        storeId: form.value.storeId ? { id: form.value.storeId } : null,
      }
      await userService.update(editingId.value, payload)
    } else {
      const payload = {
        name: form.value.name,
        username: form.value.username,
        email: form.value.email,
        password: form.value.password,
        role: form.value.role,
        storeId: form.value.storeId ? { id: form.value.storeId } : null,
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
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <ImportProgressOverlay :active="importing" label="Đang nhập danh sách người dùng..." />
    <div class="admin-content">
      <div class="manage-header">
        <div><h1>Người dùng</h1><p>Quản lý tài khoản, vai trò, đơn vị làm việc và trạng thái truy cập hệ thống.</p></div>
        <div class="manage-header-actions">
          <button v-if="auth.isAdmin" class="btn btn-secondary" :disabled="importing" @click="openImportPicker">
            <FileSpreadsheet :size="16" /> {{ importing ? 'Đang nhập...' : 'Nhập từ Excel' }}
          </button>
          <input ref="importInput" type="file" accept=".xlsx" class="users-import-input" @change="onImportFileChange" />
          <button v-if="auth.isAdmin" class="btn btn-primary" @click="openCreateForm"><Plus :size="16" /> Thêm người dùng</button>
        </div>
      </div>

      <div v-if="importResults" class="card users-import-results">
        <h2>Kết quả nhập file</h2>
        <div v-for="r in importResults" :key="r.row" class="users-import-row">
          <span class="users-import-row-num">Dòng {{ r.row }}</span>
          <span class="users-import-row-user">{{ r.username }}</span>
          <span v-if="r.status === 'success' && r.store" class="users-import-row-store">{{ r.store }}</span>
          <span class="badge" :class="r.status === 'success' ? 'badge-success' : 'badge-danger'">
            {{ r.status === 'success' ? 'Thành công' : r.message }}
          </span>
        </div>
        <button class="btn btn-secondary btn-sm" @click="importResults = null">Đóng</button>
      </div>

      <div class="users-toolbar admin-toolbar">
        <div class="search-bar">
          <input v-model="keyword" type="text" placeholder="Tìm theo tên hoặc email..." @input="onSearchInput" />
        </div>
        <select v-model="filterChainId" class="input users-dept-select">
          <option :value="null">Tất cả chuỗi</option>
          <option v-for="c in chains" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <select v-model="filterRegionId" class="input users-dept-select">
          <option :value="null">Tất cả vùng</option>
          <option v-for="r in filterRegions" :key="r.id" :value="r.id">{{ r.name }}</option>
        </select>
        <select v-model="filterStoreId" class="input users-dept-select">
          <option :value="null">Tất cả siêu thị</option>
          <option value="none">Không thuộc siêu thị</option>
          <option v-for="s in filterStores" :key="s.id" :value="s.id">{{ s.name }}</option>
        </select>
        <span class="admin-toolbar-meta">{{ filteredUsers.length }} người đang hiển thị</span>
      </div>

      <FormModal
        v-if="showForm && auth.isAdmin"
        size="large"
        :title="editingId ? 'Chỉnh sửa người dùng' : 'Thêm người dùng mới'"
        description="Thiết lập thông tin đăng nhập, vai trò và siêu thị làm việc."
        :submit-label="editingId ? 'Lưu thay đổi' : 'Thêm người dùng'"
        :saving="saving"
        :disabled="!form.name.trim() || !form.email.trim() || (!editingId && (!form.username.trim() || !form.password)) || (form.role === 'EMPLOYEE' && !form.storeId)"
        @close="closeForm"
        @submit="submit"
      >
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <section class="admin-form-section">
          <div class="admin-form-section-heading"><h3>Thông tin tài khoản</h3><p>Dùng để đăng nhập và nhận thông báo từ hệ thống.</p></div>
          <div class="admin-form-grid admin-form-grid--three">
          <div class="form-field">
            <label for="user-name">Họ tên</label>
            <input id="user-name" v-model="form.name" type="text" class="input" required />
          </div>
          <div class="form-field">
            <label for="user-username">Tên đăng nhập</label>
            <input id="user-username" v-model="form.username" type="text" class="input" :disabled="!!editingId" :required="!editingId" />
          </div>
          <div class="form-field">
            <label for="user-email">Email</label>
            <input id="user-email" v-model="form.email" type="email" class="input" required />
          </div>
          <div v-if="!editingId" class="form-field">
            <label for="user-password">Mật khẩu ban đầu</label>
            <input id="user-password" v-model="form.password" type="password" class="input" required />
          </div></div>
        </section>
        <section class="admin-form-section">
          <div class="admin-form-section-heading"><h3>Vai trò và đơn vị</h3><p>Quyết định quyền thao tác và phạm vi dữ liệu người dùng có thể xem.</p></div>
          <div class="admin-form-grid">
          <div class="form-field">
            <label for="user-role">Vai trò</label>
            <select id="user-role" v-model="form.role" class="input">
              <option value="EMPLOYEE">Nhân viên</option>
              <option value="TRAINER">Trainer</option>
            </select>
          </div>
          <div class="form-field">
            <label for="user-store-chain">Chuỗi<span class="form-label-optional"> (lọc)</span></label>
            <select id="user-store-chain" v-model="formChainId" class="input">
              <option :value="null">Tất cả chuỗi</option>
              <option v-for="c in chains" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-field">
            <label for="user-store-region">Vùng<span class="form-label-optional"> (lọc)</span></label>
            <select id="user-store-region" v-model="formRegionId" class="input">
              <option :value="null">Tất cả vùng</option>
              <option v-for="r in regions" :key="r.id" :value="r.id">{{ r.name }}</option>
            </select>
          </div>
          <div class="form-field">
            <label for="user-store">Siêu thị làm việc<span v-if="form.role === 'EMPLOYEE'" class="form-label-optional"> (bắt buộc)</span></label>
            <select id="user-store" v-model="form.storeId" class="input">
              <option :value="null">Không thuộc siêu thị</option>
              <option v-for="s in storesForForm" :key="s.id" :value="s.id">{{ s.name }}</option>
            </select>
            <small v-if="form.role === 'EMPLOYEE'" class="form-help">Nhân viên bắt buộc phải thuộc một siêu thị.</small>
            <small v-else class="form-help">Trainer để trống sẽ phụ trách toàn tập đoàn.</small>
          </div></div>
        </section>
      </FormModal>

      <FormModal
        v-if="pointsUser"
        size="large"
        :title="`Lịch sử điểm · ${pointsUser.name}`"
        description="Theo dõi tổng điểm và các giao dịch đã ghi nhận cho người dùng này."
        hide-submit
        @close="pointsUser = null"
      >
        <p v-if="pointsLoading" class="state-text">Đang tải...</p>
        <template v-else-if="pointsData">
          <div class="users-points-total">Tổng điểm hiện tại: <strong>{{ pointsData.totalPoints }}</strong></div>
          <div v-if="pointsData.transactions.length === 0" class="empty-state">Chưa có giao dịch điểm nào.</div>
          <div v-else class="users-points-list">
            <div v-for="t in pointsData.transactions" :key="t.id" class="users-points-row">
              <span class="users-points-reason">{{ t.reason }}</span>
              <span class="users-points-date">{{ formatDate(t.createdAt) }}</span>
              <span class="users-points-value">+{{ t.points }}</span>
            </div>
          </div>
        </template>
        <template #footer><button type="button" class="btn btn-secondary" @click="pointsUser = null">Đóng</button></template>
      </FormModal>

      <div class="manage-table-wrap">
        <div class="admin-panel-heading"><div><h2>Danh sách người dùng</h2><p>Trạng thái tài khoản và quyền đang được cấp.</p></div><span class="admin-panel-count">{{ filteredUsers.length }} người</span></div>
        <div class="users-table-header">
          <div>HỌ TÊN</div><div>EMAIL</div><div>SIÊU THỊ</div><div>VAI TRÒ</div><div>TRẠNG THÁI</div><div>HÀNH ĐỘNG</div>
        </div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="filteredUsers.length === 0" class="empty-state">Không tìm thấy người dùng nào.</div>
        <template v-else>
          <div v-for="u in filteredUsers" :key="u.id" class="users-row">
            <div class="users-row-name">{{ u.name }}</div>
            <div class="users-row-email">{{ u.email }}</div>
            <div class="users-row-dept">{{ u.storeId ? u.storeId.name : '—' }}</div>
            <div class="users-row-role">{{ u.role }}</div>
            <div>
              <span class="badge" :class="u.isActive ? 'badge-success' : 'badge-neutral'">
                {{ u.isActive ? 'Đang hoạt động' : 'Đã khoá' }}
              </span>
            </div>
            <div class="row-action-group">
              <button class="row-action-btn" @click="viewPoints(u)"><Coins :size="13" /> Xem điểm</button>
              <button v-if="auth.isAdmin && u.role !== 'ADMIN'" class="row-action-btn" @click="openEditForm(u)"><Pencil :size="13" /> Sửa</button>
              <button v-if="auth.isAdmin && u.role !== 'ADMIN' && u.isActive" class="row-action-btn row-action-btn--danger" @click="deactivate(u)"><Lock :size="13" /> Khoá</button>
              <button v-if="auth.isAdmin && u.role !== 'ADMIN' && !u.isActive" class="row-action-btn" @click="reactivate(u)"><Unlock :size="13" /> Kích hoạt lại</button>
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
