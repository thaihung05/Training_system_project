<script setup>
import { ref } from 'vue'
import { useLazyList } from '@/composables/useLazyList'
import chatService from '@/api/chatService'
import { showError } from '@/utils/alerts'
import { formatDateTime as formatDate } from '@/utils/formatDate'

const { items: pending, loading, loadingMore, hasMore, loadMore, reload } = useLazyList(
  (page, size) => chatService.getPending(page, size),
  10,
)

const answerDrafts = ref({})
const submitting = ref(null)

async function submitAnswer(item) {
  const text = (answerDrafts.value[item.id] || '').trim()
  if (!text) return
  submitting.value = item.id
  try {
    await chatService.answer(item.id, text)
    answerDrafts.value[item.id] = ''
    reload()
  } catch (err) {
    showError(err.response?.data || 'Có lỗi xảy ra.')
  } finally {
    submitting.value = null
  }
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Câu hỏi chờ trả lời</h1>
      <p class="page-subtitle">
        Câu hỏi gửi cho trợ lý ảo chung của công ty (qua khung chat nổi ở góc màn hình) — không thuộc riêng khoá học nào.
        Câu hỏi trong diễn đàn từng khoá học nằm ở tab "Diễn đàn" trong trang chi tiết khoá học đó.
      </p>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <div v-else-if="pending.length === 0" class="empty-state">Không có câu hỏi nào đang chờ trả lời.</div>
    <template v-else>
      <div class="queue-list">
        <div v-for="item in pending" :key="item.id" class="card queue-card">
          <div class="queue-meta">
            <span class="queue-user">{{ item.userId.name }}</span>
            <span class="queue-date">{{ formatDate(item.createdAt) }}</span>
          </div>
          <div class="queue-question">{{ item.question }}</div>
          <div class="queue-answer-row">
            <input
              v-model="answerDrafts[item.id]"
              type="text"
              class="input"
              placeholder="Nhập câu trả lời..."
              @keyup.enter="submitAnswer(item)"
            />
            <button
              class="btn btn-primary btn-sm"
              :disabled="submitting === item.id || !answerDrafts[item.id]?.trim()"
              @click="submitAnswer(item)"
            >
              Trả lời
            </button>
          </div>
        </div>
      </div>
      <button v-if="hasMore" class="btn btn-secondary queue-load-more" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
      </button>
    </template>
  </div>
</template>

<style scoped src="./ChatQueueView.css"></style>
