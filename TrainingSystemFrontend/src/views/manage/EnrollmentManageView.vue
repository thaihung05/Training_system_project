<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import { useLazyList } from '@/composables/useLazyList'
import courseService from '@/api/courseService'
import storeService from '@/api/storeService'
import userService from '@/api/userService'
import enrollmentService from '@/api/enrollmentService'
import { confirmDialog } from '@/utils/alerts'
import { ChevronLeft, UserMinus } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const courseId = Number(route.params.courseId)

const { data: course } = useAsyncData(() => courseService.getCourseById(courseId))
const { items: roster, loading: rosterLoading, loadingMore: rosterLoadingMore, hasMore: rosterHasMore, loadMore: loadMoreRoster, reload: refreshRoster } = useLazyList(
  (page, size) => enrollmentService.getRoster(courseId, page, size),
  20,
)
const { data: fullRoster, refresh: refreshFullRoster } = useAsyncData(() => enrollmentService.getRoster(courseId))
const { data: stores } = useAsyncData(() => storeService.getAll())
const { data: allUsers } = useAsyncData(() => userService.getAll())

const enrolledUserIds = computed(() => new Set((fullRoster.value || []).map((e) => e.userId.id)))
const availableEmployees = computed(() =>
  (allUsers.value || []).filter(
    (u) => u.role === 'EMPLOYEE' && u.isActive && !enrolledUserIds.value.has(u.id),
  ),
)

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
    resultMsg.value = `Đã ghi danh ${res.data.enrolled.length} người. Bỏ qua ${res.data.skipped.length} người (đã ghi danh từ trước).`
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
    resultMsg.value = `Đã ghi danh ${res.data.enrolled.length} người. Bỏ qua ${res.data.skipped.length} người (đã ghi danh từ trước).`
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
  <div class="page">
    <div class="detail-header">
      <button class="back-btn" @click="router.push({ name: 'manage-courses' })"><ChevronLeft :size="18" /></button>
      <div class="detail-heading">
        <h1>Ghi danh — {{ course?.title }}</h1>
        <div class="detail-meta">{{ fullRoster?.length ?? 0 }} người đã ghi danh</div>
      </div>
    </div>

    <p v-if="course && !course.isActive" class="alert alert-error">
      Khoá học đang bị ẩn — hãy mở lại khoá học ở trang Quản lý khoá học trước khi ghi danh thêm người.
    </p>
    <p v-if="resultMsg" class="alert alert-success">{{ resultMsg }}</p>
    <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>

    <div class="manage-layout">
      <div class="manage-table-wrap">
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
            <div class="roster-row-dept">{{ e.userId.storeId ? e.userId.storeId.name : '—' }}</div>
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
        <div class="card">
          <h2>Ghi danh theo siêu thị</h2>
          <div class="form-field">
            <select v-model="selectedStoreId" class="input" :disabled="!course?.isActive">
              <option :value="null">-- Chọn siêu thị --</option>
              <option v-for="s in stores" :key="s.id" :value="s.id">{{ s.name }}</option>
            </select>
          </div>
          <button class="btn btn-primary" :disabled="!selectedStoreId || submitting || !course?.isActive" @click="enrollStore">
            Ghi danh cả siêu thị
          </button>
        </div>

        <div class="card">
          <h2>Ghi danh cá nhân</h2>
          <div v-if="availableEmployees.length === 0" class="state-text">Tất cả nhân viên đã được ghi danh.</div>
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
                <span class="employee-pick-dept">{{ u.storeId ? u.storeId.name : '—' }}</span>
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
</template>

<style scoped src="./EnrollmentManageView.css"></style>
