<script setup>
import { useLazyList } from '@/composables/useLazyList'
import certificateService from '@/api/certificateService'
import { formatDate } from '@/utils/formatDate'
import { Award, BookOpen, Download } from '@lucide/vue'

const { items: certificates, loading, loadingMore, hasMore, loadMore, error } = useLazyList(
  (page, size) => certificateService.getMy(page, size),
  12,
)
</script>

<template>
  <div class="page certificate-page">
    <header class="certificate-page-header">
      <div>
        <h1>Chứng chỉ của tôi</h1>
        <p>Lưu lại kết quả những khóa học bạn đã hoàn thành.</p>
      </div>
      <div class="certificate-count">
        <strong>{{ certificates.length }}</strong>
        <span>chứng chỉ đang hiển thị</span>
      </div>
    </header>

    <p v-if="loading" class="state-text">Đang tải chứng chỉ…</p>
    <p v-else-if="error" class="alert alert-error">{{ error.response?.data || 'Không thể tải danh sách chứng chỉ.' }}</p>
    <div v-else-if="certificates.length === 0" class="certificate-empty">
      <Award :size="32" />
      <h2>Chưa có chứng chỉ</h2>
      <p>Hoàn thành toàn bộ bài học và vượt qua các bài kiểm tra bắt buộc để nhận chứng chỉ.</p>
      <RouterLink :to="{ name: 'my-courses' }" class="btn btn-primary">Tiếp tục học</RouterLink>
    </div>

    <section v-else class="certificate-list" aria-label="Danh sách chứng chỉ">
      <article v-for="certificate in certificates" :key="certificate.id" class="certificate-row">
        <div class="certificate-mark" aria-hidden="true"><Award :size="24" /></div>
        <div class="certificate-info">
          <div class="certificate-course"><BookOpen :size="14" /> Khóa học đã hoàn thành</div>
          <h2>{{ certificate.courseId.title }}</h2>
          <div class="certificate-meta">
            <span>Cấp ngày {{ formatDate(certificate.issuedAt) }}</span>
            <span class="certificate-code">{{ certificate.certificateCode }}</span>
          </div>
        </div>
        <a v-if="certificate.pdfUrl" :href="certificate.pdfUrl" target="_blank" rel="noopener" class="btn btn-primary certificate-download">
          <Download :size="15" /> Xem chứng chỉ
        </a>
        <span v-else class="certificate-pending">File PDF đang được cập nhật</span>
      </article>
    </section>

    <button v-if="hasMore" class="btn btn-secondary certificate-load-more" :disabled="loadingMore" @click="loadMore">
      {{ loadingMore ? 'Đang tải…' : 'Tải thêm chứng chỉ' }}
    </button>
  </div>
</template>

<style scoped src="./MyCertificatesView.css"></style>
