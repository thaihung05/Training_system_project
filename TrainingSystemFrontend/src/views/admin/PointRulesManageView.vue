<script setup>
import { computed, ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import FormModal from '@/components/common/FormModal.vue'
import pointRuleService from '@/api/pointRuleService'
import { Pencil } from '@lucide/vue'

const { data: rules, loading, refresh } = useAsyncData(() => pointRuleService.getAll())

const ruleDefinitions = {
  LESSON_COMPLETED: {
    label: 'Hoàn thành bài học',
    detail: 'Trao một lần khi nhân viên hoàn tất một bài học.',
    order: 1,
  },
  TEST_PASSED: {
    label: 'Vượt qua bài kiểm tra',
    detail: 'Trao ở lần đầu nhân viên đạt yêu cầu của bài kiểm tra.',
    order: 2,
  },
  COURSE_COMPLETED: {
    label: 'Hoàn thành khóa học',
    detail: 'Trao khi nhân viên hoàn tất khóa học và đủ điều kiện nhận chứng chỉ.',
    order: 3,
  },
}

const systemRules = computed(() =>
  (rules.value || [])
    .filter((rule) => ruleDefinitions[rule.actionType])
    .sort((first, second) => ruleDefinitions[first.actionType].order - ruleDefinitions[second.actionType].order),
)

const missingRuleCount = computed(() => Object.keys(ruleDefinitions).length - systemRules.value.length)

const editingId = ref(null)
const form = ref({ actionType: '', points: 0, description: '' })
const saving = ref(false)
const errorMsg = ref('')
const showForm = ref(false)

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
    await pointRuleService.update(editingId.value, payload)
    showForm.value = false
    await refresh()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="manage-header">
        <div><h1>Quy tắc tính điểm</h1><p>Điều chỉnh mức điểm thưởng cho ba hoạt động học tập đang được hệ thống ghi nhận.</p></div>
      </div>

      <FormModal
        v-if="showForm"
        title="Điều chỉnh điểm thưởng"
        description="Mức điểm mới chỉ áp dụng cho các hoạt động phát sinh sau khi lưu."
        submit-label="Lưu thay đổi"
        :saving="saving"
        :disabled="saving || Number(form.points) < 0 || !Number.isInteger(Number(form.points))"
        @close="closeForm"
        @submit="submit"
      >
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="rule-edit-context">
          <span>Sự kiện</span>
          <strong>{{ ruleDefinitions[form.actionType]?.label }}</strong>
          <small>{{ form.actionType }}</small>
        </div>
        <div class="admin-form-grid">
          <div class="form-field">
            <label for="rule-points">Số điểm</label>
            <input id="rule-points" v-model="form.points" type="number" min="0" step="1" class="input" required />
            <small class="form-help">Điểm đã phát trước đây được giữ nguyên.</small>
          </div>
          <div class="form-field">
            <label for="rule-description">Mô tả</label>
            <textarea id="rule-description" v-model="form.description" class="input" rows="3" maxlength="255" placeholder="Giải thích khi nào điểm được trao."></textarea>
          </div>
        </div>
      </FormModal>

      <div class="manage-table-wrap">
        <div class="admin-panel-heading"><div><h2>Mức điểm đang áp dụng</h2><p>Mã sự kiện do hệ thống quản lý và không thể thay đổi.</p></div><span class="admin-panel-count">{{ systemRules.length }}/3 quy tắc</span></div>
        <p v-if="!loading && missingRuleCount > 0" class="rules-missing">
          Đang thiếu {{ missingRuleCount }} quy tắc hệ thống. Cần khôi phục dữ liệu cấu hình trước khi hoạt động tương ứng có thể cộng điểm.
        </p>
        <div class="rules-table-header">
          <div>SỰ KIỆN HỌC TẬP</div><div>ĐIỂM THƯỞNG</div><div>MÔ TẢ</div><div>THAO TÁC</div>
        </div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="systemRules.length === 0" class="empty-state">Chưa có quy tắc hệ thống nào.</div>
        <template v-else>
          <div v-for="r in systemRules" :key="r.id" class="rules-row">
            <div class="rules-row-event">
              <strong>{{ ruleDefinitions[r.actionType].label }}</strong>
              <span>{{ ruleDefinitions[r.actionType].detail }}</span>
              <small>{{ r.actionType }}</small>
            </div>
            <div class="rules-row-points">{{ r.points }} điểm</div>
            <div class="rules-row-desc">{{ r.description }}</div>
            <div class="row-action-group">
              <button class="row-action-btn" @click="openEditForm(r)"><Pencil :size="13" /> Điều chỉnh</button>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped src="./PointRulesManageView.css"></style>
