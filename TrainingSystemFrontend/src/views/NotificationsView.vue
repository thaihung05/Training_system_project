<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Trash2 } from '@lucide/vue'
import { useLazyList } from '@/composables/useLazyList'
import notificationService from '@/api/notificationService'
import { formatDateTime as formatDate } from '@/utils/formatDate'

const router = useRouter()
const unreadOnly = ref(false)

const { items: notifications, loading, loadingMore, hasMore, loadMore, reload } = useLazyList(
  (page, size) => notificationService.getMy(unreadOnly.value, page, size),
  20,
)

function setFilter(value) {
  unreadOnly.value = value
  reload()
}

async function markRead(n) {
  if (n.isRead) return
  await notificationService.markRead(n.id)
  n.isRead = true
}

function openNotification(n) {
  markRead(n)
  if (n.link) {
    router.push(n.link)
  }
}

async function markAllRead() {
  await notificationService.markAllRead()
  reload()
}

async function remove(n) {
  await notificationService.remove(n.id)
  notifications.value = notifications.value.filter((x) => x.id !== n.id)
}
</script>

<template>
  <div class="page">
    <div class="notif-header">
      <h1>Thông báo</h1>
      <button class="btn btn-secondary btn-sm" @click="markAllRead">Đánh dấu tất cả đã đọc</button>
    </div>

    <div class="notif-filters">
      <button class="filter-chip" :class="{ 'filter-chip--active': !unreadOnly }" @click="setFilter(false)">Tất cả</button>
      <button class="filter-chip" :class="{ 'filter-chip--active': unreadOnly }" @click="setFilter(true)">Chưa đọc</button>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <div v-else-if="notifications.length === 0" class="empty-state">Không có thông báo nào.</div>

    <template v-else>
      <div class="notif-list">
        <div
          v-for="n in notifications"
          :key="n.id"
          class="notif-row"
          :class="{ 'notif-row--unread': !n.isRead }"
          @click="openNotification(n)"
        >
          <span v-if="!n.isRead" class="notif-dot"></span>
          <div class="notif-body">
            <div class="notif-title">{{ n.title }}</div>
            <div class="notif-content">{{ n.content }}</div>
            <div class="notif-date">{{ formatDate(n.createdAt) }}</div>
          </div>
          <button class="notif-delete" @click.stop="remove(n)"><Trash2 :size="15" /></button>
        </div>
      </div>
      <button v-if="hasMore" class="btn btn-secondary notif-load-more" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
      </button>
    </template>
  </div>
</template>

<style scoped src="./NotificationsView.css"></style>
