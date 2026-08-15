<script setup>
import { computed, ref } from 'vue'
import { Award, Pencil } from '@lucide/vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import FormModal from '@/components/common/FormModal.vue'
import badgeService from '@/api/badgeService'

const { data: badges, loading, refresh } = useAsyncData(() => badgeService.getAll())

const badgeDefinitions = {
  CERT_1: {
    label: 'Đạt 1 chứng chỉ',
    detail: 'Trao khi nhân viên nhận được chứng chỉ đầu tiên.',
    order: 1,
  },
  CERT_5: {
    label: 'Đạt 5 chứng chỉ',
    detail: 'Trao khi nhân viên tích lũy đủ 5 chứng chỉ.',
    order: 2,
  },
  CERT_10: {
    label: 'Đạt 10 chứng chỉ',
    detail: 'Trao khi nhân viên tích lũy đủ 10 chứng chỉ.',
    order: 3,
  },
}

const systemBadges = computed(() =>
  (badges.value || [])
    .filter((b) => badgeDefinitions[b.code])
    .sort((first, second) => badgeDefinitions[first.code].order - badgeDefinitions[second.code].order),
)

const missingBadgeCount = computed(() => Object.keys(badgeDefinitions).length - systemBadges.value.length)

const editingId = ref(null)
const form = ref({ code: '', name: '', description: '', iconUrl: '' })
const saving = ref(false)
const errorMsg = ref('')
const showForm = ref(false)

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
    await badgeService.update(editingId.value, payload)
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
        <div><h1>Huy hiệu</h1><p>Điều chỉnh nội dung hiển thị cho ba mốc thành tích chứng chỉ đang được hệ thống ghi nhận.</p></div>
      </div>

      <FormModal
        v-if="showForm"
        title="Chỉnh sửa huy hiệu"
        description="Mã huy hiệu do hệ thống quản lý và không thể thay đổi."
        submit-label="Lưu thay đổi"
        :saving="saving"
        :disabled="!form.name.trim()"
        @close="closeForm"
        @submit="submit"
      >
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="rule-edit-context">
          <span>Mốc thành tích</span>
          <strong>{{ badgeDefinitions[form.code]?.label }}</strong>
          <small>{{ form.code }}</small>
        </div>
        <div class="admin-form-grid">
          <div class="form-field">
            <label for="badge-name">Tên huy hiệu</label>
            <input id="badge-name" v-model="form.name" type="text" class="input" placeholder="Ví dụ: Học viên chăm chỉ" required />
          </div>
          <div class="form-field">
            <label for="badge-icon">Đường dẫn biểu tượng <span class="form-label-optional">(không bắt buộc)</span></label>
            <input id="badge-icon" v-model="form.iconUrl" type="url" class="input" placeholder="https://..." />
          </div>
          <div class="form-field admin-form-span">
            <label for="badge-description">Mô tả</label>
            <textarea id="badge-description" v-model="form.description" class="input" rows="3" maxlength="255" placeholder="Điều kiện hoặc ý nghĩa của huy hiệu."></textarea>
          </div>
        </div>
      </FormModal>

      <section class="admin-data-panel badge-manage-panel">
        <div class="admin-panel-heading"><div><h2>Danh mục huy hiệu</h2><p>Ba mốc thành tích do hệ thống quản lý.</p></div><span class="admin-panel-count">{{ systemBadges.length }}/3 huy hiệu</span></div>
        <p v-if="!loading && missingBadgeCount > 0" class="rules-missing">
          Đang thiếu {{ missingBadgeCount }} huy hiệu hệ thống. Cần khôi phục dữ liệu cấu hình trước khi mốc thành tích tương ứng có thể được cấp.
        </p>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="systemBadges.length === 0" class="empty-state">Chưa có huy hiệu hệ thống nào.</div>
        <div v-else class="badge-manage-grid">
          <div v-for="b in systemBadges" :key="b.id" class="card badge-manage-card">
            <div class="badge-icon">
              <img v-if="b.iconUrl" :src="b.iconUrl" :alt="b.name" />
              <Award v-else :size="20" />
            </div>
            <div class="badge-manage-name">{{ b.name }}</div>
            <div class="badge-manage-code">{{ b.code }}</div>
            <div class="badge-manage-condition">{{ badgeDefinitions[b.code].detail }}</div>
            <div class="badge-manage-desc">{{ b.description }}</div>
            <div class="row-action-group badge-manage-actions">
              <button class="row-action-btn" @click="openEditForm(b)"><Pencil :size="13" /> Sửa</button>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped src="./BadgesManageView.css"></style>
