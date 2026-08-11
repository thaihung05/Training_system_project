<script setup>
import { useAuthStore } from '@/stores/auth'
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Bell, Menu, X } from '@lucide/vue'
import notificationService from '@/api/notificationService'


const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const mobileMenuOpen = ref(false)
watch(() => route.fullPath, () => { mobileMenuOpen.value = false })

const unreadCount = ref(0)
async function refreshUnreadCount() {
  if (!auth.isLoggedIn) return
  try {
    const res = await notificationService.unreadCount()
    unreadCount.value = res.data.count
  } catch {
  }
}
refreshUnreadCount()
watch(() => route.fullPath, refreshUnreadCount)

const navItems = computed(() => {
  if (auth.isTrainer) {
    return [
      { name: 'home', label: 'Trang chủ' },
      { name: 'courses', label: 'Khoá học' },
      { name: 'manage-courses', label: 'Quản lý' },
      { name: 'chat-queue', label: 'Hỏi & Đáp' },
    ]
  }

  const items = [
    { name: 'home', label: 'Trang chủ' },
    { name: 'my-courses', label: 'Khoá học của tôi' },
    { name: 'leaderboard', label: 'Thành tích' },
    { name: 'my-attempts', label: 'Lịch sử làm bài' },
    { name: 'my-chat-history', label: 'Câu hỏi của tôi' },
  ]
  if (auth.isAdmin) {
    items.splice(1, 0, { name: 'courses', label: 'Khoá học' })
    items.push({ name: 'manage-courses', label: 'Quản lý' })
    items.push({ name: 'chat-queue', label: 'Hỏi & Đáp' })
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

    <nav class="navbar-nav" :class="{ 'navbar-nav--open': mobileMenuOpen }">
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
        <span v-if="unreadCount > 0" class="navbar-bell-badge">{{ unreadCount > 9 ? '9+' : unreadCount }}</span>
      </RouterLink>
      <RouterLink :to="{ name: 'profile' }" class="navbar-user">
        <span class="navbar-avatar">{{ initials }}</span>
        <span class="navbar-user-name">{{ auth.user?.name }}</span>
      </RouterLink>
      <button class="btn btn-secondary btn-sm" @click="handleLogout">Đăng xuất</button>
      <button class="navbar-menu-toggle" @click="mobileMenuOpen = !mobileMenuOpen">
        <X v-if="mobileMenuOpen" :size="20" />
        <Menu v-else :size="20" />
      </button>
    </div>
  </div>
</template>

<style scoped src="./AppNavbar.css"></style>
