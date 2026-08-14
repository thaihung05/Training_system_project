<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import regionService from '@/api/regionService'
import storeService from '@/api/storeService'
import { confirmDialog, showError } from '@/utils/alerts'
import { Pencil, Trash2 } from '@lucide/vue'

const { data: regions, loading, refresh } = useAsyncData(() => regionService.getAll())
const { data: stores } = useAsyncData(() => storeService.getAll())

function storeCountFor(regionId) {
  return (stores.value || []).filter((s) => s.regionId && s.regionId.id === regionId).length
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

function openEditForm(r) {
  editingId.value = r.id
  form.value = { name: r.name }
  errorMsg.value = ''
  showForm.value = true
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  try {
    if (editingId.value) {
      await regionService.update(editingId.value, { name: form.value.name })
    } else {
      await regionService.create({ name: form.value.name })
    }
    showForm.value = false
    refresh()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function remove(r) {
  if (!(await confirmDialog(`Xoá vùng "${r.name}"?`))) return
  try {
    await regionService.remove(r.id)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá vùng này.')
  }
}
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="manage-header">
        <h1>Vùng</h1>
        <button class="btn btn-primary" @click="openCreateForm">+ Thêm vùng</button>
      </div>

      <div v-if="showForm" class="card inline-form">
        <h2>{{ editingId ? 'Sửa vùng' : 'Thêm vùng mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Tên vùng</label>
          <input v-model="form.name" type="text" class="input" placeholder="VD: Hồ Chí Minh" />
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">Lưu</button>
          <button class="btn btn-secondary" @click="showForm = false">Huỷ</button>
        </div>
      </div>

      <p v-if="loading" class="state-text">Đang tải...</p>
      <div v-else-if="regions.length === 0" class="empty-state">Chưa có vùng nào.</div>
      <div v-else class="dept-grid">
        <div v-for="r in regions" :key="r.id" class="card dept-card">
          <div class="dept-name">{{ r.name }}</div>
          <div class="dept-stats">
            <div>
              <div class="dept-stat-label">Siêu thị</div>
              <div class="dept-stat-value">{{ storeCountFor(r.id) }}</div>
            </div>
          </div>
          <div class="row-action-group dept-actions">
            <button class="row-action-btn" @click="openEditForm(r)"><Pencil :size="13" /> Sửa</button>
            <button class="row-action-btn row-action-btn--danger" @click="remove(r)"><Trash2 :size="13" /> Xoá</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./OrgManageView.css"></style>
