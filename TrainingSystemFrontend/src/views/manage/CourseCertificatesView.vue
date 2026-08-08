<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import { useAuthStore } from '@/stores/auth'
import courseService from '@/api/courseService'
import certificateService from '@/api/certificateService'
import uploadService from '@/api/uploadService'
import { showError } from '@/utils/alerts'
import { ChevronLeft } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const courseId = Number(route.params.courseId)

const { data: course } = useAsyncData(() => courseService.getCourseById(courseId))
const { data: certificates, loading, error, refresh } = useAsyncData(() =>
  certificateService.getByCourse(courseId),
)

const uploadingId = ref(null)

async function handleFileChange(event, cert) {
  const file = event.target.files[0]
  if (!file) return
  uploadingId.value = cert.id
  try {
    const res = await uploadService.uploadPdf(file)
    await certificateService.updatePdfUrl(cert.id, res.data.url)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Tải file thất bại.')
  } finally {
    uploadingId.value = null
  }
}

function formatDate(ms) {
  if (!ms) return ''
  return new Date(ms).toLocaleDateString('vi-VN')
}
</script>

<template>
  <div class="page">
    <div class="detail-header">
      <button class="back-btn" @click="router.push({ name: 'manage-courses' })"><ChevronLeft :size="18" /></button>
      <div class="detail-heading">
        <h1>Chứng chỉ — {{ course?.title }}</h1>
        <div class="detail-meta">{{ certificates?.length ?? 0 }} chứng chỉ đã cấp</div>
      </div>
    </div>

    <div class="manage-table-wrap">
      <div class="certmg-table-header">
        <div>NHÂN VIÊN</div>
        <div>MÃ CHỨNG CHỈ</div>
        <div>NGÀY CẤP</div>
        <div>FILE PDF</div>
      </div>
      <p v-if="loading" class="state-text">Đang tải...</p>
      <p v-else-if="error" class="alert alert-error">{{ error.response?.data || 'Có lỗi xảy ra.' }}</p>
      <div v-else-if="!certificates || certificates.length === 0" class="empty-state">Chưa có ai được cấp chứng chỉ.</div>
      <template v-else>
        <div v-for="c in certificates" :key="c.id" class="certmg-row">
          <div class="certmg-name">{{ c.userId.name }}</div>
          <div class="certmg-code">{{ c.certificateCode }}</div>
          <div class="certmg-date">{{ formatDate(c.issuedAt) }}</div>
          <div class="certmg-file">
            <a v-if="c.pdfUrl" :href="c.pdfUrl" target="_blank" class="manage-action">Tải về</a>
            <span v-else class="certmg-empty">Chưa có file</span>
            <div v-if="auth.isAdmin" class="certmg-upload">
              <input
                type="file"
                accept="application/pdf"
                :disabled="uploadingId === c.id"
                @change="handleFileChange($event, c)"
              />
              <span v-if="uploadingId === c.id" class="certmg-uploading">Đang tải...</span>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped src="./CourseCertificatesView.css"></style>
