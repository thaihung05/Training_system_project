<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import FormModal from '@/components/common/FormModal.vue'
import pointRuleService from '@/api/pointRuleService'
import { confirmDialog } from '@/utils/alerts'
import { Pencil, Plus, Trash2 } from '@lucide/vue'

const { data: rules, loading, refresh } = useAsyncData(() => pointRuleService.getAll())

const editingId = ref(null)
const form = ref({ actionType: '', points: 0, description: '' })
const saving = ref(false)
const errorMsg = ref('')
const showForm = ref(false)

function openCreateForm() {
  editingId.value = null
  form.value = { actionType: '', points: 0, description: '' }
  errorMsg.value = ''
  showForm.value = true
}

function openEditForm(r) {
  editingId.value = r.id
  form.value = { actionType: r.actionType, points: r.points, description: r.description || '' }
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
    actionType: form.value.actionType,
    points: Number(form.value.points),
    description: form.value.description,
  }
  try {
    if (editingId.value) {
      await pointRuleService.update(editingId.value, payload)
    } else {
      await pointRuleService.create(payload)
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
  if (!(await confirmDialog(`Xoá quy tắc "${r.actionType}"? Hành động này sẽ không còn được cộng điểm nữa.`))) return
  await pointRuleService.remove(r.id)
  refresh()
}
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="manage-header">
        <div><h1>Quy tắc tính điểm</h1><p>Cấu hình số điểm hệ thống trao cho từng hành động học tập.</p></div>
        <button class="btn btn-primary" @click="openCreateForm"><Plus :size="16" /> Thêm quy tắc</button>
      </div>

      <FormModal
        v-if="showForm"
        :title="editingId ? 'Chỉnh sửa quy tắc điểm' : 'Thêm quy tắc điểm'"
        description="Mã hành động phải khớp với sự kiện mà hệ thống đang phát sinh."
        :submit-label="editingId ? 'Lưu thay đổi' : 'Thêm quy tắc'"
        :saving="saving"
        :disabled="!form.actionType.trim() || Number(form.points) < 0"
        @close="closeForm"
        @submit="submit"
      >
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="admin-form-grid">
          <div class="form-field">
            <label for="rule-action">Mã hành động</label>
            <input id="rule-action" v-model="form.actionType" type="text" class="input" placeholder="Ví dụ: TEST_PASSED" required />
          </div>
          <div class="form-field">
            <label for="rule-points">Số điểm</label>
            <input id="rule-points" v-model="form.points" type="number" min="0" class="input" required />
            <small class="form-help">Chỉ áp dụng cho giao dịch mới, không tính lại lịch sử điểm.</small>
          </div>
          <div class="form-field admin-form-span">
            <label for="rule-description">Mô tả</label>
            <textarea id="rule-description" v-model="form.description" class="input" rows="3" placeholder="Giải thích khi nào điểm được trao."></textarea>
          </div>
        </div>
      </FormModal>

      <div class="manage-table-wrap">
        <div class="admin-panel-heading"><div><h2>Danh sách quy tắc</h2><p>Các hành động đang được quy đổi thành điểm thưởng.</p></div><span class="admin-panel-count">{{ rules?.length ?? 0 }} quy tắc</span></div>
        <div class="rules-table-header">
          <div>LOẠI HÀNH ĐỘNG</div><div>ĐIỂM</div><div>MÔ TẢ</div><div>HÀNH ĐỘNG</div>
        </div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="rules.length === 0" class="empty-state">Chưa có quy tắc nào.</div>
        <template v-else>
          <div v-for="r in rules" :key="r.id" class="rules-row">
            <div class="rules-row-action">{{ r.actionType }}</div>
            <div class="rules-row-points">+{{ r.points }}</div>
            <div class="rules-row-desc">{{ r.description }}</div>
            <div class="row-action-group">
              <button class="row-action-btn" @click="openEditForm(r)"><Pencil :size="13" /> Sửa</button>
              <button class="row-action-btn row-action-btn--danger" @click="remove(r)"><Trash2 :size="13" /> Xoá</button>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped src="./PointRulesManageView.css"></style>
