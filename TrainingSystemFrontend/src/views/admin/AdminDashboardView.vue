<script setup>
import { computed } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import AdminSidebar from '@/components/nav/AdminSidebar.vue'
import userService from '@/api/userService'
import courseService from '@/api/courseService'
import storeService from '@/api/storeService'
import pointService from '@/api/pointService'

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
      <div class="page-header">
        <h1>Tổng quan hệ thống</h1>
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

      <div class="admin-hub">
        <RouterLink :to="{ name: 'admin-users' }" class="card card--link admin-hub-card">
          <div class="admin-hub-title">Quản lý người dùng</div>
          <div class="admin-hub-desc">{{ employeeCount }} nhân viên · {{ trainerCount }} trainer</div>
        </RouterLink>
        <RouterLink :to="{ name: 'admin-stores' }" class="card card--link admin-hub-card">
          <div class="admin-hub-title">Quản lý siêu thị</div>
          <div class="admin-hub-desc">{{ stores?.length ?? 0 }} siêu thị</div>
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
