<script setup>
import { computed } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import userService from '@/api/userService'
import courseService from '@/api/courseService'
import departmentService from '@/api/departmentService'
import pointService from '@/api/pointService'

const { data: users } = useAsyncData(() => userService.getAll())
const { data: courses } = useAsyncData(() => courseService.getCourses())
const { data: departments } = useAsyncData(() => departmentService.getAll())
const { data: pointsIssued } = useAsyncData(() => pointService.getTotalIssued())

const activeCourses = computed(() => (courses.value || []).filter((c) => c.isActive).length)
const employeeCount = computed(() => (users.value || []).filter((u) => u.role === 'EMPLOYEE').length)
const trainerCount = computed(() => (users.value || []).filter((u) => u.role === 'TRAINER').length)
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <div class="page-header">
        <h1>Tổng quan hệ thống</h1>
      </div>

      <div class="admin-stats">
        <div class="stat-card">
          <div class="stat-body">
            <div class="stat-subtitle">Tổng người dùng</div>
            <div class="stat-mono">{{ users?.length ?? 0 }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-body">
            <div class="stat-subtitle">Khoá học đang mở</div>
            <div class="stat-mono">{{ activeCourses }}/{{ courses?.length ?? 0 }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-body">
            <div class="stat-subtitle">Phòng ban</div>
            <div class="stat-mono">{{ departments?.length ?? 0 }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-body">
            <div class="stat-subtitle">Tổng điểm đã phát</div>
            <div class="stat-mono stat-mono--gold">{{ pointsIssued?.totalPointsIssued ?? 0 }}</div>
          </div>
        </div>
      </div>

      <div class="admin-hub">
        <RouterLink :to="{ name: 'admin-users' }" class="card card--link admin-hub-card">
          <div class="admin-hub-title">Quản lý người dùng</div>
          <div class="admin-hub-desc">{{ employeeCount }} nhân viên · {{ trainerCount }} trainer</div>
        </RouterLink>
        <RouterLink :to="{ name: 'admin-departments' }" class="card card--link admin-hub-card">
          <div class="admin-hub-title">Quản lý phòng ban</div>
          <div class="admin-hub-desc">{{ departments?.length ?? 0 }} phòng ban</div>
        </RouterLink>
        <RouterLink :to="{ name: 'manage-courses' }" class="card card--link admin-hub-card">
          <div class="admin-hub-title">Quản lý khoá học</div>
          <div class="admin-hub-desc">{{ courses?.length ?? 0 }} khoá học</div>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped src="./AdminDashboardView.css"></style>
