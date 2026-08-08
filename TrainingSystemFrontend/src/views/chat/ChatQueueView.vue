<script setup>
import { ref } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import chatService from '@/api/chatService'
import { showError } from '@/utils/alerts'

const { data: pending, loading, refresh } = useAsyncData(() => chatService.getPending())

const answerDrafts = ref({})
const submitting = ref(null)

async function submitAnswer(item) {
  const text = (answerDrafts.value[item.id] || '').trim()
  if (!text) return
  submitting.value = item.id
  try {
    await chatService.answer(item.id, text)
    answerDrafts.value[item.id] = ''
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Có lỗi xảy ra.')
  } finally {
    submitting.value = null
  }
}

function formatDate(ms) {
  if (!ms) return ''
  return new Date(ms).toLocaleString('vi-VN')
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Câu hỏi chờ trả lời</h1>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <div v-else-if="pending.length === 0" class="empty-state">Không có câu hỏi nào đang chờ trả lời.</div>
    <div v-else class="queue-list">
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
  </div>
</template>

<style scoped src="./ChatQueueView.css"></style>
