<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLazyList } from '@/composables/useLazyList'
import { useAsyncData } from '@/composables/useAsyncData'
import courseService from '@/api/courseService'
import departmentService from '@/api/departmentService'
import uploadService from '@/api/uploadService'
import { confirmDialog, showError } from '@/utils/alerts'
import { formatDate } from '@/utils/formatDate'
import { Pencil, BookOpen, ClipboardList, Users, Award, EyeOff, Eye } from '@lucide/vue'

const router = useRouter()
const imageInput = ref(null)

function openImagePicker() {
  imageInput.value.click()
}

const { items: courses, loading, loadingMore, hasMore, loadMore, reload } = useLazyList(
  (page, size) => courseService.getMyCourses(page, size),
  15,
)
const { data: departments } = useAsyncData(() => departmentService.getAll())

const editingId = ref(null)
const form = ref({ title: '', description: '', departmentId: null, isActive: true, imageUrl: null })
const saving = ref(false)
const errorMsg = ref('')
const uploadingImage = ref(false)

function resetForm() {
  editingId.value = null
  form.value = { title: '', description: '', departmentId: null, isActive: true, imageUrl: null }
  errorMsg.value = ''
}

function editCourse(c) {
  editingId.value = c.id
  form.value = {
    title: c.title,
    description: c.description,
    departmentId: c.departmentId ? c.departmentId.id : null,
    isActive: c.isActive,
    imageUrl: c.imageUrl || null,
  }
  errorMsg.value = ''
}

async function onImageFileChange(e) {
  const file = e.target.files[0]
  e.target.value = ''
  if (!file) return
  uploadingImage.value = true
  try {
    const res = await uploadService.uploadImage(file)
    form.value.imageUrl = res.data.url
  } catch (err) {
    showError(err.response?.data || 'Tải ảnh lên thất bại.')
  } finally {
    uploadingImage.value = false
  }
}

function buildPayload() {
  return {
    title: form.value.title,
    description: form.value.description,
    departmentId: form.value.departmentId ? { id: form.value.departmentId } : null,
    isActive: form.value.isActive,
    imageUrl: form.value.imageUrl,
  }
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  try {
    if (editingId.value) {
      await courseService.update(editingId.value, buildPayload())
    } else {
      await courseService.create(buildPayload())
    }
    resetForm()
    reload()
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}

async function deactivate(c) {
  if (!(await confirmDialog(`Ẩn khoá học "${c.title}"?`))) return
  await courseService.remove(c.id)
  reload()
}

async function reactivate(c) {
  await courseService.update(c.id, {
    title: c.title,
    description: c.description,
    departmentId: c.departmentId ? { id: c.departmentId.id } : null,
    isActive: true,
    imageUrl: c.imageUrl,
  })
  reload()
}

function goEnroll(c) {
  router.push({ name: 'course-enrollments', params: { courseId: c.id } })
}

function goLessons(c) {
  router.push({ name: 'manage-lessons', params: { courseId: c.id } })
}

function goTests(c) {
  router.push({ name: 'manage-tests', params: { courseId: c.id } })
}

function goCertificates(c) {
  router.push({ name: 'course-certificates', params: { courseId: c.id } })
}

</script>

<template>
  <div class="page">
    <div class="manage-header">
      <h1>Quản lý khoá học</h1>
    </div>

    <div class="manage-layout">
      <div class="manage-table-wrap">
        <div class="manage-table-header">
          <div>TÊN KHOÁ HỌC</div>
          <div>PHÒNG BAN</div>
          <div>NGƯỜI TẠO</div>
          <div>TRẠNG THÁI</div>
          <div>HÀNH ĐỘNG</div>
        </div>
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="courses.length === 0" class="empty-state">Bạn chưa tạo khoá học nào.</div>
        <template v-else>
          <div v-for="c in courses" :key="c.id" class="manage-row">
            <div class="manage-row-title">{{ c.title }}</div>
            <div class="manage-row-dept">{{ c.departmentId ? c.departmentId.name : 'Toàn công ty' }}</div>
            <div class="manage-row-creator">
              <div v-if="c.createdBy">{{ c.createdBy.name }}</div>
              <div class="manage-row-creator-date">{{ formatDate(c.createdAt) }}</div>
            </div>
            <div>
              <span class="badge" :class="c.isActive ? 'badge-success' : 'badge-neutral'">
                {{ c.isActive ? 'Đang mở' : 'Đã ẩn' }}
              </span>
            </div>
            <div class="row-action-group">
              <button class="row-action-btn" @click="editCourse(c)"><Pencil :size="13" /> Sửa</button>
              <button class="row-action-btn" @click="goLessons(c)"><BookOpen :size="13" /> Bài học</button>
              <button class="row-action-btn" @click="goTests(c)"><ClipboardList :size="13" /> Kiểm tra</button>
              <button class="row-action-btn" @click="goEnroll(c)"><Users :size="13" /> Ghi danh</button>
              <button class="row-action-btn" @click="goCertificates(c)"><Award :size="13" /> Chứng chỉ</button>
              <button v-if="c.isActive" class="row-action-btn row-action-btn--danger" @click="deactivate(c)"><EyeOff :size="13" /> Ẩn</button>
              <button v-else class="row-action-btn" @click="reactivate(c)"><Eye :size="13" /> Mở lại</button>
            </div>
          </div>
        </template>
        <button v-if="hasMore" class="btn btn-secondary courses-load-more" :disabled="loadingMore" @click="loadMore">
          {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
        </button>
      </div>

      <div class="manage-form card">
        <h2>{{ editingId ? 'Sửa khoá học' : 'Thêm khoá học mới' }}</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <div class="form-field">
          <label>Tên khoá học</label>
          <input v-model="form.title" type="text" class="input" placeholder="VD: Kỹ năng bán hàng nâng cao" />
        </div>
        <div class="form-field">
          <label>Phòng ban</label>
          <select v-model="form.departmentId" class="input">
            <option :value="null">Toàn công ty</option>
            <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
          </select>
        </div>
        <div class="form-field">
          <label>Mô tả ngắn</label>
          <textarea v-model="form.description" class="input" rows="3" placeholder="Mô tả nội dung khoá học..."></textarea>
        </div>
        <div class="form-field">
          <label>Ảnh đại diện khoá học</label>
          <img v-if="form.imageUrl" :src="form.imageUrl" class="manage-course-image-preview" />
          <div class="manage-course-image-actions">
            <button type="button" class="btn btn-secondary btn-sm" :disabled="uploadingImage" @click="openImagePicker">
              {{ uploadingImage ? 'Đang tải ảnh lên...' : form.imageUrl ? 'Đổi ảnh khác' : 'Chọn ảnh' }}
            </button>
            <span v-if="form.imageUrl && !uploadingImage" class="manage-action manage-action--danger" @click="form.imageUrl = null">Bỏ ảnh</span>
          </div>
          <input ref="imageInput" type="file" accept="image/jpeg,image/png,image/webp" class="hidden-file-input" @change="onImageFileChange" />
        </div>
        <label class="manage-checkbox">
          <input type="checkbox" v-model="form.isActive" />
          Đang mở (nhân viên có thể xem và được ghi danh)
        </label>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">
            {{ editingId ? 'Lưu thay đổi' : 'Lưu khoá học' }}
          </button>
          <button v-if="editingId" class="btn btn-secondary" @click="resetForm">Huỷ</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./CourseManageView.css"></style>
