<script setup>
import { ref } from 'vue'
import { Award, Pencil, Trash2 } from '@lucide/vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import badgeService from '@/api/badgeService'
import { confirmDialog, showError } from '@/utils/alerts'

const { data: badges, loading, refresh } = useAsyncData(() => badgeService.getAll())

const editingId = ref(null)
const form = ref({ code: '', name: '', description: '', iconUrl: '' })
const saving = ref(false)
const errorMsg = ref('')
const showForm = ref(false)

function openCreateForm() {
  editingId.value = null
  form.value = { code: '', name: '', description: '', iconUrl: '' }
  errorMsg.value = ''
  showForm.value = true
}

function openEditForm(b) {
  editingId.value = b.id
  form.value = { code: b.code, name: b.name, description: b.description || '', iconUrl: b.iconUrl || '' }
  errorMsg.value = ''
  showForm.value = true
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  const payload = {
    code: form.value.code,
    name: form.value.name,
    description: form.value.description,
    iconUrl: form.value.iconUrl,
  }
  try {
    if (editingId.value) {
      await badgeService.update(editingId.value, payload)
    } else {
      await badgeService.create(payload)
    }
    showForm.value = false
    refresh()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function remove(b) {
  if (!(await confirmDialog(`Xoá huy hiệu "${b.name}"? Những ai đã đạt huy hiệu này cũng sẽ mất.`))) return
  try {
    await badgeService.remove(b.id)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá huy hiệu này.')
  }
}
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="manage-header">
        <h1>Huy hiệu</h1>
        <button class="btn btn-primary" @click="openCreateForm">+ Thêm huy hiệu</button>
      </div>

      <div v-if="showForm" class="card inline-form">
        <h2>{{ editingId ? 'Sửa huy hiệu' : 'Thêm huy hiệu mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Mã huy hiệu</label>
          <input v-model="form.code" type="text" class="input" placeholder="VD: CERT_5" />
        </div>
        <div class="form-field">
          <label>Tên huy hiệu</label>
          <input v-model="form.name" type="text" class="input" placeholder="VD: Học viên chăm chỉ" />
        </div>
        <div class="form-field">
          <label>Mô tả</label>
          <textarea v-model="form.description" class="input" rows="2"></textarea>
        </div>
        <div class="form-field">
          <label>Link icon (tuỳ chọn)</label>
          <input v-model="form.iconUrl" type="text" class="input" placeholder="https://..." />
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">Lưu</button>
          <button class="btn btn-secondary" @click="showForm = false">Huỷ</button>
        </div>
      </div>

      <p v-if="loading" class="state-text">Đang tải...</p>
      <div v-else-if="badges.length === 0" class="empty-state">Chưa có huy hiệu nào.</div>
      <div v-else class="badge-manage-grid">
        <div v-for="b in badges" :key="b.id" class="card badge-manage-card">
          <div class="badge-icon"><Award :size="20" /></div>
          <div class="badge-manage-name">{{ b.name }}</div>
          <div class="badge-manage-code">{{ b.code }}</div>
          <div class="badge-manage-desc">{{ b.description }}</div>
          <div class="row-action-group badge-manage-actions">
            <button class="row-action-btn" @click="openEditForm(b)"><Pencil :size="13" /> Sửa</button>
            <button class="row-action-btn row-action-btn--danger" @click="remove(b)"><Trash2 :size="13" /> Xoá</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./BadgesManageView.css"></style>
