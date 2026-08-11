<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import pointRuleService from '@/api/pointRuleService'
import { confirmDialog } from '@/utils/alerts'
import { Pencil, Trash2 } from '@lucide/vue'

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
        <h1>Quy tắc tính điểm</h1>
        <button class="btn btn-primary" @click="openCreateForm">+ Thêm quy tắc</button>
      </div>

      <div v-if="showForm" class="card inline-form">
        <h2>{{ editingId ? 'Sửa quy tắc' : 'Thêm quy tắc mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Loại hành động</label>
          <input v-model="form.actionType" type="text" class="input" placeholder="VD: TEST_PASSED" />
        </div>
        <div class="form-field">
          <label>Điểm</label>
          <input v-model="form.points" type="number" class="input" />
        </div>
        <div class="form-field">
          <label>Mô tả</label>
          <textarea v-model="form.description" class="input" rows="2" placeholder="Mô tả ngắn gọn..."></textarea>
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">Lưu</button>
          <button class="btn btn-secondary" @click="showForm = false">Huỷ</button>
        </div>
      </div>

      <div class="manage-table-wrap">
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
