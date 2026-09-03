<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import { useLazyList } from '@/composables/useLazyList'
import courseService from '@/api/courseService'
import testService from '@/api/testService'
import testAttemptService from '@/api/testAttemptService'
import { formatDateTime } from '@/utils/formatDate'
import TrainerCourseNav from '@/components/trainer/TrainerCourseNav.vue'
import CourseAccessError from '@/components/trainer/CourseAccessError.vue'
import { BarChart3 } from '@lucide/vue'

const route = useRoute()
const courseId = Number(route.params.courseId)
const testId = Number(route.params.testId)

const { data: course, error: courseError } = useAsyncData(() => courseService.getCourseById(courseId))
const { data: tests } = useAsyncData(() => testService.getByCourse(courseId))
const currentTest = computed(() => (tests.value || []).find((t) => t.id === testId) || null)

const { items: attempts, loading, loadingMore, hasMore, loadMore } = useLazyList(
  (page, size) => testAttemptService.getByTest(testId, page, size),
  20,
)

function formatDate(ms) {
  if (!ms) return 'Chưa nộp'
  return formatDateTime(ms)
}
</script>

<template>
  <CourseAccessError v-if="courseError" :message="courseError.response?.data" />
  <div v-else class="page trainer-page">
    <TrainerCourseNav :course="course" active="tests" />
    <div class="trainer-context-header">
      <div>
        <h2>{{ currentTest?.title || 'Đang tải bài kiểm tra…' }}</h2>
        <p>{{ attempts?.length ?? 0 }} lượt làm bài trong trang hiện tại.</p>
      </div>
    </div>

    <div class="manage-table-wrap trainer-panel">
      <div class="trainer-panel-heading">
        <div><h2>Kết quả nhân viên</h2><p>Điểm, trạng thái đạt và thời gian nộp.</p></div>
        <BarChart3 :size="19" />
      </div>
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
    <button v-if="hasMore" class="btn btn-secondary attempts-load-more" :disabled="loadingMore" @click="loadMore">
      {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
    </button>
  </div>
</template>

<style scoped src="./TestAttemptsRosterView.css"></style>
