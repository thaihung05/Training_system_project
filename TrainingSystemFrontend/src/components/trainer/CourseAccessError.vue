<script setup>
import { RouterLink } from 'vue-router'
import { ShieldAlert } from '@lucide/vue'

defineProps({
  message: { type: String, default: 'Bạn không có quyền truy cập khoá học này.' },
  toName: { type: String, default: 'manage-courses' },
  toLabel: { type: String, default: 'Quay lại danh sách khoá học' },
})
</script>

<template>
  <div class="page">
    <div class="trainer-panel course-access-error">
      <ShieldAlert :size="30" />
      <h2>Không thể mở khoá học</h2>
      <p>{{ message }}</p>
      <RouterLink :to="{ name: toName }" class="btn btn-primary">{{ toLabel }}</RouterLink>
    </div>
  </div>
</template>

<style scoped src="./CourseAccessError.css"></style>
