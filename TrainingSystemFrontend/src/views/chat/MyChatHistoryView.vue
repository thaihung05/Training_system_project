<script setup>
import { useLazyList } from '@/composables/useLazyList'
import chatService from '@/api/chatService'

const { items: history, loading, loadingMore, hasMore, loadMore } = useLazyList(
  (page, size) => chatService.getMy(page, size),
  15,
)

function formatDate(ms) {
  if (!ms) return ''
  return new Date(ms).toLocaleString('vi-VN')
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Câu hỏi của tôi</h1>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <div v-else-if="history.length === 0" class="empty-state">Bạn chưa đặt câu hỏi nào cho trợ lý ảo.</div>

    <template v-else>
      <div class="mychat-list">
        <div v-for="h in history" :key="h.id" class="card mychat-card">
          <div class="mychat-date">{{ formatDate(h.createdAt) }}</div>
          <div class="mychat-question">{{ h.question }}</div>
          <div v-if="h.answer" class="mychat-answer">{{ h.answer }}</div>
          <div v-else class="mychat-pending">Đang chờ trả lời...</div>
        </div>
      </div>
      <button v-if="hasMore" class="btn btn-secondary mychat-load-more" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
      </button>
    </template>
  </div>
</template>

<style scoped src="./MyChatHistoryView.css"></style>
