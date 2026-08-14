<script setup>
import { ref } from 'vue'
import { Award, Pencil, Plus, Trash2 } from '@lucide/vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import FormModal from '@/components/common/FormModal.vue'
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

function closeForm() {
  showForm.value = false
  errorMsg.value = ''
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
        <div><h1>Huy hiệu</h1><p>Quản lý danh hiệu ghi nhận thành tích học tập của nhân viên.</p></div>
        <button class="btn btn-primary" @click="openCreateForm"><Plus :size="16" /> Thêm huy hiệu</button>
      </div>

      <FormModal
        v-if="showForm"
        :title="editingId ? 'Chỉnh sửa huy hiệu' : 'Thêm huy hiệu mới'"
        description="Đặt mã ổn định và tên ngắn gọn để huy hiệu dễ nhận biết trong hồ sơ nhân viên."
        :submit-label="editingId ? 'Lưu thay đổi' : 'Thêm huy hiệu'"
        :saving="saving"
        :disabled="!form.code.trim() || !form.name.trim()"
        @close="closeForm"
        @submit="submit"
      >
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="admin-form-grid">
          <div class="form-field">
            <label for="badge-code">Mã huy hiệu</label>
            <input id="badge-code" v-model="form.code" type="text" class="input" placeholder="Ví dụ: CERT_5" required />
          </div>
          <div class="form-field">
            <label for="badge-name">Tên huy hiệu</label>
            <input id="badge-name" v-model="form.name" type="text" class="input" placeholder="Ví dụ: Học viên chăm chỉ" required />
          </div>
          <div class="form-field admin-form-span">
            <label for="badge-description">Mô tả</label>
            <textarea id="badge-description" v-model="form.description" class="input" rows="3" placeholder="Điều kiện hoặc ý nghĩa của huy hiệu."></textarea>
          </div>
          <div class="form-field admin-form-span">
            <label for="badge-icon">Đường dẫn biểu tượng <span class="form-label-optional">(không bắt buộc)</span></label>
            <input id="badge-icon" v-model="form.iconUrl" type="url" class="input" placeholder="https://..." />
          </div>
        </div>
      </FormModal>

      <section class="admin-data-panel badge-manage-panel">
        <div class="admin-panel-heading"><div><h2>Danh mục huy hiệu</h2><p>Các danh hiệu hiện có trong hệ thống.</p></div><span class="admin-panel-count">{{ badges?.length ?? 0 }} huy hiệu</span></div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="badges.length === 0" class="empty-state">Chưa có huy hiệu nào.</div>
        <div v-else class="badge-manage-grid">
        <div v-for="b in badges" :key="b.id" class="card badge-manage-card">
          <div class="badge-icon">
            <img v-if="b.iconUrl" :src="b.iconUrl" :alt="b.name" />
            <Award v-else :size="20" />
          </div>
          <div class="badge-manage-name">{{ b.name }}</div>
          <div class="badge-manage-code">{{ b.code }}</div>
          <div class="badge-manage-desc">{{ b.description }}</div>
          <div class="row-action-group badge-manage-actions">
            <button class="row-action-btn" @click="openEditForm(b)"><Pencil :size="13" /> Sửa</button>
            <button class="row-action-btn row-action-btn--danger" @click="remove(b)"><Trash2 :size="13" /> Xoá</button>
          </div>
        </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped src="./BadgesManageView.css"></style>
