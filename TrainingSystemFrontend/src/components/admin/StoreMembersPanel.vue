<script setup>
import { computed, ref, watch } from 'vue'
import userService from '@/api/userService'
import FormModal from '@/components/common/FormModal.vue'
import { ArrowRightLeft, Trash2, Users, UserPlus, X } from '@lucide/vue'

const props = defineProps({
  storeId: { type: Number, required: true },
  storeName: { type: String, default: '' },
  users: { type: Array, default: () => [] },
  stores: { type: Array, default: () => [] },
  chains: { type: Array, default: () => [] },
  regions: { type: Array, default: () => [] },
})
const emit = defineEmits(['refresh', 'close'])

const search = ref('')
const busyId = ref(null)
const errorMsg = ref('')

const members = computed(() =>
  props.users
    .filter((u) => u.storeId && u.storeId.id === props.storeId)
    .slice()
    .sort((a, b) => a.name.localeCompare(b.name)),
)

const available = computed(() => {
  const kw = search.value.trim().toLowerCase()
  return props.users
    .filter((u) => u.role !== 'ADMIN' && (!u.storeId || u.storeId.id !== props.storeId))
    .filter((u) => !kw || u.name.toLowerCase().includes(kw))
    .slice(0, 30)
})

function initials(name) {
  return (name || '').split(' ').slice(-2).map((w) => w[0]).join('').toUpperCase()
}

async function add(u) {
  errorMsg.value = ''
  busyId.value = u.id
  try {
    await userService.update(u.id, { name: u.name, email: u.email, role: u.role, storeId: { id: props.storeId } })
    emit('refresh')
  } catch (err) {
    errorMsg.value = err.response?.data || 'Không thể thêm người này vào siêu thị.'
  } finally {
    busyId.value = null
  }
}

async function remove(u) {
  if (u.role === 'EMPLOYEE') return
  errorMsg.value = ''
  busyId.value = u.id
  try {
    await userService.update(u.id, { name: u.name, email: u.email, role: u.role, storeId: null })
    emit('refresh')
  } catch (err) {
    errorMsg.value = err.response?.data || 'Không thể bỏ người này khỏi siêu thị.'
  } finally {
    busyId.value = null
  }
}

const reassignTarget = ref(null)
const reassignChainId = ref(null)
const reassignRegionId = ref(null)
const reassignStoreId = ref(null)
const reassignSaving = ref(false)
const reassignError = ref('')

const reassignRegions = computed(() => {
  const matching = reassignChainId.value
    ? props.stores.filter((s) => s.chainId && s.chainId.id === reassignChainId.value)
    : props.stores
  const ids = new Set(matching.map((s) => s.regionId && s.regionId.id))
  return props.regions.filter((r) => ids.has(r.id))
})

const reassignStores = computed(() => {
  if (!reassignChainId.value || !reassignRegionId.value) return []
  return props.stores.filter(
    (s) => s.chainId && s.chainId.id === reassignChainId.value && s.regionId && s.regionId.id === reassignRegionId.value,
  )
})

watch(reassignChainId, () => {
  if (!reassignRegions.value.some((r) => r.id === reassignRegionId.value)) reassignRegionId.value = null
  reassignStoreId.value = null
})
watch(reassignRegionId, () => {
  reassignStoreId.value = null
})

function openReassign(u) {
  reassignTarget.value = u
  reassignChainId.value = null
  reassignRegionId.value = null
  reassignStoreId.value = null
  reassignError.value = ''
}

function closeReassign() {
  reassignTarget.value = null
}

async function submitReassign() {
  if (!reassignStoreId.value) return
  reassignError.value = ''
  reassignSaving.value = true
  try {
    const u = reassignTarget.value
    await userService.update(u.id, { name: u.name, email: u.email, role: u.role, storeId: { id: reassignStoreId.value } })
    reassignTarget.value = null
    emit('refresh')
  } catch (err) {
    reassignError.value = err.response?.data || 'Không thể chuyển siêu thị cho người này.'
  } finally {
    reassignSaving.value = false
  }
}
</script>

<template>
  <div class="store-members">
    <div class="store-members-header">
      <span class="store-members-title"><Users :size="14" /> Thành viên · {{ storeName }}</span>
      <button class="store-members-close" aria-label="Đóng" @click="emit('close')"><X :size="15" /></button>
    </div>

    <p v-if="errorMsg" class="alert alert-error store-members-error">{{ errorMsg }}</p>

    <div class="store-members-columns">
      <div class="store-members-col">
        <div class="store-members-col-heading">
          <span>Đang thuộc siêu thị này</span>
          <span class="store-members-count">{{ members.length }}</span>
        </div>
        <div v-if="members.length === 0" class="state-text store-members-empty">Chưa có ai thuộc siêu thị này.</div>
        <div v-else class="store-member-list">
          <div v-for="u in members" :key="u.id" class="store-member-row">
            <span class="store-member-avatar">{{ initials(u.name) }}</span>
            <div class="store-member-info">
              <span class="store-member-name">{{ u.name }}</span>
              <span class="store-member-role">{{ u.role === 'TRAINER' ? 'Trainer' : 'Nhân viên' }}</span>
            </div>
            <button
              v-if="u.role === 'TRAINER'"
              class="store-member-remove"
              :disabled="busyId === u.id"
              title="Bỏ khỏi siêu thị"
              @click="remove(u)"
            >
              <Trash2 :size="13" />
            </button>
            <button v-else class="store-member-reassign" @click="openReassign(u)">
              <ArrowRightLeft :size="12" /> Đổi siêu thị khác
            </button>
          </div>
        </div>
      </div>

      <div class="store-members-col">
        <div class="store-members-col-heading"><span>Thêm vào siêu thị này</span></div>
        <input v-model="search" type="text" class="input store-members-search" placeholder="Tìm theo tên..." />
        <div v-if="available.length === 0" class="state-text store-members-empty">Không tìm thấy ai phù hợp.</div>
        <div v-else class="store-member-list">
          <div v-for="u in available" :key="u.id" class="store-member-row">
            <span class="store-member-avatar store-member-avatar--muted">{{ initials(u.name) }}</span>
            <div class="store-member-info">
              <span class="store-member-name">{{ u.name }}</span>
              <span class="store-member-role">{{ u.storeId ? u.storeId.name : 'Chưa có siêu thị' }}</span>
            </div>
            <button class="store-member-add" :disabled="busyId === u.id" @click="add(u)">
              <UserPlus :size="13" /> Thêm
            </button>
          </div>
        </div>
      </div>
    </div>

    <FormModal
      v-if="reassignTarget"
      size="small"
      :title="`Đổi siêu thị cho ${reassignTarget.name}`"
      description="Chọn Chuỗi rồi Vùng để lọc ra đúng siêu thị cần chuyển tới."
      submit-label="Chuyển siêu thị"
      :saving="reassignSaving"
      :disabled="!reassignStoreId"
      @close="closeReassign"
      @submit="submitReassign"
    >
      <p v-if="reassignError" class="alert alert-error">{{ reassignError }}</p>
      <div class="form-field">
        <label for="reassign-chain">Chuỗi</label>
        <select id="reassign-chain" v-model="reassignChainId" class="input">
          <option :value="null">-- Chọn Chuỗi --</option>
          <option v-for="c in chains" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
      </div>
      <div class="form-field">
        <label for="reassign-region">Vùng</label>
        <select id="reassign-region" v-model="reassignRegionId" class="input" :disabled="!reassignChainId">
          <option :value="null">-- Chọn Vùng --</option>
          <option v-for="r in reassignRegions" :key="r.id" :value="r.id">{{ r.name }}</option>
        </select>
      </div>
      <div class="form-field">
        <label for="reassign-store">Siêu thị</label>
        <select id="reassign-store" v-model="reassignStoreId" class="input" :disabled="!reassignChainId || !reassignRegionId">
          <option :value="null">-- Chọn Siêu thị --</option>
          <option v-for="s in reassignStores" :key="s.id" :value="s.id">{{ s.name }}</option>
        </select>
        <small v-if="reassignChainId && reassignRegionId" class="form-help">
          Tìm thấy {{ reassignStores.length }} siêu thị đúng Chuỗi và Vùng đã chọn.
        </small>
      </div>
    </FormModal>
  </div>
</template>

<style scoped src="./StoreMembersPanel.css"></style>
