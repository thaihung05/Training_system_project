<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import testAttemptService from '@/api/testAttemptService'
import { confirmDialog } from '@/utils/alerts'

const route = useRoute()
const router = useRouter()
const testId = Number(route.params.testId)
const attemptId = Number(route.params.attemptId)
const courseId = route.query.courseId

const { data: questions, loading, error } = useAsyncData(() =>
  testAttemptService.getQuestionsForAttempt(testId),
)

const answers = ref({})
const submitting = ref(false)
const errorMsg = ref('')

const answeredCount = computed(() => Object.keys(answers.value).length)

async function submitAttempt() {
  if (!(await confirmDialog('Nộp bài kiểm tra? Bạn sẽ không thể sửa lại sau khi nộp.'))) return
  submitting.value = true
  errorMsg.value = ''
  const payload = {
    answers: (questions.value || []).map((q) => ({
      questionId: q.id,
      selectedOptionId: answers.value[q.id] || null,
    })),
  }
  try {
    await testAttemptService.submit(attemptId, payload)
    router.push({ name: 'attempt-result', params: { attemptId }, query: { courseId } })
  } catch (err) {
    errorMsg.value = err.response?.data || 'Nộp bài thất bại.'
    submitting.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="attempt-header">
      <h1>Đang làm bài kiểm tra</h1>
      <div class="attempt-progress">Đã trả lời {{ answeredCount }}/{{ questions?.length ?? 0 }} câu</div>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <p v-else-if="error" class="alert alert-error">{{ error.response?.data || 'Có lỗi xảy ra.' }}</p>
    <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>

    <div v-if="questions && questions.length > 0" class="attempt-list">
      <div v-for="(q, index) in questions" :key="q.id" class="card attempt-card">
        <div class="attempt-question">Câu {{ index + 1 }}: {{ q.content }}</div>
        <label v-for="opt in q.options" :key="opt.id" class="attempt-option-row">
          <input type="radio" :name="'q-' + q.id" :value="opt.id" v-model="answers[q.id]" />
          <span>{{ opt.optionText }}</span>
        </label>
      </div>
    </div>

    <button
      v-if="questions && questions.length > 0"
      class="btn btn-primary attempt-submit"
      :disabled="submitting"
      @click="submitAttempt"
    >
      Nộp bài
    </button>
  </div>
</template>

<style scoped src="./AttemptTakeView.css"></style>
