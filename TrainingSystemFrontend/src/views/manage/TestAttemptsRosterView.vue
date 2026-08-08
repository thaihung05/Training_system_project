<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import testService from '@/api/testService'
import testAttemptService from '@/api/testAttemptService'
import { ChevronLeft } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const courseId = Number(route.params.courseId)
const testId = Number(route.params.testId)

const { data: tests } = useAsyncData(() => testService.getByCourse(courseId))
const currentTest = computed(() => (tests.value || []).find((t) => t.id === testId) || null)

const { data: attempts, loading } = useAsyncData(() => testAttemptService.getByTest(testId))

function formatDate(ms) {
  if (!ms) return 'Chưa nộp'
  return new Date(ms).toLocaleString('vi-VN')
}
</script>

<template>
  <div class="page">
    <div class="detail-header">
      <button class="back-btn" @click="router.push({ name: 'manage-tests', params: { courseId } })">
        <ChevronLeft :size="18" />
      </button>
      <div class="detail-heading">
        <h1>Kết quả — {{ currentTest?.title }}</h1>
        <div class="detail-meta">{{ attempts?.length ?? 0 }} lượt làm bài</div>
      </div>
    </div>

    <div class="manage-table-wrap">
      <div class="attempts-table-header">
        <div>NHÂN VIÊN</div>
        <div>LẦN</div>
        <div>ĐIỂM</div>
        <div>KẾT QUẢ</div>
        <div>NGÀY NỘP</div>
      </div>
      <p v-if="loading" class="state-text">Đang tải...</p>
      <div v-else-if="attempts.length === 0" class="empty-state">Chưa có ai làm bài kiểm tra này.</div>
      <template v-else>
        <div v-for="a in attempts" :key="a.id" class="attempts-row">
          <div class="attempts-name">{{ a.userId.name }}</div>
          <div class="attempts-mono">Lần {{ a.attemptNo }}</div>
          <div class="attempts-mono">{{ a.score }}%</div>
          <div>
            <span v-if="a.submittedAt" class="badge" :class="a.passed ? 'badge-success' : 'badge-danger'">
              {{ a.passed ? 'Đạt' : 'Chưa đạt' }}
            </span>
            <span v-else class="badge badge-neutral">Đang làm</span>
          </div>
          <div class="attempts-mono">{{ formatDate(a.submittedAt) }}</div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped src="./TestAttemptsRosterView.css"></style>
