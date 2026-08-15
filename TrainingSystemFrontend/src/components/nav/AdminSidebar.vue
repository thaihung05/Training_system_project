<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Award, ChevronLeft, Coins, LayoutDashboard, LogOut, Map, Network, Store, Users } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const navGroups = [
  {
    label: 'Điều hành',
    items: [
      { name: 'admin-dashboard', label: 'Tổng quan', icon: LayoutDashboard },
      { name: 'admin-users', label: 'Người dùng', icon: Users },
    ],
  },
  {
    label: 'Cơ cấu bán lẻ',
    items: [
      { name: 'admin-organization', query: { tab: 'stores' }, label: 'Siêu thị', icon: Store },
      { name: 'admin-organization', query: { tab: 'chains' }, label: 'Chuỗi', icon: Network },
      { name: 'admin-organization', query: { tab: 'regions' }, label: 'Vùng', icon: Map },
    ],
  },
  {
    label: 'Ghi nhận',
    items: [
      { name: 'admin-point-rules', label: 'Quy tắc điểm', icon: Coins },
      { name: 'admin-badges', label: 'Huy hiệu', icon: Award },
    ],
  },
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
        <div class="admin-sidebar-title">TLH Training</div>
        <div class="admin-sidebar-sub">Bảng điều khiển</div>
      </div>
    </div>
    <nav class="admin-sidebar-nav">
      <div v-for="group in navGroups" :key="group.label" class="admin-sidebar-group">
        <div class="admin-sidebar-group-label">{{ group.label }}</div>
        <RouterLink
          v-for="item in group.items"
          :key="item.label"
          :to="{ name: item.name, query: item.query }"
          class="admin-sidebar-link"
          :class="{ 'admin-sidebar-link--active': route.name === item.name && (!item.query || route.query.tab === item.query.tab) }"
        >
          <component :is="item.icon" :size="17" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </div>
    </nav>
    <div class="admin-sidebar-footer">
      <div class="admin-sidebar-account">
        <span>{{ auth.user?.name?.charAt(0)?.toUpperCase() || 'A' }}</span>
        <div><strong>{{ auth.user?.name || 'Quản trị viên' }}</strong><small>ADMIN</small></div>
      </div>
      <RouterLink :to="{ name: 'home' }" class="admin-sidebar-back">
        <ChevronLeft :size="14" /> Về trang nhân viên
      </RouterLink>
      <button class="admin-sidebar-logout" @click="handleLogout"><LogOut :size="15" /> Đăng xuất</button>
    </div>
  </aside>
</template>

<style scoped src="./AdminSidebar.css"></style>
