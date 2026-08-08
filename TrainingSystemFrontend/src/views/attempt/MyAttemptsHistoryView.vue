<script setup>
import { useAsyncData } from '@/composables/useAsyncData'
import testAttemptService from '@/api/testAttemptService'

const { data: attempts, loading } = useAsyncData(() => testAttemptService.getMy())

function formatDate(ms) {
  if (!ms) return ''
  return new Date(ms).toLocaleString('vi-VN')
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Lịch sử làm bài kiểm tra</h1>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <div v-else-if="attempts.length === 0" class="empty-state">Bạn chưa làm bài kiểm tra nào.</div>
    <div v-else class="manage-table-wrap">
      <div class="history-table-header">
        <div>BÀI KIỂM TRA</div>
        <div>KHOÁ HỌC</div>
        <div>LẦN</div>
        <div>ĐIỂM</div>
        <div>KẾT QUẢ</div>
        <div>NGÀY NỘP</div>
        <div></div>
      </div>
      <div v-for="a in attempts" :key="a.id" class="history-row">
        <div class="history-title">{{ a.testId.title }}</div>
        <div class="history-course">{{ a.testId.courseId?.title }}</div>
        <div class="history-mono">Lần {{ a.attemptNo }}</div>
        <div class="history-mono">{{ a.score }}%</div>
        <div>
          <span v-if="a.submittedAt" class="badge" :class="a.passed ? 'badge-success' : 'badge-danger'">
            {{ a.passed ? 'Đạt' : 'Chưa đạt' }}
          </span>
          <span v-else class="badge badge-neutral">Đang làm</span>
        </div>
        <div class="history-mono">{{ formatDate(a.submittedAt) }}</div>
        <div>
          <RouterLink
            v-if="a.submittedAt"
            :to="{ name: 'attempt-result', params: { attemptId: a.id }, query: { courseId: a.testId.courseId?.id } }"
            class="manage-action"
          >
            Xem chi tiết
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./MyAttemptsHistoryView.css"></style>
