<script setup>
import { ref, watch } from 'vue'
import { useLazyList } from '@/composables/useLazyList'
import { useAsyncData } from '@/composables/useAsyncData'
import { useAuthStore } from '@/stores/auth'
import courseService from '@/api/courseService'
import chainService from '@/api/chainService'
import regionService from '@/api/regionService'
import { formatDate } from '@/utils/formatDate'
import { thumbFor } from '@/utils/courseThumb'
import { scopeLabel } from '@/utils/courseScope'

const auth = useAuthStore()
const keyword = ref('')
const selectedChainId = ref(null)
const selectedRegionId = ref(null)

const { items: courses, loading, loadingMore, hasMore, loadMore, error, reload } = useLazyList(
  (page, size) => courseService.getCourses(keyword.value, selectedChainId.value, selectedRegionId.value, page, size),
  9,
)
const { data: chains } = useAsyncData(() => chainService.getAll())
const { data: regions } = useAsyncData(() => regionService.getAll())

let debounceTimer = null
watch(keyword, () => {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(reload, 400)
})

watch([selectedChainId, selectedRegionId], reload)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Khoá học</h1>
    </div>

    <div class="course-toolbar">
      <div class="search-bar">
        <input v-model="keyword" type="text" placeholder="Tìm kiếm khoá học..." />
      </div>
      <div v-if="!auth.isEmployee" class="course-filter-selects">
        <select v-model="selectedChainId" class="input">
          <option :value="null">Tất cả chuỗi</option>
          <option v-for="c in chains" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <select v-model="selectedRegionId" class="input">
          <option :value="null">Tất cả vùng</option>
          <option v-for="r in regions" :key="r.id" :value="r.id">{{ r.name }}</option>
        </select>
      </div>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <p v-else-if="error" class="alert alert-error">{{ error.response?.data || 'Có lỗi xảy ra.' }}</p>
    <div v-else-if="courses.length === 0" class="empty-state">Không tìm thấy khoá học nào.</div>

    <template v-else>
      <div class="course-grid">
        <div v-for="c in courses" :key="c.id" class="course-card">
          <div class="course-card-thumb" :style="c.imageUrl ? {} : { background: thumbFor(c).bg }">
            <img v-if="c.imageUrl" :src="c.imageUrl" class="course-card-thumb-img" />
            <span v-else class="course-card-thumb-initials" :style="{ color: thumbFor(c).fg }">{{ thumbFor(c).initials }}</span>
            <span class="course-card-badge">{{ scopeLabel(c) }}</span>
          </div>
          <div class="course-card-body">
            <div class="course-card-title">{{ c.title }}</div>
            <div class="course-card-desc">{{ c.description }}</div>
            <div v-if="c.createdBy" class="course-card-meta">
              Tạo bởi <strong>{{ c.createdBy.name }}</strong> · {{ formatDate(c.createdAt) }}
            </div>
            <RouterLink :to="{ name: 'course-detail', params: { id: c.id } }" class="btn btn-primary">
              Xem khoá học
            </RouterLink>
          </div>
        </div>
      </div>
      <button v-if="hasMore" class="btn btn-secondary course-load-more" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
      </button>
    </template>
  </div>
</template>

<style scoped src="./CourseListView.css"></style>
