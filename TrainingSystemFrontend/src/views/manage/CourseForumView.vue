<script setup>
import { reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import { useLazyList } from '@/composables/useLazyList'
import courseService from '@/api/courseService'
import forumService from '@/api/forumService'
import TrainerCourseNav from '@/components/trainer/TrainerCourseNav.vue'
import CourseAccessError from '@/components/trainer/CourseAccessError.vue'
import { formatDateTime as formatForumDate } from '@/utils/formatDate'
import { MessageSquare } from '@lucide/vue'

const route = useRoute()
const courseId = Number(route.params.courseId)

const { data: course, error: courseError } = useAsyncData(() => courseService.getCourseById(courseId))

const canAnswer = ref(false)
const { items: questions, loading, loadingMore, hasMore, error, loadMore, reload } = useLazyList(
  async (page, size) => {
    const res = await forumService.getByCourse(courseId, page, size)
    canAnswer.value = res.data.canAnswer
    return { data: res.data.questions }
  },
  10,
)

const answerDrafts = reactive({})
const answering = reactive({})
const answerErrors = reactive({})

const expandedAnswers = reactive({})
function isLongThread(q) {
  return q.answers.length > 1 || q.answers.some((a) => a.content.length > 220)
}
function toggleAnswers(questionId) {
  expandedAnswers[questionId] = !expandedAnswers[questionId]
}

async function submitAnswer(questionId) {
  const content = (answerDrafts[questionId] || '').trim()
  if (!content) return
  answering[questionId] = true
  answerErrors[questionId] = ''
  try {
    await forumService.answer(questionId, content)
    answerDrafts[questionId] = ''
    await reload()
  } catch (err) {
    answerErrors[questionId] = err.response?.data || 'Không gửi được câu trả lời.'
  } finally {
    answering[questionId] = false
  }
}
</script>

<template>
  <CourseAccessError v-if="courseError" :message="courseError.response?.data" />
  <div v-else class="page trainer-page">
    <TrainerCourseNav :course="course" active="forum" />

    <div class="trainer-context-header">
      <div>
        <h2>Diễn đàn khoá học</h2>
        <p>{{ questions?.length ?? 0 }} câu hỏi từ học viên trong danh sách hiện tại.</p>
      </div>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <p v-else-if="error" class="alert alert-error">{{ error.response?.data || 'Không tải được diễn đàn.' }}</p>
    <div v-else-if="questions.length === 0" class="empty-state forummg-empty">
      <MessageSquare :size="30" />
      <p>Chưa có câu hỏi nào trong diễn đàn khoá học này.</p>
    </div>

    <template v-else>
      <div class="forummg-list">
        <div v-for="q in questions" :key="q.id" class="trainer-panel forummg-card">
          <div class="forummg-head">
            <span class="forummg-author">{{ q.userId.name }}</span>
            <span class="forummg-date">{{ formatForumDate(q.createdAt) }}</span>
          </div>
          <div class="forummg-content">{{ q.content }}</div>

          <template v-if="q.answers.length > 0">
            <div
              class="forummg-answer-list"
              :class="{ 'forummg-answer-list--collapsed': isLongThread(q) && !expandedAnswers[q.id] }"
            >
              <div v-for="a in q.answers" :key="a.id" class="forummg-answer-row">
                <div class="forummg-answer-head">
                  <span class="forummg-author" :class="{ 'forummg-author--trainer': a.userId.id !== q.userId.id }">{{ a.userId.name }}</span>
                  <span class="forummg-date">{{ formatForumDate(a.createdAt) }}</span>
                </div>
                <div class="forummg-answer-content">{{ a.content }}</div>
              </div>
            </div>
            <button v-if="isLongThread(q)" type="button" class="forummg-toggle-btn" @click="toggleAnswers(q.id)">
              {{ expandedAnswers[q.id] ? 'Thu gọn câu trả lời' : `Xem đầy đủ câu trả lời (${q.answers.length})` }}
            </button>
          </template>
          <div v-else class="forummg-pending">Chưa có câu trả lời.</div>

          <div v-if="canAnswer" class="forummg-answer-form">
            <input
              v-model="answerDrafts[q.id]"
              type="text"
              class="input"
              placeholder="Trả lời câu hỏi này..."
              @keyup.enter="submitAnswer(q.id)"
            />
            <button
              class="btn btn-primary btn-sm"
              :disabled="answering[q.id] || !(answerDrafts[q.id] || '').trim()"
              @click="submitAnswer(q.id)"
            >
              Trả lời
            </button>
          </div>
          <p v-if="answerErrors[q.id]" class="alert alert-error">{{ answerErrors[q.id] }}</p>
        </div>
      </div>
      <button v-if="hasMore" class="btn btn-secondary forummg-load-more" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
      </button>
    </template>
  </div>
</template>

<style scoped src="./CourseForumView.css"></style>
