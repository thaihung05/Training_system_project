<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import { useLazyList } from '@/composables/useLazyList'
import courseService from '@/api/courseService'
import storeService from '@/api/storeService'
import chainService from '@/api/chainService'
import regionService from '@/api/regionService'
import userService from '@/api/userService'
import enrollmentService from '@/api/enrollmentService'
import { confirmDialog } from '@/utils/alerts'
import { isStoreInCourseScope } from '@/utils/courseScope'
import TrainerCourseNav from '@/components/trainer/TrainerCourseNav.vue'
import CourseAccessError from '@/components/trainer/CourseAccessError.vue'
import { Building2, MapPinned, Store, UserMinus, Users } from '@lucide/vue'

const route = useRoute()
const courseId = Number(route.params.courseId)

const { data: course, error: courseError } = useAsyncData(() => courseService.getCourseById(courseId))
const { items: roster, loading: rosterLoading, loadingMore: rosterLoadingMore, hasMore: rosterHasMore, loadMore: loadMoreRoster, reload: refreshRoster } = useLazyList(
  (page, size) => enrollmentService.getRoster(courseId, page, size),
  20,
)
const { data: fullRoster, refresh: refreshFullRoster } = useAsyncData(() => enrollmentService.getRoster(courseId))
const { data: stores } = useAsyncData(() => storeService.getAll())
const { data: chains } = useAsyncData(() => chainService.getAll())
const { data: regions } = useAsyncData(() => regionService.getAll())
const { data: allUsers } = useAsyncData(() => userService.getAll())

const enrolledUserIds = computed(() => new Set((fullRoster.value || []).map((e) => e.userId.id)))
const scopeEligibleStores = computed(() =>
  (stores.value || []).filter((store) => isStoreInCourseScope(store, course.value)),
)
const selectedChainId = ref(null)
const selectedRegionId = ref(null)

const availableChains = computed(() => {
  const ids = new Set(scopeEligibleStores.value.map((store) => store.chainId?.id))
  return (chains.value || []).filter((chain) => ids.has(chain.id))
})

const availableRegions = computed(() => {
  const matchingStores = selectedChainId.value
    ? scopeEligibleStores.value.filter((store) => store.chainId?.id === selectedChainId.value)
    : scopeEligibleStores.value
  const ids = new Set(matchingStores.map((store) => store.regionId?.id))
  return (regions.value || []).filter((region) => ids.has(region.id))
})

const filteredStores = computed(() => {
  if (!selectedChainId.value || !selectedRegionId.value) return []
  return scopeEligibleStores.value.filter(
    (store) => store.chainId?.id === selectedChainId.value && store.regionId?.id === selectedRegionId.value,
  )
})

const availableEmployees = computed(() =>
  (allUsers.value || []).filter(
    (u) =>
      u.role === 'EMPLOYEE' &&
      u.isActive &&
      isStoreInCourseScope(u.storeId, course.value) &&
      !enrolledUserIds.value.has(u.id),
  ),
)

watch(selectedChainId, () => {
  if (!availableRegions.value.some((region) => region.id === selectedRegionId.value)) selectedRegionId.value = null
  selectedStoreId.value = null
})

watch(selectedRegionId, () => {
  selectedStoreId.value = null
})

const employeeSearch = ref('')
const filteredAvailableEmployees = computed(() => {
  const kw = employeeSearch.value.trim().toLowerCase()
  if (!kw) return availableEmployees.value
  return availableEmployees.value.filter((u) => u.name.toLowerCase().includes(kw))
})

const selectedUserIds = ref([])
const selectedStoreId = ref(null)
const resultMsg = ref('')
const errorMsg = ref('')
const submitting = ref(false)

async function enrollSelected() {
  if (selectedUserIds.value.length === 0) return
  errorMsg.value = ''
  resultMsg.value = ''
  submitting.value = true
  try {
    const res = await enrollmentService.enrollUsers(courseId, selectedUserIds.value)
    resultMsg.value = `Đã ghi danh ${res.data.enrolled.length} người. Bỏ qua ${res.data.skipped.length} người (đã ghi danh hoặc không còn đủ điều kiện).`
    selectedUserIds.value = []
    refreshRoster()
    refreshFullRoster()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    submitting.value = false
  }
}

async function enrollStore() {
  if (!selectedStoreId.value) return
  errorMsg.value = ''
  resultMsg.value = ''
  submitting.value = true
  try {
    const res = await enrollmentService.enrollByStore(courseId, selectedStoreId.value)
    resultMsg.value = `Đã ghi danh ${res.data.enrolled.length} người. Bỏ qua ${res.data.skipped.length} người (đã ghi danh hoặc không còn đủ điều kiện).`
    selectedStoreId.value = null
    refreshRoster()
    refreshFullRoster()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    submitting.value = false
  }
}

async function unenroll(e) {
  if (!(await confirmDialog(`Huỷ ghi danh "${e.userId.name}" khỏi khoá học?`))) return
  await enrollmentService.unenroll(e.id)
  refreshRoster()
  refreshFullRoster()
}
</script>

<template>
  <CourseAccessError v-if="courseError" :message="courseError.response?.data" />
  <div v-else class="page trainer-page enrollment-workspace">
    <TrainerCourseNav :course="course" active="enrollments" />

    <div class="trainer-context-header">
      <div>
        <h2>Ghi danh nhân viên</h2>
        <p>{{ fullRoster?.length ?? 0 }} nhân viên đang tham gia khóa học.</p>
      </div>
    </div>

    <p v-if="course && !course.isActive" class="alert alert-error">
      Khoá học đang bị ẩn - hãy mở lại khoá học ở trang Quản lý khoá học trước khi ghi danh thêm người.
    </p>
    <p v-if="resultMsg" class="alert alert-success">{{ resultMsg }}</p>
    <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>

    <div class="manage-layout enrollment-layout">
      <div class="manage-table-wrap trainer-panel enrollment-roster">
        <div class="trainer-panel-heading">
          <div><h2>Danh sách đã ghi danh</h2><p>Theo dõi Siêu thị và tiến độ hiện tại.</p></div>
          <Users :size="19" />
        </div>
        <div class="roster-header">
          <div>NHÂN VIÊN</div>
          <div>SIÊU THỊ</div>
          <div>TIẾN ĐỘ</div>
          <div>HÀNH ĐỘNG</div>
        </div>
        <p v-if="rosterLoading" class="state-text">Đang tải...</p>
        <div v-else-if="roster.length === 0" class="empty-state">Chưa có ai ghi danh khoá học này.</div>
        <template v-else>
          <div v-for="e in roster" :key="e.id" class="roster-row">
            <div class="roster-row-name">{{ e.userId.name }}</div>
            <div class="roster-row-dept">{{ e.userId.storeId ? e.userId.storeId.name : '-' }}</div>
            <div class="roster-row-progress">{{ e.progressPercent }}%</div>
            <div class="row-action-group">
              <button class="row-action-btn row-action-btn--danger" @click="unenroll(e)"><UserMinus :size="13" /> Huỷ ghi danh</button>
            </div>
          </div>
        </template>
        <button v-if="rosterHasMore" class="btn btn-secondary roster-load-more" :disabled="rosterLoadingMore" @click="loadMoreRoster">
          {{ rosterLoadingMore ? 'Đang tải...' : 'Tải thêm' }}
        </button>
      </div>

      <div class="enroll-side">
        <div class="trainer-panel enroll-store-card">
          <div class="trainer-panel-heading">
            <div><h2>Ghi danh theo Siêu thị</h2><p>Chọn lần lượt Chuỗi, Vùng và Siêu thị.</p></div>
            <Store :size="19" />
          </div>

          <div v-if="scopeEligibleStores.length === 0" class="state-text enrollment-card-padding">
            Không có Siêu thị phù hợp với phạm vi khóa học.
          </div>

          <div v-else class="enrollment-filter-flow">
            <label class="enrollment-filter-step">
              <span class="enrollment-step-number">1</span>
              <span class="enrollment-step-label"><Building2 :size="15" /> Chọn Chuỗi</span>
              <select v-model="selectedChainId" class="input" :disabled="!course?.isActive">
                <option :value="null">-- Chọn Chuỗi --</option>
                <option v-for="chain in availableChains" :key="chain.id" :value="chain.id">{{ chain.name }}</option>
              </select>
            </label>

            <label class="enrollment-filter-step" :class="{ muted: !selectedChainId }">
              <span class="enrollment-step-number">2</span>
              <span class="enrollment-step-label"><MapPinned :size="15" /> Chọn Vùng</span>
              <select v-model="selectedRegionId" class="input" :disabled="!course?.isActive || !selectedChainId">
                <option :value="null">-- Chọn Vùng --</option>
                <option v-for="region in availableRegions" :key="region.id" :value="region.id">{{ region.name }}</option>
              </select>
            </label>

            <label class="enrollment-filter-step" :class="{ muted: !selectedRegionId }">
              <span class="enrollment-step-number">3</span>
              <span class="enrollment-step-label"><Store :size="15" /> Chọn Siêu thị</span>
              <select v-model="selectedStoreId" class="input" :disabled="!course?.isActive || !selectedChainId || !selectedRegionId">
                <option :value="null">-- Chọn Siêu thị --</option>
                <option v-for="store in filteredStores" :key="store.id" :value="store.id">{{ store.name }}</option>
              </select>
              <small v-if="selectedChainId && selectedRegionId">
                Tìm thấy {{ filteredStores.length }} Siêu thị đúng Chuỗi và Vùng đã chọn.
              </small>
            </label>

            <button class="btn btn-primary" :disabled="!selectedStoreId || submitting || !course?.isActive" @click="enrollStore">
              <Users :size="16" /> Ghi danh cả Siêu thị
            </button>
          </div>
        </div>

        <div class="trainer-panel enroll-person-card">
          <div class="trainer-panel-heading">
            <div><h2>Ghi danh cá nhân</h2><p>Chọn một hoặc nhiều nhân viên phù hợp.</p></div>
            <Users :size="19" />
          </div>
          <div class="enrollment-card-padding">
            <div v-if="availableEmployees.length === 0" class="state-text">Không còn nhân viên phù hợp để ghi danh.</div>
            <template v-else>
            <input
              v-model="employeeSearch"
              type="text"
              class="input employee-search"
              placeholder="Tìm theo tên nhân viên..."
            />
            <div v-if="filteredAvailableEmployees.length === 0" class="state-text">Không tìm thấy nhân viên phù hợp.</div>
            <div v-else class="employee-picklist">
              <label v-for="u in filteredAvailableEmployees" :key="u.id" class="employee-pick-row">
                <input type="checkbox" :value="u.id" v-model="selectedUserIds" :disabled="!course?.isActive" />
                <span>{{ u.name }}</span>
                <span class="employee-pick-dept">{{ u.storeId ? u.storeId.name : '-' }}</span>
              </label>
            </div>
            <button
              class="btn btn-primary"
              :disabled="selectedUserIds.length === 0 || submitting || !course?.isActive"
              @click="enrollSelected"
            >
              Ghi danh {{ selectedUserIds.length > 0 ? `(${selectedUserIds.length})` : '' }}
            </button>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./EnrollmentManageView.css"></style>
