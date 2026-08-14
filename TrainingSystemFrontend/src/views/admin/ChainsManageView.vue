<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import chainService from '@/api/chainService'
import storeService from '@/api/storeService'
import { confirmDialog, showError } from '@/utils/alerts'
import { Pencil, Trash2 } from '@lucide/vue'

const { data: chains, loading, refresh } = useAsyncData(() => chainService.getAll())
const { data: stores } = useAsyncData(() => storeService.getAll())

function storeCountFor(chainId) {
  return (stores.value || []).filter((s) => s.chainId && s.chainId.id === chainId).length
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

function openEditForm(c) {
  editingId.value = c.id
  form.value = { name: c.name }
  errorMsg.value = ''
  showForm.value = true
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  try {
    if (editingId.value) {
      await chainService.update(editingId.value, { name: form.value.name })
    } else {
      await chainService.create({ name: form.value.name })
    }
    showForm.value = false
    refresh()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function remove(c) {
  if (!(await confirmDialog(`Xoá chuỗi "${c.name}"?`))) return
  try {
    await chainService.remove(c.id)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá chuỗi này.')
  }
}
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="manage-header">
        <h1>Chuỗi</h1>
        <button class="btn btn-primary" @click="openCreateForm">+ Thêm chuỗi</button>
      </div>

      <div v-if="showForm" class="card inline-form">
        <h2>{{ editingId ? 'Sửa chuỗi' : 'Thêm chuỗi mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Tên chuỗi</label>
          <input v-model="form.name" type="text" class="input" placeholder="VD: Điện Máy Xanh" />
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">Lưu</button>
          <button class="btn btn-secondary" @click="showForm = false">Huỷ</button>
        </div>
      </div>

      <p v-if="loading" class="state-text">Đang tải...</p>
      <div v-else-if="chains.length === 0" class="empty-state">Chưa có chuỗi nào.</div>
      <div v-else class="dept-grid">
        <div v-for="c in chains" :key="c.id" class="card dept-card">
          <div class="dept-name">{{ c.name }}</div>
          <div class="dept-stats">
            <div>
              <div class="dept-stat-label">Siêu thị</div>
              <div class="dept-stat-value">{{ storeCountFor(c.id) }}</div>
            </div>
          </div>
          <div class="row-action-group dept-actions">
            <button class="row-action-btn" @click="openEditForm(c)"><Pencil :size="13" /> Sửa</button>
            <button class="row-action-btn row-action-btn--danger" @click="remove(c)"><Trash2 :size="13" /> Xoá</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./OrgManageView.css"></style>
