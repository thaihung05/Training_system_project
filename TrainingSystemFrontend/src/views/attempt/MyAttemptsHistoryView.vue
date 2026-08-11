<script setup>
import { useLazyList } from '@/composables/useLazyList'
import testAttemptService from '@/api/testAttemptService'
import { formatDateTime as formatDate } from '@/utils/formatDate'
import { Eye } from '@lucide/vue'

const { items: attempts, loading, loadingMore, hasMore, loadMore } = useLazyList(
  (page, size) => testAttemptService.getMy(undefined, page, size),
  15,
)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Lịch sử làm bài kiểm tra</h1>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <div v-else-if="attempts.length === 0" class="empty-state">Bạn chưa làm bài kiểm tra nào.</div>
    <template v-else>
      <div class="manage-table-wrap">
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
              class="row-action-btn"
            >
              <Eye :size="13" /> Xem chi tiết
            </RouterLink>
          </div>
        </div>
      </div>
      <button v-if="hasMore" class="btn btn-secondary history-load-more" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
      </button>
    </template>
  </div>
</template>

<style scoped src="./MyAttemptsHistoryView.css"></style>
