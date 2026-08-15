<script setup>
import { computed } from 'vue'
import { scopeLabel } from '@/utils/courseScope'
import { ArrowLeft, Award, BookOpen, ClipboardCheck, LayoutDashboard, MessageSquare, Users } from '@lucide/vue'

const props = defineProps({
  course: { type: Object, default: null },
  active: { type: String, default: 'overview' },
})

const steps = computed(() => [
  { key: 'overview', label: 'Tổng quan', icon: LayoutDashboard, to: { name: 'course-overview', params: { courseId: props.course?.id } } },
  { key: 'lessons', label: 'Bài học', icon: BookOpen, to: { name: 'manage-lessons', params: { courseId: props.course?.id } } },
  { key: 'tests', label: 'Kiểm tra', icon: ClipboardCheck, to: { name: 'manage-tests', params: { courseId: props.course?.id } } },
  { key: 'enrollments', label: 'Ghi danh', icon: Users, to: { name: 'course-enrollments', params: { courseId: props.course?.id } } },
  { key: 'certificates', label: 'Chứng chỉ', icon: Award, to: { name: 'course-certificates', params: { courseId: props.course?.id } } },
  { key: 'forum', label: 'Diễn đàn', icon: MessageSquare, to: { name: 'course-forum', params: { courseId: props.course?.id } } },
])
</script>

<template>
  <section class="trainer-course-hero">
    <div class="trainer-course-hero__top">
      <RouterLink :to="{ name: 'manage-courses' }" class="trainer-course-back">
        <ArrowLeft :size="16" /> Tất cả khóa học
      </RouterLink>
      <span v-if="course" class="trainer-course-status" :class="{ 'trainer-course-status--muted': !course.isActive }">
        {{ course.isActive ? 'Đang mở' : 'Đang ẩn' }}
      </span>
    </div>

    <div class="trainer-course-hero__body">
      <div>
        <h1>{{ course?.title || 'Đang tải khóa học…' }}</h1>
        <p>{{ course ? scopeLabel(course) : 'Đang tải phạm vi áp dụng' }}</p>
      </div>
    </div>

    <nav v-if="course" class="trainer-course-steps" aria-label="Các bước quản lý khóa học">
      <RouterLink
        v-for="step in steps"
        :key="step.key"
        :to="step.to"
        class="trainer-course-step"
        :class="{ 'trainer-course-step--active': active === step.key }"
      >
        <component :is="step.icon" :size="16" />
        <span>{{ step.label }}</span>
      </RouterLink>
    </nav>
    <div v-else class="trainer-course-steps trainer-course-steps--loading" aria-hidden="true"></div>
  </section>
</template>

<style scoped src="./TrainerCourseNav.css"></style>
