<script setup>
import { ref, watch } from 'vue'
import { useAsyncData } from '@/composables/useAsyncData'
import { useAuthStore } from '@/stores/auth'
import courseService from '@/api/courseService'
import departmentService from '@/api/departmentService'

const auth = useAuthStore()
const keyword = ref('')
const selectedDeptId = ref(null)

const { data: courses, loading, error, refresh } = useAsyncData(() =>
  courseService.getCourses(keyword.value, selectedDeptId.value),
)
const { data: departments } = useAsyncData(() => departmentService.getAll())

function selectDept(id) {
  selectedDeptId.value = id
}

let debounceTimer = null
watch(keyword, () => {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(refresh, 400)
})

watch(selectedDeptId, refresh)
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
      <div v-if="!auth.isEmployee" class="filter-chips">
        <button
          class="filter-chip"
          :class="{ 'filter-chip--active': selectedDeptId === null }"
          @click="selectDept(null)"
        >
          Tất cả
        </button>
        <button
          v-for="d in departments"
          :key="d.id"
          class="filter-chip"
          :class="{ 'filter-chip--active': selectedDeptId === d.id }"
          @click="selectDept(d.id)"
        >
          {{ d.name }}
        </button>
      </div>
    </div>

    <p v-if="loading" class="state-text">Đang tải...</p>
    <p v-else-if="error" class="alert alert-error">{{ error.response?.data || 'Có lỗi xảy ra.' }}</p>
    <div v-else-if="courses.length === 0" class="empty-state">Không tìm thấy khoá học nào.</div>

    <div v-else class="course-grid">
      <div v-for="c in courses" :key="c.id" class="course-card">
        <div class="course-card-thumb">
          <span class="course-card-badge">{{ c.departmentId ? c.departmentId.name : 'Toàn công ty' }}</span>
        </div>
        <div class="course-card-body">
          <div class="course-card-title">{{ c.title }}</div>
          <div class="course-card-desc">{{ c.description }}</div>
          <RouterLink :to="{ name: 'course-detail', params: { id: c.id } }" class="btn btn-primary">
            Xem khoá học
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./CourseListView.css"></style>
