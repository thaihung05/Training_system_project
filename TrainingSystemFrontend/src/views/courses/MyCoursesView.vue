<script setup>
import { ref, computed, watch } from 'vue'
import { useLazyList } from '@/composables/useLazyList'
import enrollmentService from '@/api/enrollmentService'
import { formatDate } from '@/utils/formatDate'
import { thumbFor } from '@/utils/courseThumb'
import { scopeLabel } from '@/utils/courseScope'

const { items: enrollments, loading, loadingMore, hasMore, loadMore, error } = useLazyList(
  (page, size) => enrollmentService.getMyEnrollments(page, size),
  9,
)

const filter = ref('all')

watch(filter, async (val) => {
  if (val === 'all') return
  while (hasMore.value) {
    await loadMore()
  }
})

const filtered = computed(() => {
  const list = enrollments.value || []
  if (filter.value === 'in-progress') return list.filter((e) => e.progressPercent < 100)
  if (filter.value === 'done') return list.filter((e) => e.progressPercent >= 100)
  return list
})
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Khoá học của tôi</h1>
    </div>

    <div class="mycourse-filters">
      <button class="filter-chip" :class="{ 'filter-chip--active': filter === 'all' }" @click="filter = 'all'">Tất cả</button>
      <button class="filter-chip" :class="{ 'filter-chip--active': filter === 'in-progress' }" @click="filter = 'in-progress'">Đang học</button>
      <button class="filter-chip" :class="{ 'filter-chip--active': filter === 'done' }" @click="filter = 'done'">Đã hoàn thành</button>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <p v-else-if="error" class="alert alert-error">{{ error.response?.data || 'Có lỗi xảy ra.' }}</p>
    <div v-else-if="filtered.length === 0" class="empty-state">Không có khoá học nào ở mục này.</div>

    <template v-else>
      <div class="mycourse-grid">
        <div v-for="e in filtered" :key="e.id" class="mycourse-card">
          <div class="mycourse-thumb" :style="e.courseId.imageUrl ? {} : { background: thumbFor(e.courseId).bg }">
            <img v-if="e.courseId.imageUrl" :src="e.courseId.imageUrl" class="mycourse-thumb-img" />
            <span v-else class="mycourse-thumb-initials" :style="{ color: thumbFor(e.courseId).fg }">{{ thumbFor(e.courseId).initials }}</span>
            <span class="mycourse-badge" :class="e.progressPercent >= 100 ? 'mycourse-badge--done' : 'mycourse-badge--progress'">
              {{ e.progressPercent >= 100 ? 'Đã hoàn thành' : 'Đang học' }}
            </span>
          </div>
          <div class="mycourse-body">
            <div class="mycourse-dept">{{ scopeLabel(e.courseId) }}</div>
            <div class="mycourse-title">{{ e.courseId.title }}</div>
            <div v-if="e.courseId.createdBy" class="mycourse-meta">
              Tạo bởi <strong>{{ e.courseId.createdBy.name }}</strong> · {{ formatDate(e.courseId.createdAt) }}
            </div>
            <div class="progress-track">
              <div class="progress-fill" :style="{ width: e.progressPercent + '%' }"></div>
            </div>
            <div class="mycourse-percent">{{ e.progressPercent }}%</div>
            <RouterLink :to="{ name: 'course-detail', params: { id: e.courseId.id } }" class="btn btn-primary">
              {{ e.progressPercent >= 100 ? 'Xem lại' : 'Tiếp tục học' }}
            </RouterLink>
          </div>
        </div>
      </div>
      <button v-if="hasMore" class="btn btn-secondary mycourse-load-more" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
      </button>
    </template>
  </div>
</template>

<style scoped src="./MyCoursesView.css"></style>
