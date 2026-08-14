<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import FormModal from '@/components/common/FormModal.vue'
import storeService from '@/api/storeService'
import chainService from '@/api/chainService'
import regionService from '@/api/regionService'
import userService from '@/api/userService'
import { confirmDialog, showError } from '@/utils/alerts'
import { Pencil, Plus, Trash2 } from '@lucide/vue'

const { data: stores, loading, refresh } = useAsyncData(() => storeService.getAll())
const { data: chains } = useAsyncData(() => chainService.getAll())
const { data: regions } = useAsyncData(() => regionService.getAll())
const { data: users } = useAsyncData(() => userService.getAll())

function employeeCountFor(storeId) {
  return (users.value || []).filter((u) => u.storeId && u.storeId.id === storeId).length
}

const editingId = ref(null)
const form = ref({ name: '', chainId: null, regionId: null })
const saving = ref(false)
const errorMsg = ref('')
const showForm = ref(false)

function openCreateForm() {
  editingId.value = null
  form.value = { name: '', chainId: null, regionId: null }
  errorMsg.value = ''
  showForm.value = true
}

function openEditForm(s) {
  editingId.value = s.id
  form.value = {
    name: s.name,
    chainId: s.chainId ? s.chainId.id : null,
    regionId: s.regionId ? s.regionId.id : null,
  }
  errorMsg.value = ''
  showForm.value = true
}

function closeForm() {
  showForm.value = false
  errorMsg.value = ''
}

function buildPayload() {
  return {
    name: form.value.name,
    chainId: form.value.chainId ? { id: form.value.chainId } : null,
    regionId: form.value.regionId ? { id: form.value.regionId } : null,
  }
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  try {
    if (editingId.value) {
      await storeService.update(editingId.value, buildPayload())
    } else {
      await storeService.create(buildPayload())
    }
    showForm.value = false
    refresh()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function remove(s) {
  if (!(await confirmDialog(`Xoá siêu thị "${s.name}"?`))) return
  try {
    await storeService.remove(s.id)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá siêu thị này.')
  }
}
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="manage-header">
        <div><h1>Siêu thị</h1><p>Quản lý đơn vị làm việc và mối liên kết giữa siêu thị, chuỗi, vùng.</p></div>
        <button class="btn btn-primary" @click="openCreateForm"><Plus :size="16" /> Thêm siêu thị</button>
      </div>

      <FormModal
        v-if="showForm"
        :title="editingId ? 'Chỉnh sửa siêu thị' : 'Thêm siêu thị mới'"
        description="Mỗi siêu thị phải thuộc một Chuỗi và một Vùng để hệ thống lọc khóa học, trainer và nhân viên chính xác."
        :submit-label="editingId ? 'Lưu thay đổi' : 'Thêm siêu thị'"
        :saving="saving"
        :disabled="!form.name.trim() || !form.chainId || !form.regionId"
        @close="closeForm"
        @submit="submit"
      >
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="admin-form-grid">
          <div class="form-field admin-form-span">
            <label for="store-name">Tên siêu thị</label>
            <input id="store-name" v-model="form.name" type="text" class="input" placeholder="Ví dụ: Điện Máy Xanh Nguyễn Trãi" required />
          </div>
          <div class="form-field">
            <label for="store-chain">Chuỗi</label>
            <select id="store-chain" v-model="form.chainId" class="input" required>
              <option :value="null" disabled>Chọn chuỗi</option>
              <option v-for="c in chains" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-field">
            <label for="store-region">Vùng</label>
            <select id="store-region" v-model="form.regionId" class="input" required>
              <option :value="null" disabled>Chọn vùng</option>
              <option v-for="r in regions" :key="r.id" :value="r.id">{{ r.name }}</option>
            </select>
          </div>
        </div>
        <p class="form-help">Chuỗi và Vùng được dùng để lọc khóa học và nhân viên phù hợp với siêu thị.</p>
      </FormModal>

      <section class="admin-data-panel">
        <div class="admin-panel-heading"><div><h2>Danh sách siêu thị</h2><p>Kiểm tra nhanh cơ cấu và số nhân viên tại từng đơn vị.</p></div><span class="admin-panel-count">{{ stores?.length ?? 0 }} siêu thị</span></div>
        <div class="admin-org-table-header admin-org-store-header"><div>Tên siêu thị</div><div>Chuỗi</div><div>Vùng</div><div>Nhân viên</div><div>Hành động</div></div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="stores.length === 0" class="empty-state">Chưa có siêu thị nào.</div>
        <div v-for="s in stores" v-else :key="s.id" class="admin-org-row admin-org-store-row">
          <div><div class="admin-org-name">{{ s.name }}</div><div class="admin-org-detail">Mã hệ thống #{{ s.id }}</div></div>
          <div class="admin-org-detail">{{ s.chainId ? s.chainId.name : 'Chưa gán' }}</div>
          <div class="admin-org-detail">{{ s.regionId ? s.regionId.name : 'Chưa gán' }}</div>
          <div class="admin-org-value">{{ employeeCountFor(s.id) }}</div>
          <div class="row-action-group">
            <button class="row-action-btn" @click="openEditForm(s)"><Pencil :size="13" /> Sửa</button>
            <button class="row-action-btn row-action-btn--danger" @click="remove(s)"><Trash2 :size="13" /> Xoá</button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped src="./OrgManageView.css"></style>
