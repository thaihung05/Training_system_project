<script setup>
import { ref, computed, watch, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import { useLazyList } from '@/composables/useLazyList'
import courseService from '@/api/courseService'
import lessonService from '@/api/lessonService'
import enrollmentService from '@/api/enrollmentService'
import lessonProgressService from '@/api/lessonProgressService'
import testService from '@/api/testService'
import testAttemptService from '@/api/testAttemptService'
import forumService from '@/api/forumService'
import { showError } from '@/utils/alerts'
import { formatDate, formatDateTime as formatForumDate } from '@/utils/formatDate'
import { ChevronLeft, ChevronRight, Check, CheckCheck, FileText, ExternalLink } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const courseId = Number(route.params.id)

const VALID_TABS = ['lessons', 'tests', 'forum']
const activeTab = ref(VALID_TABS.includes(route.query.tab) ? route.query.tab : 'lessons')

const { data: course, error: courseError } = useAsyncData(() => courseService.getCourseById(courseId))
const { data: lessons, loading: lessonsLoading, error: lessonsError } = useAsyncData(() =>
  lessonService.getByCourse(courseId),
)
const { data: myEnrollments, refresh: refreshEnrollments } = useAsyncData(() =>
  enrollmentService.getMyEnrollments(),
)

const myEnrollment = computed(() =>
  (myEnrollments.value || []).find((e) => e.courseId.id === courseId) || null,
)

const progressList = ref([])
async function loadProgress() {
  if (!myEnrollment.value) return
  const res = await enrollmentService.getProgress(myEnrollment.value.id)
  progressList.value = res.data
}
watch(myEnrollment, loadProgress, { immediate: true })

const activeLessonId = ref(null)
watch(lessons, (list) => {
  if (list && list.length > 0 && activeLessonId.value === null) {
    activeLessonId.value = list[0].id
  }
})

const activeIndex = computed(() =>
  (lessons.value || []).findIndex((l) => l.id === activeLessonId.value),
)
const activeLesson = computed(() => (lessons.value || [])[activeIndex.value] || null)

function progressFor(lessonId) {
  return progressList.value.find((p) => p.lessonId.id === lessonId) || null
}
const activeProgress = computed(() => progressFor(activeLessonId.value))

function selectLesson(id) {
  activeLessonId.value = id
}
function goPrev() {
  if (activeIndex.value > 0) activeLessonId.value = lessons.value[activeIndex.value - 1].id
}
function goNext() {
  if (activeIndex.value < lessons.value.length - 1) activeLessonId.value = lessons.value[activeIndex.value + 1].id
}

const completing = ref(false)
async function markComplete() {
  if (!activeProgress.value || activeProgress.value.isCompleted) return
  completing.value = true
  try {
    await lessonProgressService.markComplete(activeProgress.value.id)
    await loadProgress()
    await refreshEnrollments()
  } finally {
    completing.value = false
  }
}

const { data: tests, loading: testsLoading, error: testsError } = useAsyncData(() => testService.getByCourse(courseId))
const { data: myAttempts } = useAsyncData(() => testAttemptService.getMy())

function attemptsForTest(testId) {
  return (myAttempts.value || []).filter((a) => a.testId.id === testId)
}
function openAttemptFor(testId) {
  return attemptsForTest(testId).find((a) => !a.submittedAt) || null
}
function hasPassed(testId) {
  return attemptsForTest(testId).some((a) => a.passed)
}

const startingTestId = ref(null)
async function startTest(t) {
  startingTestId.value = t.id
  try {
    const res = await testAttemptService.start(t.id)
    router.push({ name: 'attempt-take', params: { testId: t.id, attemptId: res.data.id }, query: { courseId } })
  } catch (err) {
    showError(err.response?.data || 'Không thể bắt đầu làm bài.')
  } finally {
    startingTestId.value = null
  }
}

function resumeTest(t) {
  const open = openAttemptFor(t.id)
  if (open) router.push({ name: 'attempt-take', params: { testId: t.id, attemptId: open.id }, query: { courseId } })
}

const canAnswerForum = ref(false)
const { items: forumQuestions, loading: forumLoading, loadingMore: forumLoadingMore, hasMore: forumHasMore, error: forumError, loadMore: loadMoreForum, reload: reloadForum } = useLazyList(
  async (page, size) => {
    const res = await forumService.getByCourse(courseId, page, size)
    canAnswerForum.value = res.data.canAnswer
    return { data: res.data.questions }
  },
  8,
)

const questionDraft = ref('')
const asking = ref(false)
const askError = ref('')

async function askQuestion() {
  const content = questionDraft.value.trim()
  if (!content) return
  asking.value = true
  askError.value = ''
  try {
    await forumService.ask(courseId, content)
    questionDraft.value = ''
    await reloadForum()
  } catch (err) {
    askError.value = err.response?.data || 'Không gửi được câu hỏi.'
  } finally {
    asking.value = false
  }
}

const answerDrafts = reactive({})
const answering = reactive({})
const answerErrors = reactive({})

async function submitAnswer(questionId) {
  const content = (answerDrafts[questionId] || '').trim()
  if (!content) return
  answering[questionId] = true
  answerErrors[questionId] = ''
  try {
    await forumService.answer(questionId, content)
    answerDrafts[questionId] = ''
    await reloadForum()
  } catch (err) {
    answerErrors[questionId] = err.response?.data || 'Không gửi được câu trả lời.'
  } finally {
    answering[questionId] = false
  }
}

</script>

<template>
  <div class="page">
    <p v-if="courseError" class="alert alert-error">{{ courseError.response?.data || 'Không tải được thông tin khoá học.' }}</p>
    <div class="detail-header">
      <button class="back-btn" @click="router.back()"><ChevronLeft :size="18" /></button>
      <div class="detail-heading">
        <h1>{{ course?.title }}</h1>
        <div class="detail-meta">
          <span v-if="myEnrollment">{{ myEnrollment.progressPercent }}% hoàn thành · </span>{{ lessons?.length ?? 0 }} bài
          <span v-if="course?.createdBy"> · Tạo bởi {{ course.createdBy.name }} ngày {{ formatDate(course.createdAt) }}</span>
        </div>
      </div>
    </div>

    <div class="detail-tabs">
      <div class="detail-tab" :class="{ 'detail-tab--active': activeTab === 'lessons' }" @click="activeTab = 'lessons'">
        Bài học
      </div>
      <div class="detail-tab" :class="{ 'detail-tab--active': activeTab === 'tests' }" @click="activeTab = 'tests'">
        Bài kiểm tra
      </div>
      <div class="detail-tab" :class="{ 'detail-tab--active': activeTab === 'forum' }" @click="activeTab = 'forum'">
        Diễn đàn
      </div>
    </div>

    <template v-if="activeTab === 'lessons'">
      <p v-if="lessonsLoading" class="state-text">Đang tải...</p>
      <p v-else-if="lessonsError" class="alert alert-error">{{ lessonsError.response?.data || 'Không tải được danh sách bài học.' }}</p>
      <div v-else class="detail-body">
        <div v-if="activeLesson" class="lesson-main">
          <div class="lesson-viewer">
            <a v-if="activeLesson.slidePdfUrl" :href="activeLesson.slidePdfUrl" target="_blank" class="lesson-pdf-link">
              <FileText :size="15" /> Mở tài liệu bài học <ExternalLink :size="13" />
            </a>
            <span v-else class="lesson-pdf-empty">Bài học chưa có tài liệu.</span>
          </div>
          <div class="lesson-info-card">
            <div class="lesson-info-title">Bài {{ activeIndex + 1 }}: {{ activeLesson.title }}</div>
          </div>
          <div class="lesson-nav">
            <button class="btn btn-secondary" :disabled="activeIndex <= 0" @click="goPrev">
              <ChevronLeft :size="15" /> Bài trước
            </button>
            <button
              v-if="myEnrollment"
              class="btn"
              :class="activeProgress?.isCompleted ? 'btn-secondary' : 'btn-success'"
              :disabled="completing || activeProgress?.isCompleted"
              @click="markComplete"
            >
              <CheckCheck v-if="activeProgress?.isCompleted" :size="15" />
              <Check v-else :size="15" />
              {{ activeProgress?.isCompleted ? 'Đã hoàn thành' : 'Hoàn thành' }}
            </button>
            <button class="btn btn-primary" :disabled="activeIndex >= lessons.length - 1" @click="goNext">
              Bài tiếp <ChevronRight :size="15" />
            </button>
          </div>
        </div>
        <div v-else class="empty-state">Khoá học chưa có bài học nào.</div>

        <aside v-if="lessons && lessons.length > 0" class="lesson-sidebar">
          <div class="lesson-sidebar-header">
            <div class="lesson-sidebar-title">Nội dung khoá học</div>
            <div class="lesson-sidebar-sub">{{ lessons.length }} bài</div>
            <div v-if="myEnrollment" class="progress-track">
              <div class="progress-fill" :style="{ width: myEnrollment.progressPercent + '%' }"></div>
            </div>
          </div>
          <div
            v-for="l in lessons"
            :key="l.id"
            class="lesson-row"
            :class="{ 'lesson-row--active': l.id === activeLessonId }"
            @click="selectLesson(l.id)"
          >
            <span class="lesson-row-check" :class="{ 'lesson-row-check--done': progressFor(l.id)?.isCompleted }">
              <Check v-if="progressFor(l.id)?.isCompleted" :size="11" />
            </span>
            <span class="lesson-row-title">{{ l.title }}</span>
          </div>
        </aside>
      </div>
    </template>

    <template v-else-if="activeTab === 'tests'">
      <p v-if="testsLoading" class="state-text">Đang tải...</p>
      <p v-else-if="testsError" class="alert alert-error">{{ testsError.response?.data || 'Không tải được danh sách bài kiểm tra.' }}</p>
      <div v-else-if="tests.length === 0" class="empty-state">Khoá học chưa có bài kiểm tra nào.</div>
      <div v-else class="test-tab-list">
        <div v-for="t in tests" :key="t.id" class="card test-tab-card">
          <div class="test-tab-header">
            <div class="test-tab-title">{{ t.title }}</div>
            <span v-if="hasPassed(t.id)" class="badge badge-success">Đã đạt</span>
          </div>
          <div class="test-tab-meta">
            <span>Điểm đạt: {{ t.passScore }}%</span>
            <span>Số lần làm: {{ attemptsForTest(t.id).length }}/{{ t.maxAttempts }}</span>
          </div>
          <button v-if="openAttemptFor(t.id)" class="btn btn-primary" @click="resumeTest(t)">
            Tiếp tục làm bài
          </button>
          <button
            v-else-if="attemptsForTest(t.id).length < t.maxAttempts"
            class="btn btn-primary"
            :disabled="startingTestId === t.id"
            @click="startTest(t)"
          >
            Bắt đầu làm bài
          </button>
          <button v-else class="btn btn-secondary" disabled>Đã hết lượt làm bài</button>
        </div>
      </div>
    </template>

    <template v-if="activeTab === 'forum'">
      <div class="forum-ask card">
        <textarea
          v-model="questionDraft"
          class="input forum-ask-input"
          rows="3"
          placeholder="Đặt câu hỏi cho trainer về khoá học này..."
        ></textarea>
        <p v-if="askError" class="alert alert-error">{{ askError }}</p>
        <button class="btn btn-primary" :disabled="asking || !questionDraft.trim()" @click="askQuestion">
          {{ asking ? 'Đang gửi...' : 'Đặt câu hỏi' }}
        </button>
      </div>

      <p v-if="forumLoading" class="state-text">Đang tải...</p>
      <p v-else-if="forumError" class="alert alert-error">{{ forumError.response?.data || 'Không tải được diễn đàn.' }}</p>
      <div v-else-if="forumQuestions.length === 0" class="empty-state">Chưa có câu hỏi nào trong diễn đàn khoá học này.</div>

      <template v-else>
        <div class="forum-list">
          <div v-for="q in forumQuestions" :key="q.id" class="card forum-question-card">
            <div class="forum-question-head">
              <span class="forum-author">{{ q.userId.name }}</span>
              <span class="forum-date">{{ formatForumDate(q.createdAt) }}</span>
            </div>
            <div class="forum-question-content">{{ q.content }}</div>

            <div v-if="q.answers.length > 0" class="forum-answer-list">
              <div v-for="a in q.answers" :key="a.id" class="forum-answer-row">
                <div class="forum-answer-head">
                  <span class="forum-author forum-author--trainer">{{ a.userId.name }}</span>
                  <span class="forum-date">{{ formatForumDate(a.createdAt) }}</span>
                </div>
                <div class="forum-answer-content">{{ a.content }}</div>
              </div>
            </div>
            <div v-else class="forum-pending">Chưa có câu trả lời.</div>

            <div v-if="canAnswerForum" class="forum-answer-form">
              <input
                v-model="answerDrafts[q.id]"
                type="text"
                class="input"
                placeholder="Trả lời câu hỏi này..."
                @keyup.enter="submitAnswer(q.id)"
              />
              <button
                class="btn btn-secondary btn-sm"
                :disabled="answering[q.id] || !(answerDrafts[q.id] || '').trim()"
                @click="submitAnswer(q.id)"
              >
                Trả lời
              </button>
            </div>
            <p v-if="answerErrors[q.id]" class="alert alert-error">{{ answerErrors[q.id] }}</p>
          </div>
        </div>
        <button v-if="forumHasMore" class="btn btn-secondary forum-load-more" :disabled="forumLoadingMore" @click="loadMoreForum">
          {{ forumLoadingMore ? 'Đang tải...' : 'Tải thêm' }}
        </button>
      </template>
    </template>
  </div>
</template>

<style scoped src="./CourseDetailView.css"></style>
