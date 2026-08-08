<script setup>
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Bell } from '@lucide/vue'


const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const navItems = computed(() => {
  const items = [
    { name: 'home', label: 'Trang chủ' },
    { name: 'courses', label: 'Khoá học' },
    { name: 'my-courses', label: 'Khoá học của tôi' },
    { name: 'leaderboard', label: 'Thành tích' },
    { name: 'my-attempts', label: 'Lịch sử làm bài' },
  ]
  if (auth.isTrainerOrAdmin) {
    items.push({ name: 'manage-courses', label: 'Quản lý' })
    items.push({ name: 'chat-queue', label: 'Hỏi & Đáp' })
  }
    if (auth.isAdmin) {
    items.push({ name: 'admin-dashboard', label: 'Quản trị' })
  }
  return items
})

const initials = computed(() => {
  const name = auth.user?.name || ''
  return name.split(' ').slice(-2).map((w) => w[0]).join('').toUpperCase()
})

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="navbar">
    <RouterLink to="/" class="navbar-brand">
      <span class="navbar-mark">TLH</span>
      <span class="navbar-brand-text">
        <span class="navbar-brand-name">Training System</span>
        <span class="navbar-brand-sub">Đào tạo nội bộ</span>
      </span>
    </RouterLink>

    <nav class="navbar-nav">
      <RouterLink
        v-for="item in navItems"
        :key="item.name"
        :to="{ name: item.name }"
        class="navbar-link"
        :class="{ 'navbar-link--active': route.name === item.name }"
      >
        {{ item.label }}
      </RouterLink>
    </nav>

    <div class="navbar-actions">
      <RouterLink to="/notifications" class="navbar-bell">
        <Bell :size="17" />
      </RouterLink>
      <RouterLink :to="{ name: 'profile' }" class="navbar-user">
        <span class="navbar-avatar">{{ initials }}</span>
        <span class="navbar-user-name">{{ auth.user?.name }}</span>
      </RouterLink>
      <button class="btn btn-secondary btn-sm" @click="handleLogout">Đăng xuất</button>
    </div>
  </div>
</template>

<style scoped src="./AppNavbar.css"></style>
