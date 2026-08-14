<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import storeService from '@/api/storeService'
import chainService from '@/api/chainService'
import regionService from '@/api/regionService'
import userService from '@/api/userService'
import { confirmDialog, showError } from '@/utils/alerts'
import { Pencil, Trash2 } from '@lucide/vue'

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
        <h1>Siêu thị</h1>
        <button class="btn btn-primary" @click="openCreateForm">+ Thêm siêu thị</button>
      </div>

      <div v-if="showForm" class="card inline-form">
        <h2>{{ editingId ? 'Sửa siêu thị' : 'Thêm siêu thị mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Tên siêu thị</label>
          <input v-model="form.name" type="text" class="input" placeholder="VD: Điện Máy Xanh Nguyễn Trãi" />
        </div>
        <div class="form-field">
          <label>Chuỗi</label>
          <select v-model="form.chainId" class="input">
            <option :value="null" disabled>-- Chọn chuỗi --</option>
            <option v-for="c in chains" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>
        <div class="form-field">
          <label>Vùng</label>
          <select v-model="form.regionId" class="input">
            <option :value="null" disabled>-- Chọn vùng --</option>
            <option v-for="r in regions" :key="r.id" :value="r.id">{{ r.name }}</option>
          </select>
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">Lưu</button>
          <button class="btn btn-secondary" @click="showForm = false">Huỷ</button>
        </div>
      </div>

      <p v-if="loading" class="state-text">Đang tải...</p>
      <div v-else-if="stores.length === 0" class="empty-state">Chưa có siêu thị nào.</div>
      <div v-else class="dept-grid">
        <div v-for="s in stores" :key="s.id" class="card dept-card">
          <div class="dept-name">{{ s.name }}</div>
          <div class="store-tags">
            <span class="badge">{{ s.chainId ? s.chainId.name : '—' }}</span>
            <span class="badge">{{ s.regionId ? s.regionId.name : '—' }}</span>
          </div>
          <div class="dept-stats">
            <div>
              <div class="dept-stat-label">Nhân viên</div>
              <div class="dept-stat-value">{{ employeeCountFor(s.id) }}</div>
            </div>
          </div>
          <div class="row-action-group dept-actions">
            <button class="row-action-btn" @click="openEditForm(s)"><Pencil :size="13" /> Sửa</button>
            <button class="row-action-btn row-action-btn--danger" @click="remove(s)"><Trash2 :size="13" /> Xoá</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./OrgManageView.css"></style>
