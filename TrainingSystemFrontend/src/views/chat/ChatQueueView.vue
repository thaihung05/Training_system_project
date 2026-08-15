<script setup>
import { ref } from 'vue'
import { useLazyList } from '@/composables/useLazyList'
import chatService from '@/api/chatService'
import { showError } from '@/utils/alerts'
import { formatDateTime as formatDate } from '@/utils/formatDate'
import { Check, Clock3, MessageCircleQuestion, Send } from '@lucide/vue'

const activeTab = ref('pending')

const { items: pending, loading, loadingMore, hasMore, loadMore, reload } = useLazyList(
  (page, size) => chatService.getPending(page, size),
  10,
)
const {
  items: answered,
  loading: answeredLoading,
  loadingMore: answeredLoadingMore,
  hasMore: answeredHasMore,
  loadMore: loadMoreAnswered,
  reload: reloadAnswered,
} = useLazyList((page, size) => chatService.getAnswered(page, size), 10)

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
    reloadAnswered()
  } catch (err) {
    showError(err.response?.data || 'Có lỗi xảy ra.')
  } finally {
    submitting.value = null
  }
}
</script>

<template>
  <div class="page trainer-page trainer-queue-page">
    <header class="trainer-page-header">
      <div>
        <h1>Hộp thư hỏi đáp</h1>
        <p>Các câu hỏi gửi đến trợ lý chung của công ty. Câu hỏi thuộc khóa học vẫn được xử lý tại diễn đàn của khóa đó.</p>
      </div>
      <div class="queue-counter"><MessageCircleQuestion :size="18" /><strong>{{ pending.length }}</strong><span>đang chờ</span></div>
    </header>

    <div class="queue-tabs">
      <button class="queue-tab" :class="{ 'queue-tab--active': activeTab === 'pending' }" @click="activeTab = 'pending'">Chờ trả lời</button>
      <button class="queue-tab" :class="{ 'queue-tab--active': activeTab === 'answered' }" @click="activeTab = 'answered'">Đã trả lời</button>
    </div>

    <template v-if="activeTab === 'pending'">
      <p v-if="loading" class="state-text">Đang tải...</p>
      <div v-else-if="pending.length === 0" class="empty-state">Không có câu hỏi nào đang chờ trả lời.</div>
      <template v-else>
        <div class="queue-list">
          <div v-for="item in pending" :key="item.id" class="trainer-panel queue-card">
            <div class="queue-meta">
              <span class="queue-user"><span class="queue-avatar">{{ item.userId.name?.charAt(0) }}</span>{{ item.userId.name }}</span>
              <span class="queue-date"><Clock3 :size="13" />{{ formatDate(item.createdAt) }}</span>
            </div>
            <div class="queue-question">{{ item.question }}</div>
            <div class="queue-answer-row">
              <textarea
                v-model="answerDrafts[item.id]"
                class="input"
                rows="2"
                placeholder="Viết câu trả lời rõ ràng cho nhân viên…"
              ></textarea>
              <button
                class="btn btn-primary btn-sm"
                :disabled="submitting === item.id || !answerDrafts[item.id]?.trim()"
                @click="submitAnswer(item)"
              >
                <Send :size="15" /> Gửi trả lời
              </button>
            </div>
          </div>
        </div>
        <button v-if="hasMore" class="btn btn-secondary queue-load-more" :disabled="loadingMore" @click="loadMore">
          {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
        </button>
      </template>
    </template>

    <template v-else>
      <p v-if="answeredLoading" class="state-text">Đang tải...</p>
      <div v-else-if="answered.length === 0" class="empty-state">Chưa có câu hỏi nào được trả lời.</div>
      <template v-else>
        <div class="queue-list">
          <div v-for="item in answered" :key="item.id" class="trainer-panel queue-card queue-card--answered">
            <div class="queue-meta">
              <span class="queue-user"><span class="queue-avatar">{{ item.userId.name?.charAt(0) }}</span>{{ item.userId.name }}</span>
              <span class="queue-date"><Clock3 :size="13" />{{ formatDate(item.createdAt) }}</span>
            </div>
            <div class="queue-question">{{ item.question }}</div>
            <div class="queue-answered-row">
              <Check :size="14" />
              <span>{{ item.answer }}</span>
            </div>
          </div>
        </div>
        <button v-if="answeredHasMore" class="btn btn-secondary queue-load-more" :disabled="answeredLoadingMore" @click="loadMoreAnswered">
          {{ answeredLoadingMore ? 'Đang tải...' : 'Tải thêm' }}
        </button>
      </template>
    </template>
  </div>
</template>

<style scoped src="./ChatQueueView.css"></style>
