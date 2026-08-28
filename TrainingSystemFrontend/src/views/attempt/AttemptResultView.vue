<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import testAttemptService from '@/api/testAttemptService'

const route = useRoute()
const router = useRouter()
const attemptId = Number(route.params.attemptId)
const courseId = route.query.courseId

const { data: attempt, loading, error } = useAsyncData(() => testAttemptService.getById(attemptId))

function goBackToCourse() {
  if (courseId) {
    router.push({ name: 'course-detail', params: { id: courseId } })
  } else {
    router.push({ name: 'courses' })
  }
}
</script>

<template>
  <div class="page">
    <p v-if="loading" class="state-text">Đang tải...</p>
    <p v-else-if="error" class="alert alert-error">{{ error.response?.data || 'Không tải được kết quả bài làm.' }}</p>
    <div v-else-if="attempt" class="result-wrap">
      <div class="card result-card" :class="attempt.passed ? 'result-card--pass' : 'result-card--fail'">
        <div class="result-score">{{ attempt.score }}%</div>
        <div class="result-label">{{ attempt.passed ? 'ĐẠT' : 'CHƯA ĐẠT' }}</div>
        <div class="result-meta">Lần làm thứ {{ attempt.attemptNo }}</div>
      </div>

      <div class="card">
        <h2>Chi tiết từng câu</h2>
        <div class="result-answers">
          <div v-for="(a, index) in attempt.answers" :key="a.questionId" class="result-answer-row">
            <span>Câu {{ index + 1 }}</span>
            <span class="badge" :class="a.isCorrect ? 'badge-success' : 'badge-danger'">
              {{ a.isCorrect ? 'Đúng' : 'Sai' }}
            </span>
          </div>
        </div>
      </div>

      <button class="btn btn-secondary" @click="goBackToCourse">Quay lại khoá học</button>
    </div>
  </div>
</template>

<style scoped src="./AttemptResultView.css"></style>
