<script setup>
import { computed } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import userService from '@/api/userService'
import courseService from '@/api/courseService'
import storeService from '@/api/storeService'
import pointService from '@/api/pointService'
import { ArrowRight, Award, BookOpen, Coins, Map, Network, Store, Users } from '@lucide/vue'

const { data: users } = useAsyncData(() => userService.getAll())
const { data: courses } = useAsyncData(() => courseService.getCourses(undefined, undefined, undefined, undefined, undefined, false))
const { data: stores } = useAsyncData(() => storeService.getAll())
const { data: pointsIssued } = useAsyncData(() => pointService.getTotalIssued())

const activeCourses = computed(() => (courses.value || []).filter((c) => c.isActive).length)
const employeeCount = computed(() => (users.value || []).filter((u) => u.role === 'EMPLOYEE').length)
const trainerCount = computed(() => (users.value || []).filter((u) => u.role === 'TRAINER').length)
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

      <div class="admin-dashboard-columns">
        <section class="admin-dashboard-panel">
          <div class="admin-dashboard-panel-heading"><div><h2>Tổ chức và tài khoản</h2><p>Dữ liệu nền quyết định phạm vi sử dụng hệ thống.</p></div></div>
          <RouterLink :to="{ name: 'admin-users' }" class="admin-task-row">
            <span class="admin-task-icon"><Users :size="18" /></span><div><strong>Người dùng</strong><small>{{ employeeCount }} nhân viên · {{ trainerCount }} trainer</small></div><ArrowRight :size="16" />
          </RouterLink>
          <RouterLink :to="{ name: 'admin-stores' }" class="admin-task-row">
            <span class="admin-task-icon"><Store :size="18" /></span><div><strong>Siêu thị</strong><small>{{ stores?.length ?? 0 }} đơn vị đang được quản lý</small></div><ArrowRight :size="16" />
          </RouterLink>
          <RouterLink :to="{ name: 'admin-chains' }" class="admin-task-row">
            <span class="admin-task-icon"><Network :size="18" /></span><div><strong>Chuỗi bán lẻ</strong><small>Thiết lập cơ cấu thương hiệu</small></div><ArrowRight :size="16" />
          </RouterLink>
          <RouterLink :to="{ name: 'admin-regions' }" class="admin-task-row">
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
