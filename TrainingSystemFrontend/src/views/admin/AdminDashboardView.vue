<script setup>
import { computed } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import userService from '@/api/userService'
import courseService from '@/api/courseService'
import storeService from '@/api/storeService'
import chainService from '@/api/chainService'
import regionService from '@/api/regionService'
import pointService from '@/api/pointService'
import pointRuleService from '@/api/pointRuleService'
import badgeService from '@/api/badgeService'
import { ArrowRight, Award, BookOpen, Coins, Map, Network, Store, Users } from '@lucide/vue'

const { data: users } = useAsyncData(() => userService.getAll())
const { data: courses } = useAsyncData(() => courseService.getCourses(undefined, undefined, undefined, undefined, undefined, false))
const { data: stores } = useAsyncData(() => storeService.getAll())
const { data: chains } = useAsyncData(() => chainService.getAll())
const { data: regions } = useAsyncData(() => regionService.getAll())
const { data: pointsIssued } = useAsyncData(() => pointService.getTotalIssued())
const { data: pointRules } = useAsyncData(() => pointRuleService.getAll())
const { data: badges } = useAsyncData(() => badgeService.getAll())

const activeCourses = computed(() => (courses.value || []).filter((c) => c.isActive).length)
const employeeCount = computed(() => (users.value || []).filter((u) => u.role === 'EMPLOYEE').length)
const trainerCount = computed(() => (users.value || []).filter((u) => u.role === 'TRAINER').length)

const SYSTEM_POINT_RULE_TYPES = ['LESSON_COMPLETED', 'TEST_PASSED', 'COURSE_COMPLETED']
const SYSTEM_BADGE_CODES = ['CERT_1', 'CERT_5', 'CERT_10']

const employeesWithoutStore = computed(() => (users.value || []).filter((u) => u.role === 'EMPLOYEE' && !u.storeId).length)
const trainersWithoutStore = computed(() => (users.value || []).filter((u) => u.role === 'TRAINER' && !u.storeId).length)
const storesWithoutUsers = computed(() =>
  (stores.value || []).filter((s) => !(users.value || []).some((u) => u.storeId && u.storeId.id === s.id)).length,
)
const chainsWithoutStores = computed(() =>
  (chains.value || []).filter((c) => !(stores.value || []).some((s) => s.chainId && s.chainId.id === c.id)).length,
)
const regionsWithoutStores = computed(() =>
  (regions.value || []).filter((r) => !(stores.value || []).some((s) => s.regionId && s.regionId.id === r.id)).length,
)
const missingPointRules = computed(() => {
  const existing = (pointRules.value || []).map((r) => r.actionType)
  return SYSTEM_POINT_RULE_TYPES.filter((t) => !existing.includes(t)).length
})
const missingBadges = computed(() => {
  const existing = (badges.value || []).map((b) => b.code)
  return SYSTEM_BADGE_CODES.filter((c) => !existing.includes(c)).length
})

const healthChecks = computed(() => [
  { label: 'Nhân viên chưa gán siêu thị', count: employeesWithoutStore.value, kind: employeesWithoutStore.value > 0 ? 'error' : 'ok' },
  { label: 'Trainer chưa gán siêu thị', count: trainersWithoutStore.value, kind: trainersWithoutStore.value > 0 ? 'info' : 'ok' },
  { label: 'Siêu thị chưa có người dùng', count: storesWithoutUsers.value, kind: storesWithoutUsers.value > 0 ? 'warning' : 'ok' },
  { label: 'Chuỗi chưa có siêu thị', count: chainsWithoutStores.value, kind: chainsWithoutStores.value > 0 ? 'warning' : 'ok' },
  { label: 'Vùng chưa có siêu thị', count: regionsWithoutStores.value, kind: regionsWithoutStores.value > 0 ? 'warning' : 'ok' },
  { label: 'Thiếu quy tắc điểm hệ thống', count: missingPointRules.value, kind: missingPointRules.value > 0 ? 'error' : 'ok' },
  { label: 'Thiếu huy hiệu hệ thống', count: missingBadges.value, kind: missingBadges.value > 0 ? 'error' : 'ok' },
])
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="admin-page-heading admin-page-header">
        <div><h1>Tổng quan vận hành</h1><p>Theo dõi dữ liệu cốt lõi và đi nhanh đến các công việc quản trị thường dùng.</p></div>
        <RouterLink :to="{ name: 'manage-courses' }" class="btn btn-primary"><BookOpen :size="16" /> Quản lý khóa học</RouterLink>
      </div>

      <div class="stat-rail admin-stats">
        <div class="stat-rail-item">
          <div class="stat-rail-value">{{ users?.length ?? 0 }}</div>
          <div class="stat-rail-label">Tổng người dùng</div>
        </div>
        <div class="stat-rail-item">
          <div class="stat-rail-value">{{ activeCourses }}/{{ courses?.length ?? 0 }}</div>
          <div class="stat-rail-label">Khoá học đang mở</div>
        </div>
        <div class="stat-rail-item">
          <div class="stat-rail-value">{{ stores?.length ?? 0 }}</div>
          <div class="stat-rail-label">Siêu thị</div>
        </div>
        <div class="stat-rail-item">
          <div class="stat-rail-value stat-rail-value--gold">{{ pointsIssued?.totalPointsIssued ?? 0 }}</div>
          <div class="stat-rail-label">Tổng điểm đã phát</div>
        </div>
      </div>

      <section class="admin-dashboard-panel admin-health-panel">
        <div class="admin-dashboard-panel-heading"><div><h2>Cần kiểm tra</h2><p>Các điểm dữ liệu nên rà soát để phạm vi khóa học và cấu hình hệ thống hoạt động đúng.</p></div></div>
        <div v-for="item in healthChecks" :key="item.label" class="admin-health-row">
          <span class="admin-health-label">{{ item.label }}</span>
          <span class="admin-health-count" :class="`admin-health-count--${item.kind}`">{{ item.count }}</span>
        </div>
      </section>

      <div class="admin-dashboard-columns">
        <section class="admin-dashboard-panel">
          <div class="admin-dashboard-panel-heading"><div><h2>Tổ chức và tài khoản</h2><p>Dữ liệu nền quyết định phạm vi sử dụng hệ thống.</p></div></div>
          <RouterLink :to="{ name: 'admin-users' }" class="admin-task-row">
            <span class="admin-task-icon"><Users :size="18" /></span><div><strong>Người dùng</strong><small>{{ employeeCount }} nhân viên · {{ trainerCount }} trainer</small></div><ArrowRight :size="16" />
          </RouterLink>
          <RouterLink :to="{ name: 'admin-organization', query: { tab: 'stores' } }" class="admin-task-row">
            <span class="admin-task-icon"><Store :size="18" /></span><div><strong>Siêu thị</strong><small>{{ stores?.length ?? 0 }} đơn vị đang được quản lý</small></div><ArrowRight :size="16" />
          </RouterLink>
          <RouterLink :to="{ name: 'admin-organization', query: { tab: 'chains' } }" class="admin-task-row">
            <span class="admin-task-icon"><Network :size="18" /></span><div><strong>Chuỗi bán lẻ</strong><small>Thiết lập cơ cấu thương hiệu</small></div><ArrowRight :size="16" />
          </RouterLink>
          <RouterLink :to="{ name: 'admin-organization', query: { tab: 'regions' } }" class="admin-task-row">
            <span class="admin-task-icon"><Map :size="18" /></span><div><strong>Vùng quản lý</strong><small>Phân chia phạm vi địa lý</small></div><ArrowRight :size="16" />
          </RouterLink>
        </section>

        <section class="admin-dashboard-panel">
          <div class="admin-dashboard-panel-heading"><div><h2>Đào tạo và ghi nhận</h2><p>Quản lý nội dung học tập, điểm và thành tích.</p></div></div>
          <RouterLink :to="{ name: 'manage-courses' }" class="admin-task-row">
            <span class="admin-task-icon"><BookOpen :size="18" /></span><div><strong>Khóa học</strong><small>{{ activeCourses }}/{{ courses?.length ?? 0 }} khóa đang mở</small></div><ArrowRight :size="16" />
          </RouterLink>
          <RouterLink :to="{ name: 'admin-point-rules' }" class="admin-task-row">
            <span class="admin-task-icon"><Coins :size="18" /></span><div><strong>Quy tắc điểm</strong><small>{{ pointsIssued?.totalPointsIssued ?? 0 }} điểm đã phát</small></div><ArrowRight :size="16" />
          </RouterLink>
          <RouterLink :to="{ name: 'admin-badges' }" class="admin-task-row">
            <span class="admin-task-icon"><Award :size="18" /></span><div><strong>Huy hiệu</strong><small>Quản lý danh hiệu nhân viên</small></div><ArrowRight :size="16" />
          </RouterLink>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped src="./AdminDashboardView.css"></style>
