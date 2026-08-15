<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import FormModal from '@/components/common/FormModal.vue'
import StoreMembersPanel from '@/components/admin/StoreMembersPanel.vue'
import chainService from '@/api/chainService'
import regionService from '@/api/regionService'
import storeService from '@/api/storeService'
import userService from '@/api/userService'
import { confirmDialog, showError } from '@/utils/alerts'
import { Pencil, Plus, Trash2, Users } from '@lucide/vue'

const route = useRoute()
const VALID_TABS = ['stores', 'chains', 'regions']
const activeTab = ref(VALID_TABS.includes(route.query.tab) ? route.query.tab : 'stores')

const { data: chains, refresh: refreshChains } = useAsyncData(() => chainService.getAll())
const { data: regions, refresh: refreshRegions } = useAsyncData(() => regionService.getAll())
const { data: stores, loading: storesLoading, refresh: refreshStores } = useAsyncData(() => storeService.getAll())
const { data: users, refresh: refreshUsers } = useAsyncData(() => userService.getAll())

function employeeCountFor(storeId) {
  return (users.value || []).filter((u) => u.role === 'EMPLOYEE' && u.storeId && u.storeId.id === storeId).length
}
function trainerCountFor(storeId) {
  return (users.value || []).filter((u) => u.role === 'TRAINER' && u.storeId && u.storeId.id === storeId).length
}
function storeCountForChain(chainId) {
  return (stores.value || []).filter((s) => s.chainId && s.chainId.id === chainId).length
}
function storeCountForRegion(regionId) {
  return (stores.value || []).filter((s) => s.regionId && s.regionId.id === regionId).length
}

const searchKw = ref('')
const filterChainId = ref(null)
const filterRegionId = ref(null)

const filteredStores = computed(() => {
  return (stores.value || []).filter((s) => {
    if (searchKw.value.trim() && !s.name.toLowerCase().includes(searchKw.value.trim().toLowerCase())) return false
    if (filterChainId.value && (!s.chainId || s.chainId.id !== filterChainId.value)) return false
    if (filterRegionId.value && (!s.regionId || s.regionId.id !== filterRegionId.value)) return false
    return true
  })
})

const selectedChainId = ref(null)
const selectedChain = computed(() => (chains.value || []).find((c) => c.id === selectedChainId.value) || null)
const storesOfSelectedChain = computed(() =>
  (stores.value || []).filter((s) => s.chainId && s.chainId.id === selectedChainId.value),
)

const selectedRegionId = ref(null)
const selectedRegion = computed(() => (regions.value || []).find((r) => r.id === selectedRegionId.value) || null)
const storesOfSelectedRegion = computed(() =>
  (stores.value || []).filter((s) => s.regionId && s.regionId.id === selectedRegionId.value),
)

function storesOfChainInRegion(regionId) {
  return storesOfSelectedChain.value.filter((s) => s.regionId && s.regionId.id === regionId)
}
function storesOfRegionInChain(chainId) {
  return storesOfSelectedRegion.value.filter((s) => s.chainId && s.chainId.id === chainId)
}

const editingStoreId = ref(null)
const storeForm = ref({ name: '', maSt: '', chainId: null, regionId: null })
const storeOriginalScope = ref(null)
const storeSaving = ref(false)
const storeErrorMsg = ref('')
const showStoreForm = ref(false)

function openCreateStore(prefill = {}) {
  editingStoreId.value = null
  storeOriginalScope.value = null
  storeForm.value = { name: '', maSt: '', chainId: prefill.chainId ?? null, regionId: prefill.regionId ?? null }
  storeErrorMsg.value = ''
  showStoreForm.value = true
}

function openEditStore(s) {
  editingStoreId.value = s.id
  const chainId = s.chainId ? s.chainId.id : null
  const regionId = s.regionId ? s.regionId.id : null
  storeOriginalScope.value = { chainId, regionId }
  storeForm.value = { name: s.name, maSt: s.maSt || '', chainId, regionId }
  storeErrorMsg.value = ''
  showStoreForm.value = true
}

function closeStoreForm() {
  showStoreForm.value = false
  storeErrorMsg.value = ''
}

async function submitStore() {
  storeErrorMsg.value = ''
  const original = storeOriginalScope.value
  const scopeChanged =
    original && (original.chainId !== storeForm.value.chainId || original.regionId !== storeForm.value.regionId)
  if (scopeChanged) {
    const ok = await confirmDialog(
      'Đổi Chuỗi/Vùng của siêu thị có thể làm thay đổi phạm vi khóa học nhân viên tại đây nhìn thấy, và danh mục khóa học Trainer phụ trách siêu thị này quản lý. Ghi danh cũ vẫn được giữ nguyên. Tiếp tục lưu?',
      'Xác nhận đổi cơ cấu',
    )
    if (!ok) return
  }
  storeSaving.value = true
  try {
    const payload = {
      name: storeForm.value.name,
      maSt: storeForm.value.maSt,
      chainId: storeForm.value.chainId ? { id: storeForm.value.chainId } : null,
      regionId: storeForm.value.regionId ? { id: storeForm.value.regionId } : null,
    }
    if (editingStoreId.value) {
      await storeService.update(editingStoreId.value, payload)
    } else {
      await storeService.create(payload)
    }
    showStoreForm.value = false
    refreshStores()
  } catch (err) {
    storeErrorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    storeSaving.value = false
  }
}

const expandedStoreId = ref(null)
function toggleExpandStore(storeId) {
  expandedStoreId.value = expandedStoreId.value === storeId ? null : storeId
}

async function removeStore(s) {
  if (!(await confirmDialog(`Xoá siêu thị "${s.name}"?`))) return
  try {
    await storeService.remove(s.id)
    refreshStores()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá siêu thị này.')
  }
}

const editingChainId = ref(null)
const chainForm = ref({ name: '' })
const chainSaving = ref(false)
const chainErrorMsg = ref('')
const showChainForm = ref(false)

function openCreateChain() {
  editingChainId.value = null
  chainForm.value = { name: '' }
  chainErrorMsg.value = ''
  showChainForm.value = true
}
function openEditChain(c) {
  editingChainId.value = c.id
  chainForm.value = { name: c.name }
  chainErrorMsg.value = ''
  showChainForm.value = true
}
function closeChainForm() {
  showChainForm.value = false
  chainErrorMsg.value = ''
}
async function submitChain() {
  chainErrorMsg.value = ''
  chainSaving.value = true
  try {
    if (editingChainId.value) {
      await chainService.update(editingChainId.value, { name: chainForm.value.name })
    } else {
      await chainService.create({ name: chainForm.value.name })
    }
    showChainForm.value = false
    refreshChains()
  } catch (err) {
    chainErrorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    chainSaving.value = false
  }
}
async function removeChain(c) {
  if (!(await confirmDialog(`Xoá chuỗi "${c.name}"?`))) return
  try {
    await chainService.remove(c.id)
    if (selectedChainId.value === c.id) selectedChainId.value = null
    refreshChains()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá chuỗi này.')
  }
}

const editingRegionId = ref(null)
const regionForm = ref({ name: '' })
const regionSaving = ref(false)
const regionErrorMsg = ref('')
const showRegionForm = ref(false)

function openCreateRegion() {
  editingRegionId.value = null
  regionForm.value = { name: '' }
  regionErrorMsg.value = ''
  showRegionForm.value = true
}
function openEditRegion(r) {
  editingRegionId.value = r.id
  regionForm.value = { name: r.name }
  regionErrorMsg.value = ''
  showRegionForm.value = true
}
function closeRegionForm() {
  showRegionForm.value = false
  regionErrorMsg.value = ''
}
async function submitRegion() {
  regionErrorMsg.value = ''
  regionSaving.value = true
  try {
    if (editingRegionId.value) {
      await regionService.update(editingRegionId.value, { name: regionForm.value.name })
    } else {
      await regionService.create({ name: regionForm.value.name })
    }
    showRegionForm.value = false
    refreshRegions()
  } catch (err) {
    regionErrorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    regionSaving.value = false
  }
}
async function removeRegion(r) {
  if (!(await confirmDialog(`Xoá vùng "${r.name}"?`))) return
  try {
    await regionService.remove(r.id)
    if (selectedRegionId.value === r.id) selectedRegionId.value = null
    refreshRegions()
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
        <div><h1>Cơ cấu bán lẻ</h1><p>Quản lý Chuỗi, Vùng và Siêu thị — nền tảng để phân phạm vi khóa học và phân quyền Trainer.</p></div>
      </div>

      <div class="org-tabs">
        <button class="org-tab" :class="{ 'org-tab--active': activeTab === 'stores' }" @click="activeTab = 'stores'">Tất cả siêu thị</button>
        <button class="org-tab" :class="{ 'org-tab--active': activeTab === 'chains' }" @click="activeTab = 'chains'">Theo Chuỗi</button>
        <button class="org-tab" :class="{ 'org-tab--active': activeTab === 'regions' }" @click="activeTab = 'regions'">Theo Vùng</button>
      </div>

      <template v-if="activeTab === 'stores'">
        <div class="admin-toolbar">
          <input v-model="searchKw" type="text" class="input search-bar" placeholder="Tìm theo tên siêu thị..." />
          <select v-model="filterChainId" class="input org-toolbar-select">
            <option :value="null">Tất cả chuỗi</option>
            <option v-for="c in chains" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
          <select v-model="filterRegionId" class="input org-toolbar-select">
            <option :value="null">Tất cả vùng</option>
            <option v-for="r in regions" :key="r.id" :value="r.id">{{ r.name }}</option>
          </select>
          <span class="admin-toolbar-meta">{{ filteredStores.length }} siêu thị</span>
        </div>

        <div class="org-actions-row">
          <button class="btn btn-primary" @click="openCreateStore()"><Plus :size="16" /> Thêm siêu thị</button>
        </div>

        <section class="admin-data-panel">
          <div class="admin-org-table-header admin-org-store-header"><div>Tên siêu thị</div><div>Chuỗi</div><div>Vùng</div><div>Nhân viên / Trainer</div><div>Hành động</div></div>
          <p v-if="storesLoading" class="state-text">Đang tải...</p>
          <div v-else-if="filteredStores.length === 0" class="empty-state">Không tìm thấy siêu thị phù hợp.</div>
          <template v-for="s in filteredStores" v-else :key="s.id">
            <div class="admin-org-row admin-org-store-row">
              <div><div class="admin-org-name">{{ s.name }}</div><div class="admin-org-detail">Mã siêu thị: {{ s.maSt }}</div></div>
              <div class="admin-org-detail">{{ s.chainId ? s.chainId.name : 'Chưa gán' }}</div>
              <div class="admin-org-detail">{{ s.regionId ? s.regionId.name : 'Chưa gán' }}</div>
              <div class="admin-org-value">{{ employeeCountFor(s.id) }} NV / {{ trainerCountFor(s.id) }} TR</div>
              <div class="row-action-group">
                <button
                  class="row-action-btn"
                  :class="{ 'row-action-btn--active': expandedStoreId === s.id }"
                  @click="toggleExpandStore(s.id)"
                >
                  <Users :size="13" /> Thành viên
                </button>
                <button class="row-action-btn" @click="openEditStore(s)"><Pencil :size="13" /> Sửa</button>
                <button class="row-action-btn row-action-btn--danger" @click="removeStore(s)"><Trash2 :size="13" /> Xoá</button>
              </div>
            </div>
            <StoreMembersPanel
              v-if="expandedStoreId === s.id"
              :store-id="s.id"
              :store-name="s.name"
              :users="users || []"
              :stores="stores || []"
              :chains="chains || []"
              :regions="regions || []"
              @refresh="refreshUsers"
              @close="expandedStoreId = null"
            />
          </template>
        </section>
      </template>

      <template v-else-if="activeTab === 'chains'">
        <div class="org-split">
          <section class="admin-data-panel org-split-list">
            <div class="admin-panel-heading">
              <div><h2>Danh sách chuỗi</h2><p>Chọn 1 chuỗi để xem siêu thị.</p></div>
              <button class="btn btn-primary btn-sm" @click="openCreateChain"><Plus :size="14" /> Thêm chuỗi</button>
            </div>
            <div v-if="!chains || chains.length === 0" class="empty-state">Chưa có chuỗi nào.</div>
            <div
              v-for="c in chains"
              :key="c.id"
              class="org-list-item"
              :class="{ 'org-list-item--active': selectedChainId === c.id }"
              @click="selectedChainId = c.id"
            >
              <div><div class="admin-org-name">{{ c.name }}</div><div class="admin-org-detail">{{ storeCountForChain(c.id) }} siêu thị</div></div>
              <div class="row-action-group">
                <button class="row-action-btn" @click.stop="openEditChain(c)"><Pencil :size="13" /></button>
                <button class="row-action-btn row-action-btn--danger" @click.stop="removeChain(c)"><Trash2 :size="13" /></button>
              </div>
            </div>
          </section>

          <section class="admin-data-panel org-split-detail">
            <div v-if="!selectedChain" class="empty-state">Chọn 1 chuỗi bên trái để xem danh sách siêu thị.</div>
            <template v-else>
              <div class="admin-panel-heading">
                <div><h2>Siêu thị thuộc "{{ selectedChain.name }}"</h2><p>Nhóm theo Vùng.</p></div>
                <button class="btn btn-secondary btn-sm" @click="openCreateStore({ chainId: selectedChain.id })"><Plus :size="14" /> Thêm siêu thị vào chuỗi này</button>
              </div>
              <div v-if="storesOfSelectedChain.length === 0" class="empty-state">Chuỗi này chưa có siêu thị nào.</div>
              <template v-for="r in regions" :key="r.id">
                <template v-if="storesOfChainInRegion(r.id).length > 0">
                  <div class="org-group-label">{{ r.name }}</div>
                  <template v-for="s in storesOfChainInRegion(r.id)" :key="s.id">
                    <div class="org-split-row">
                      <div><div class="admin-org-name">{{ s.name }}</div><div class="admin-org-detail">Mã siêu thị: {{ s.maSt }}</div></div>
                      <div class="row-action-group">
                        <button
                          class="row-action-btn"
                          :class="{ 'row-action-btn--active': expandedStoreId === s.id }"
                          @click="toggleExpandStore(s.id)"
                        >
                          <Users :size="13" /> Thành viên
                        </button>
                        <button class="row-action-btn" @click="openEditStore(s)"><Pencil :size="13" /> Sửa</button>
                        <button class="row-action-btn row-action-btn--danger" @click="removeStore(s)"><Trash2 :size="13" /> Xoá</button>
                      </div>
                    </div>
                    <StoreMembersPanel
                      v-if="expandedStoreId === s.id"
                      :store-id="s.id"
                      :store-name="s.name"
                      :users="users || []"
                      :stores="stores || []"
                      :chains="chains || []"
                      :regions="regions || []"
                      @refresh="refreshUsers"
                      @close="expandedStoreId = null"
                    />
                  </template>
                </template>
              </template>
            </template>
          </section>
        </div>
      </template>

      <template v-else>
        <div class="org-split">
          <section class="admin-data-panel org-split-list">
            <div class="admin-panel-heading">
              <div><h2>Danh sách vùng</h2><p>Chọn 1 vùng để xem siêu thị.</p></div>
              <button class="btn btn-primary btn-sm" @click="openCreateRegion"><Plus :size="14" /> Thêm vùng</button>
            </div>
            <div v-if="!regions || regions.length === 0" class="empty-state">Chưa có vùng nào.</div>
            <div
              v-for="r in regions"
              :key="r.id"
              class="org-list-item"
              :class="{ 'org-list-item--active': selectedRegionId === r.id }"
              @click="selectedRegionId = r.id"
            >
              <div><div class="admin-org-name">{{ r.name }}</div><div class="admin-org-detail">{{ storeCountForRegion(r.id) }} siêu thị</div></div>
              <div class="row-action-group">
                <button class="row-action-btn" @click.stop="openEditRegion(r)"><Pencil :size="13" /></button>
                <button class="row-action-btn row-action-btn--danger" @click.stop="removeRegion(r)"><Trash2 :size="13" /></button>
              </div>
            </div>
          </section>

          <section class="admin-data-panel org-split-detail">
            <div v-if="!selectedRegion" class="empty-state">Chọn 1 vùng bên trái để xem danh sách siêu thị.</div>
            <template v-else>
              <div class="admin-panel-heading">
                <div><h2>Siêu thị thuộc "{{ selectedRegion.name }}"</h2><p>Nhóm theo Chuỗi.</p></div>
                <button class="btn btn-secondary btn-sm" @click="openCreateStore({ regionId: selectedRegion.id })"><Plus :size="14" /> Thêm siêu thị vào vùng này</button>
              </div>
              <div v-if="storesOfSelectedRegion.length === 0" class="empty-state">Vùng này chưa có siêu thị nào.</div>
              <template v-for="c in chains" :key="c.id">
                <template v-if="storesOfRegionInChain(c.id).length > 0">
                  <div class="org-group-label">{{ c.name }}</div>
                  <template v-for="s in storesOfRegionInChain(c.id)" :key="s.id">
                    <div class="org-split-row">
                      <div><div class="admin-org-name">{{ s.name }}</div><div class="admin-org-detail">Mã siêu thị: {{ s.maSt }}</div></div>
                      <div class="row-action-group">
                        <button
                          class="row-action-btn"
                          :class="{ 'row-action-btn--active': expandedStoreId === s.id }"
                          @click="toggleExpandStore(s.id)"
                        >
                          <Users :size="13" /> Thành viên
                        </button>
                        <button class="row-action-btn" @click="openEditStore(s)"><Pencil :size="13" /> Sửa</button>
                        <button class="row-action-btn row-action-btn--danger" @click="removeStore(s)"><Trash2 :size="13" /> Xoá</button>
                      </div>
                    </div>
                    <StoreMembersPanel
                      v-if="expandedStoreId === s.id"
                      :store-id="s.id"
                      :store-name="s.name"
                      :users="users || []"
                      :stores="stores || []"
                      :chains="chains || []"
                      :regions="regions || []"
                      @refresh="refreshUsers"
                      @close="expandedStoreId = null"
                    />
                  </template>
                </template>
              </template>
            </template>
          </section>
        </div>
      </template>

      <FormModal
        v-if="showStoreForm"
        :title="editingStoreId ? 'Chỉnh sửa siêu thị' : 'Thêm siêu thị mới'"
        description="Mỗi siêu thị phải thuộc một Chuỗi và một Vùng để hệ thống lọc khóa học, trainer và nhân viên chính xác."
        :submit-label="editingStoreId ? 'Lưu thay đổi' : 'Thêm siêu thị'"
        :saving="storeSaving"
        :disabled="!storeForm.name.trim() || !/^\d{4}$/.test(storeForm.maSt.trim()) || !storeForm.chainId || !storeForm.regionId"
        @close="closeStoreForm"
        @submit="submitStore"
      >
        <p v-if="storeErrorMsg" class="alert alert-error">{{ storeErrorMsg }}</p>
        <div class="admin-form-grid admin-form-grid--three">
          <div class="form-field admin-form-span">
            <label for="org-store-name">Tên siêu thị</label>
            <input id="org-store-name" v-model="storeForm.name" type="text" class="input" placeholder="Ví dụ: Điện Máy Xanh Nguyễn Trãi" required />
          </div>
          <div class="form-field">
            <label for="org-store-mast">Mã siêu thị (MaST)</label>
            <input
              id="org-store-mast"
              v-model="storeForm.maSt"
              type="text"
              class="input"
              placeholder="Ví dụ: 1001"
              maxlength="4"
              inputmode="numeric"
              required
            />
            <small class="form-help">Đúng 4 chữ số, không trùng siêu thị khác.</small>
          </div>
          <div class="form-field">
            <label for="org-store-chain">Chuỗi</label>
            <select id="org-store-chain" v-model="storeForm.chainId" class="input" required>
              <option :value="null" disabled>Chọn chuỗi</option>
              <option v-for="c in chains" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-field">
            <label for="org-store-region">Vùng</label>
            <select id="org-store-region" v-model="storeForm.regionId" class="input" required>
              <option :value="null" disabled>Chọn vùng</option>
              <option v-for="r in regions" :key="r.id" :value="r.id">{{ r.name }}</option>
            </select>
          </div>
        </div>
        <p class="form-help">Chuỗi và Vùng được dùng để lọc khóa học, Trainer và nhân viên phù hợp với siêu thị. Quản lý danh sách thành viên qua nút "Thành viên" ở bảng danh sách.</p>
      </FormModal>

      <FormModal
        v-if="showChainForm"
        size="small"
        :title="editingChainId ? 'Chỉnh sửa chuỗi' : 'Thêm chuỗi mới'"
        description="Tên chuỗi được dùng khi phân phạm vi khóa học và lọc siêu thị."
        :submit-label="editingChainId ? 'Lưu thay đổi' : 'Thêm chuỗi'"
        :saving="chainSaving"
        :disabled="!chainForm.name.trim()"
        @close="closeChainForm"
        @submit="submitChain"
      >
        <p v-if="chainErrorMsg" class="alert alert-error">{{ chainErrorMsg }}</p>
        <div class="form-field">
          <label for="org-chain-name">Tên chuỗi</label>
          <input id="org-chain-name" v-model="chainForm.name" type="text" class="input" placeholder="Ví dụ: Điện Máy Xanh" required />
        </div>
      </FormModal>

      <FormModal
        v-if="showRegionForm"
        size="small"
        :title="editingRegionId ? 'Chỉnh sửa vùng' : 'Thêm vùng mới'"
        description="Tên vùng nên thống nhất với cách doanh nghiệp đang phân chia khu vực vận hành."
        :submit-label="editingRegionId ? 'Lưu thay đổi' : 'Thêm vùng'"
        :saving="regionSaving"
        :disabled="!regionForm.name.trim()"
        @close="closeRegionForm"
        @submit="submitRegion"
      >
        <p v-if="regionErrorMsg" class="alert alert-error">{{ regionErrorMsg }}</p>
        <div class="form-field">
          <label for="org-region-name">Tên vùng</label>
          <input id="org-region-name" v-model="regionForm.name" type="text" class="input" placeholder="Ví dụ: Thành phố Hồ Chí Minh" required />
        </div>
      </FormModal>
    </div>
  </div>
</template>

<style scoped src="./OrgManageView.css"></style>
