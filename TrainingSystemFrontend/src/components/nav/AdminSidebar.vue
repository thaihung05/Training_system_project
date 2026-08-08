<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ChevronLeft } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const navItems = [
  { name: 'admin-dashboard', label: 'Dashboard' },
  { name: 'admin-users', label: 'Quản lý người dùng' },
  { name: 'admin-departments', label: 'Phòng ban' },
  { name: 'admin-point-rules', label: 'Quy tắc điểm' },
  { name: 'admin-badges', label: 'Huy hiệu' },
]

function handleLogout() {
  auth.logout()
  router.push({ name: 'Login' })
}
</script>

<template>
  <aside class="admin-sidebar">
    <div class="admin-sidebar-header">
      <span class="admin-sidebar-mark">TLH</span>
      <div>
        <div class="admin-sidebar-title">Training System</div>
        <div class="admin-sidebar-sub">Quản trị hệ thống</div>
      </div>
    </div>
    <nav class="admin-sidebar-nav">
      <RouterLink
        v-for="item in navItems"
        :key="item.name"
        :to="{ name: item.name }"
        class="admin-sidebar-link"
        :class="{ 'admin-sidebar-link--active': route.name === item.name }"
      >
        <span class="admin-sidebar-dot"></span>{{ item.label }}
      </RouterLink>
    </nav>
    <div class="admin-sidebar-footer">
      <RouterLink :to="{ name: 'home' }" class="admin-sidebar-back">
        <ChevronLeft :size="14" /> Về trang nhân viên
      </RouterLink>
      <button class="admin-sidebar-logout" @click="handleLogout">Đăng xuất</button>
    </div>
  </aside>
</template>

<style scoped src="./AdminSidebar.css"></style>
