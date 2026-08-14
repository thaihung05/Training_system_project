<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import FormModal from '@/components/common/FormModal.vue'
import chainService from '@/api/chainService'
import storeService from '@/api/storeService'
import { confirmDialog, showError } from '@/utils/alerts'
import { Pencil, Plus, Trash2 } from '@lucide/vue'

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

function closeForm() {
  showForm.value = false
  errorMsg.value = ''
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
        <div><h1>Chuỗi bán lẻ</h1><p>Quản lý các thương hiệu hoặc chuỗi đang vận hành trong hệ thống đào tạo.</p></div>
        <button class="btn btn-primary" @click="openCreateForm"><Plus :size="16" /> Thêm chuỗi</button>
      </div>

      <FormModal
        v-if="showForm"
        size="small"
        :title="editingId ? 'Chỉnh sửa chuỗi' : 'Thêm chuỗi mới'"
        description="Tên chuỗi được dùng khi phân phạm vi khóa học và lọc siêu thị."
        :submit-label="editingId ? 'Lưu thay đổi' : 'Thêm chuỗi'"
        :saving="saving"
        :disabled="!form.name.trim()"
        @close="closeForm"
        @submit="submit"
      >
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label for="chain-name">Tên chuỗi</label>
          <input id="chain-name" v-model="form.name" type="text" class="input" placeholder="Ví dụ: Điện Máy Xanh" required />
        </div>
      </FormModal>

      <section class="admin-data-panel">
        <div class="admin-panel-heading"><div><h2>Danh sách chuỗi</h2><p>Số siêu thị đang gắn với từng chuỗi.</p></div><span class="admin-panel-count">{{ chains?.length ?? 0 }} chuỗi</span></div>
        <div class="admin-org-table-header"><div>Tên chuỗi</div><div>Siêu thị</div><div>Hành động</div></div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="chains.length === 0" class="empty-state">Chưa có chuỗi nào. Hãy thêm chuỗi trước khi tạo siêu thị.</div>
        <div v-for="c in chains" v-else :key="c.id" class="admin-org-row">
          <div><div class="admin-org-name">{{ c.name }}</div><div class="admin-org-detail">Mã hệ thống #{{ c.id }}</div></div>
          <div class="admin-org-value">{{ storeCountFor(c.id) }}</div>
          <div class="row-action-group">
            <button class="row-action-btn" @click="openEditForm(c)"><Pencil :size="13" /> Sửa</button>
            <button class="row-action-btn row-action-btn--danger" @click="remove(c)"><Trash2 :size="13" /> Xoá</button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped src="./OrgManageView.css"></style>
