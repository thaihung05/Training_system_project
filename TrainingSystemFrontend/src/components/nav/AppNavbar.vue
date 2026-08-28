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
      { name: 'home', label: 'Tổng quan' },
      { name: 'courses', label: 'Danh mục' },
      { name: 'manage-courses', label: 'Khóa học phụ trách' },
      { name: 'chat-queue', label: 'Hỏi & Đáp' },
    ]
  }

  const items = [
    { name: 'home', label: 'Trang chủ' },
    { name: 'my-courses', label: 'Khoá học của tôi' },
    { name: 'my-certificates', label: 'Chứng chỉ' },
    { name: 'leaderboard', label: 'Thành tích' },
    { name: 'my-attempts', label: 'Lịch sử làm bài' },
    { name: 'my-chat-history', label: 'Câu hỏi của tôi' },
  ]
  if (auth.isAdmin) {
    const certificateIndex = items.findIndex((item) => item.name === 'my-certificates')
    if (certificateIndex >= 0) items.splice(certificateIndex, 1)
    items.splice(1, 0, { name: 'courses', label: 'Khoá học' })
    items.push({ name: 'manage-courses', label: 'Quản lý' })
    items.push({ name: 'chat-queue', label: 'Hỏi & Đáp' })
    items.push({ name: 'admin-dashboard', label: 'Quản trị' })
  }
  return items
})

const brandSub = computed(() => (
    auth.isTrainer ? 'Không gian giảng viên' : 'Đào tạo nội bộ'
  )
)

function isNavActive(item) {
  if (item.name === 'manage-courses') return route.path.startsWith('/manage/courses')
  return route.name === item.name
}

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
  <div class="navbar" :class="{ 'navbar--trainer': auth.isTrainer }">
    <RouterLink to="/" class="navbar-brand">
      <span class="navbar-mark">TLH</span>
      <span class="navbar-brand-text">
        <span class="navbar-brand-name">Training System</span>
        <span class="navbar-brand-sub">{{ brandSub }}</span>
      </span>
    </RouterLink>

    <nav class="navbar-nav" :class="{ 'navbar-nav--open': mobileMenuOpen }">
      <RouterLink
        v-for="item in navItems"
        :key="item.name"
        :to="{ name: item.name }"
        class="navbar-link"
        :class="{ 'navbar-link--active': isNavActive(item) }"
      >
        {{ item.label }}
      </RouterLink>
    </nav>

    <div class="navbar-actions">
      <span v-if="auth.isTrainer" class="navbar-role-pill">Trainer</span>
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
