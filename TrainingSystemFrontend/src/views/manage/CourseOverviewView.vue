<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import TrainerCourseNav from '@/components/trainer/TrainerCourseNav.vue'
import CourseAccessError from '@/components/trainer/CourseAccessError.vue'
import courseService from '@/api/courseService'
import lessonService from '@/api/lessonService'
import testService from '@/api/testService'
import enrollmentService from '@/api/enrollmentService'
import { formatDate } from '@/utils/formatDate'

const route = useRoute()
const courseId = Number(route.params.courseId)

const { data: course, error: courseError } = useAsyncData(() => courseService.getCourseById(courseId))
const { data: lessons } = useAsyncData(() => lessonService.getByCourse(courseId))
const { data: tests } = useAsyncData(() => testService.getByCourse(courseId))
const { data: roster } = useAsyncData(() => enrollmentService.getRoster(courseId))

const completedCount = computed(() => (roster.value || []).filter((e) => e.progressPercent >= 100).length)
const avgProgress = computed(() => {
  const list = roster.value || []
  if (list.length === 0) return 0
  const total = list.reduce((sum, e) => sum + (e.progressPercent || 0), 0)
  return Math.round(total / list.length)
})

const stats = computed(() => [
  { label: 'Bài học', value: lessons.value?.length ?? '-' },
  { label: 'Bài kiểm tra', value: tests.value?.length ?? '-' },
  { label: 'Học viên', value: roster.value?.length ?? '-' },
  { label: 'Đã hoàn thành', value: `${completedCount.value}/${roster.value?.length ?? 0}` },
  { label: 'Tiến độ trung bình', value: `${avgProgress.value}%` },
])
</script>

<template>
  <CourseAccessError v-if="courseError" :message="courseError.response?.data" />
  <div v-else class="page trainer-page">
    <TrainerCourseNav :course="course" active="overview" />

    <div class="trainer-panel course-overview-stats">
      <div class="stat-rail">
        <div v-for="s in stats" :key="s.label" class="stat-rail-item">
          <div class="stat-rail-value">{{ s.value }}</div>
          <div class="stat-rail-label">{{ s.label }}</div>
        </div>
      </div>
    </div>

    <section class="trainer-panel course-overview-info">
      <h2>Thông tin khóa học</h2>
      <p class="course-overview-desc">{{ course?.description || 'Chưa có mô tả cho khóa học này.' }}</p>
      <div class="course-overview-row"><span>Người tạo</span><strong>{{ course?.createdBy?.name || '-' }}</strong></div>
      <div class="course-overview-row"><span>Chuỗi áp dụng</span><strong>{{ course?.chains?.length ? course.chains.map((c) => c.name).join(', ') : 'Toàn bộ chuỗi' }}</strong></div>
      <div class="course-overview-row"><span>Vùng áp dụng</span><strong>{{ course?.regions?.length ? course.regions.map((r) => r.name).join(', ') : 'Toàn bộ vùng' }}</strong></div>
      <div class="course-overview-row"><span>Ngày tạo</span><strong>{{ course ? formatDate(course.createdAt) : '-' }}</strong></div>
      <div class="course-overview-row"><span>Cập nhật gần nhất</span><strong>{{ course ? formatDate(course.updatedAt) : '-' }}</strong></div>
      <div class="course-overview-row"><span>Trạng thái</span><strong>{{ course?.isActive ? 'Đang mở' : 'Đang ẩn' }}</strong></div>
    </section>
  </div>
</template>

<style scoped src="./CourseOverviewView.css"></style>
