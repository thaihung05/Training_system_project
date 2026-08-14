<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import FormModal from '@/components/common/FormModal.vue'
import regionService from '@/api/regionService'
import storeService from '@/api/storeService'
import { confirmDialog, showError } from '@/utils/alerts'
import { Pencil, Plus, Trash2 } from '@lucide/vue'

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

function closeForm() {
  showForm.value = false
  errorMsg.value = ''
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
        <div><h1>Vùng quản lý</h1><p>Thiết lập các khu vực dùng để phân quyền, chọn phạm vi và lọc siêu thị.</p></div>
        <button class="btn btn-primary" @click="openCreateForm"><Plus :size="16" /> Thêm vùng</button>
      </div>

      <FormModal
        v-if="showForm"
        size="small"
        :title="editingId ? 'Chỉnh sửa vùng' : 'Thêm vùng mới'"
        description="Tên vùng nên thống nhất với cách doanh nghiệp đang phân chia khu vực vận hành."
        :submit-label="editingId ? 'Lưu thay đổi' : 'Thêm vùng'"
        :saving="saving"
        :disabled="!form.name.trim()"
        @close="closeForm"
        @submit="submit"
      >
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label for="region-name">Tên vùng</label>
          <input id="region-name" v-model="form.name" type="text" class="input" placeholder="Ví dụ: Thành phố Hồ Chí Minh" required />
        </div>
      </FormModal>

      <section class="admin-data-panel">
        <div class="admin-panel-heading"><div><h2>Danh sách vùng</h2><p>Số siêu thị đang hoạt động trong từng vùng.</p></div><span class="admin-panel-count">{{ regions?.length ?? 0 }} vùng</span></div>
        <div class="admin-org-table-header"><div>Tên vùng</div><div>Siêu thị</div><div>Hành động</div></div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="regions.length === 0" class="empty-state">Chưa có vùng nào. Hãy thêm vùng trước khi tạo siêu thị.</div>
        <div v-for="r in regions" v-else :key="r.id" class="admin-org-row">
          <div><div class="admin-org-name">{{ r.name }}</div><div class="admin-org-detail">Mã hệ thống #{{ r.id }}</div></div>
          <div class="admin-org-value">{{ storeCountFor(r.id) }}</div>
          <div class="row-action-group">
            <button class="row-action-btn" @click="openEditForm(r)"><Pencil :size="13" /> Sửa</button>
            <button class="row-action-btn row-action-btn--danger" @click="remove(r)"><Trash2 :size="13" /> Xoá</button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped src="./OrgManageView.css"></style>
