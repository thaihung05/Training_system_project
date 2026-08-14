<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLazyList } from '@/composables/useLazyList'
import { useAsyncData } from '@/composables/useAsyncData'
import courseService from '@/api/courseService'
import chainService from '@/api/chainService'
import regionService from '@/api/regionService'
import uploadService from '@/api/uploadService'
import { confirmDialog, showError } from '@/utils/alerts'
import { formatDate } from '@/utils/formatDate'
import { scopeLabel } from '@/utils/courseScope'
import {
  ArrowRight,
  Award,
  BookOpen,
  Building2,
  ClipboardList,
  Eye,
  EyeOff,
  ImagePlus,
  MapPinned,
  Pencil,
  Plus,
  Search,
  Users,
  X,
} from '@lucide/vue'

const router = useRouter()
const imageInput = ref(null)

const { items: courses, loading, loadingMore, hasMore, loadMore, reload } = useLazyList(
  (page, size) => courseService.getMyCourses(page, size),
  15,
)
const { data: chains } = useAsyncData(() => chainService.getAll())
const { data: regions } = useAsyncData(() => regionService.getAll())

const editingId = ref(null)
const formOpen = ref(false)
const courseSearch = ref('')
const form = ref({ title: '', description: '', chainIds: [], regionIds: [], isActive: false, imageUrl: null })
const saving = ref(false)
const errorMsg = ref('')
const uploadingImage = ref(false)

const visibleCourses = computed(() => {
  const keyword = courseSearch.value.trim().toLowerCase()
  if (!keyword) return courses.value
  return courses.value.filter((course) =>
    [course.title, course.description, scopeLabel(course)].some((value) => value?.toLowerCase().includes(keyword)),
  )
})

const selectedChainNames = computed(() =>
  (chains.value || []).filter((item) => form.value.chainIds.includes(item.id)).map((item) => item.name),
)
const selectedRegionNames = computed(() =>
  (regions.value || []).filter((item) => form.value.regionIds.includes(item.id)).map((item) => item.name),
)

function blankForm() {
  return { title: '', description: '', chainIds: [], regionIds: [], isActive: false, imageUrl: null }
}

function resetForm() {
  editingId.value = null
  form.value = blankForm()
  errorMsg.value = ''
}

function openCreate() {
  resetForm()
  formOpen.value = true
}

function closeForm() {
  formOpen.value = false
  resetForm()
}

function editCourse(course) {
  editingId.value = course.id
  form.value = {
    title: course.title,
    description: course.description,
    chainIds: (course.chains || []).map((chain) => chain.id),
    regionIds: (course.regions || []).map((region) => region.id),
    isActive: course.isActive,
    imageUrl: course.imageUrl || null,
  }
  errorMsg.value = ''
  formOpen.value = true
}

function toggleScope(field, id) {
  const selected = form.value[field]
  form.value[field] = selected.includes(id) ? selected.filter((itemId) => itemId !== id) : [...selected, id]
}

function openImagePicker() {
  imageInput.value?.click()
}

async function onImageFileChange(event) {
  const file = event.target.files[0]
  event.target.value = ''
  if (!file) return
  uploadingImage.value = true
  try {
    const response = await uploadService.uploadImage(file)
    form.value.imageUrl = response.data.url
  } catch (error) {
    showError(error.response?.data || 'Tải ảnh lên thất bại.')
  } finally {
    uploadingImage.value = false
  }
}

function buildPayload() {
  return {
    title: form.value.title,
    description: form.value.description,
    chains: form.value.chainIds.map((id) => ({ id })),
    regions: form.value.regionIds.map((id) => ({ id })),
    isActive: form.value.isActive,
    imageUrl: form.value.imageUrl,
  }
}

async function submit() {
  errorMsg.value = ''
  saving.value = true
  try {
    if (editingId.value) await courseService.update(editingId.value, buildPayload())
    else await courseService.create(buildPayload())
    closeForm()
    reload()
  } catch (error) {
    errorMsg.value = error.response?.data || 'Không thể lưu khóa học. Kiểm tra lại thông tin và thử lại.'
  } finally {
    saving.value = false
  }
}

async function deactivate(course) {
  if (!(await confirmDialog(`Ẩn khoá học "${course.title}"?`))) return
  await courseService.remove(course.id)
  reload()
}

async function reactivate(course) {
  try {
    await courseService.update(course.id, {
      title: course.title,
      description: course.description,
      chains: (course.chains || []).map((chain) => ({ id: chain.id })),
      regions: (course.regions || []).map((region) => ({ id: region.id })),
      isActive: true,
      imageUrl: course.imageUrl,
    })
    reload()
  } catch (error) {
    showError(error.response?.data || 'Chưa thể mở khóa học. Hãy kiểm tra lại bài học và bài kiểm tra.')
  }
}

function go(routeName, course) {
  router.push({ name: routeName, params: { courseId: course.id } })
}
</script>

<template>
  <div class="page trainer-page course-studio" @keydown.esc="closeForm">
    <header class="trainer-page-header">
      <div>
        <h1>Khóa học phụ trách</h1>
        <p>Tạo nội dung, chọn đúng phạm vi Chuỗi – Vùng và theo dõi toàn bộ vòng đời khóa học tại một nơi.</p>
      </div>
      <button class="btn btn-primary course-studio-create" @click="openCreate">
        <Plus :size="18" /> Tạo khóa học
      </button>
    </header>

    <div class="course-studio-toolbar trainer-panel">
      <label class="course-studio-search">
        <Search :size="17" />
        <input v-model="courseSearch" type="search" placeholder="Tìm theo tên, mô tả hoặc phạm vi…" />
      </label>
      <div class="course-studio-count">
        <strong>{{ visibleCourses.length }}</strong>
        <span>khóa đang hiển thị</span>
      </div>
    </div>

    <p v-if="loading" class="state-text">Đang tải danh sách khóa học…</p>
    <div v-else-if="courses.length === 0" class="course-studio-empty trainer-panel">
      <BookOpen :size="34" />
      <h2>Bắt đầu khóa học đầu tiên</h2>
      <p>Thiết lập thông tin, chọn Chuỗi – Vùng rồi bổ sung bài học và bài kiểm tra.</p>
      <button class="btn btn-primary" @click="openCreate"><Plus :size="16" /> Tạo khóa học</button>
    </div>
    <div v-else-if="visibleCourses.length === 0" class="empty-state">Không có khóa học phù hợp từ khóa.</div>

    <section v-else class="course-studio-grid" aria-label="Danh sách khóa học phụ trách">
      <article v-for="course in visibleCourses" :key="course.id" class="course-studio-card trainer-panel">
        <div class="course-studio-cover" :class="{ 'course-studio-cover--blank': !course.imageUrl }">
          <img v-if="course.imageUrl" :src="course.imageUrl" :alt="course.title" />
          <BookOpen v-else :size="30" />
          <span class="course-studio-status" :class="{ 'course-studio-status--hidden': !course.isActive }">
            {{ course.isActive ? 'Đang mở' : 'Chưa mở' }}
          </span>
        </div>

        <div class="course-studio-card-body">
          <div class="course-studio-card-meta">Cập nhật {{ formatDate(course.createdAt) }}</div>
          <h2>{{ course.title }}</h2>
          <p class="course-studio-description">{{ course.description || 'Chưa có mô tả cho khóa học này.' }}</p>
          <div class="course-studio-scope">{{ scopeLabel(course) }}</div>
        </div>

        <div class="course-studio-actions">
          <button @click="go('manage-lessons', course)"><BookOpen :size="15" /><span>Bài học</span></button>
          <button @click="go('manage-tests', course)"><ClipboardList :size="15" /><span>Kiểm tra</span></button>
          <button @click="go('course-enrollments', course)"><Users :size="15" /><span>Ghi danh</span></button>
          <button @click="go('course-certificates', course)"><Award :size="15" /><span>Chứng chỉ</span></button>
        </div>

        <div class="course-studio-card-footer">
          <button class="course-studio-text-action" @click="editCourse(course)"><Pencil :size="14" /> Chỉnh sửa</button>
          <button v-if="course.isActive" class="course-studio-text-action course-studio-text-action--danger" @click="deactivate(course)">
            <EyeOff :size="14" /> Ẩn khóa học
          </button>
          <button v-else class="course-studio-text-action" @click="reactivate(course)"><Eye :size="14" /> Mở khóa học</button>
        </div>
      </article>
    </section>

    <button v-if="hasMore" class="btn btn-secondary course-studio-load-more" :disabled="loadingMore" @click="loadMore">
      {{ loadingMore ? 'Đang tải…' : 'Tải thêm khóa học' }}
    </button>

    <Transition name="course-editor">
      <div v-if="formOpen" class="course-editor-backdrop" @click.self="closeForm">
        <form class="course-editor" @submit.prevent="submit">
          <header class="course-editor-header">
            <div>
              <h2>{{ editingId ? 'Chỉnh sửa khóa học' : 'Tạo khóa học mới' }}</h2>
              <p>Thông tin rõ ràng và phạm vi chính xác giúp ghi danh đúng nhân viên.</p>
            </div>
            <button type="button" class="course-editor-close" aria-label="Đóng" @click="closeForm"><X :size="20" /></button>
          </header>

          <div class="course-editor-body">
            <div class="course-editor-main">
              <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>

              <section class="course-editor-section">
                <div class="course-editor-section-title"><div><h3>Thông tin cơ bản</h3><p>Tên ngắn gọn, dễ nhận biết trong danh mục.</p></div></div>
                <div class="form-field">
                  <label for="course-title">Tên khóa học</label>
                  <input id="course-title" v-model="form.title" type="text" class="input" placeholder="Ví dụ: Kỹ năng tư vấn sản phẩm mới" required />
                </div>
                <div class="form-field">
                  <label for="course-description">Mô tả ngắn</label>
                  <textarea id="course-description" v-model="form.description" class="input" rows="4" placeholder="Nhân viên sẽ học được gì sau khóa học?"></textarea>
                </div>
              </section>

              <section class="course-editor-section">
                <div class="course-editor-section-title"><div><h3>Phạm vi áp dụng</h3><p>Chuỗi và Vùng kết hợp theo điều kiện AND. Để trống cả hai nghĩa là áp dụng cho toàn tập đoàn.</p></div></div>

                <div class="course-scope-builder">
                  <div class="course-scope-column">
                    <div class="course-scope-heading">
                      <div><Building2 :size="17" /><strong>Chuỗi bán lẻ</strong></div>
                      <button type="button" :class="{ active: form.chainIds.length === 0 }" @click="form.chainIds = []">Toàn bộ chuỗi</button>
                    </div>
                    <div class="course-scope-options">
                      <button
                        v-for="chain in chains"
                        :key="chain.id"
                        type="button"
                        :class="{ selected: form.chainIds.includes(chain.id) }"
                        @click="toggleScope('chainIds', chain.id)"
                      >
                        <span class="course-scope-check"></span>{{ chain.name }}
                      </button>
                    </div>
                  </div>

                  <div class="course-scope-column">
                    <div class="course-scope-heading">
                      <div><MapPinned :size="17" /><strong>Vùng quản lý</strong></div>
                      <button type="button" :class="{ active: form.regionIds.length === 0 }" @click="form.regionIds = []">Toàn bộ vùng</button>
                    </div>
                    <div class="course-scope-options">
                      <button
                        v-for="region in regions"
                        :key="region.id"
                        type="button"
                        :class="{ selected: form.regionIds.includes(region.id) }"
                        @click="toggleScope('regionIds', region.id)"
                      >
                        <span class="course-scope-check"></span>{{ region.name }}
                      </button>
                    </div>
                  </div>
                </div>

                <div class="course-scope-summary">
                  <strong>Nhân viên được áp dụng</strong>
                  <span>{{ selectedChainNames.length ? selectedChainNames.join(', ') : 'Tất cả Chuỗi' }}</span>
                  <ArrowRight :size="14" />
                  <span>{{ selectedRegionNames.length ? selectedRegionNames.join(', ') : 'Tất cả Vùng' }}</span>
                </div>
              </section>
            </div>

            <aside class="course-editor-aside">
              <section class="course-editor-preview">
                <div class="course-editor-preview-image" :class="{ empty: !form.imageUrl }">
                  <img v-if="form.imageUrl" :src="form.imageUrl" alt="Ảnh đại diện khóa học" />
                  <ImagePlus v-else :size="30" />
                </div>
                <button type="button" class="btn btn-secondary" :disabled="uploadingImage" @click="openImagePicker">
                  <ImagePlus :size="15" /> {{ uploadingImage ? 'Đang tải ảnh…' : form.imageUrl ? 'Đổi ảnh đại diện' : 'Chọn ảnh đại diện' }}
                </button>
                <button v-if="form.imageUrl && !uploadingImage" type="button" class="course-editor-remove-image" @click="form.imageUrl = null">Bỏ ảnh hiện tại</button>
                <input ref="imageInput" type="file" accept="image/jpeg,image/png,image/webp" class="hidden-file-input" @change="onImageFileChange" />
              </section>

              <label v-if="editingId" class="course-editor-switch">
                <input v-model="form.isActive" type="checkbox" />
                <span class="course-editor-switch-track"></span>
                <span><strong>Mở khóa học</strong><small>Cho phép hiển thị và ghi danh</small></span>
              </label>

              <div v-else class="course-editor-lifecycle">
                <strong>Khóa học sẽ được tạo ở trạng thái chưa mở</strong>
                <p>Thêm ít nhất một bài học hoặc một bài kiểm tra hợp lệ, sau đó mở khóa học từ danh sách.</p>
              </div>

            </aside>
          </div>

          <footer class="course-editor-footer">
            <button type="button" class="btn btn-secondary" @click="closeForm">Hủy</button>
            <button type="submit" class="btn btn-primary" :disabled="saving || uploadingImage">
              {{ saving ? 'Đang lưu…' : editingId ? 'Lưu thay đổi' : 'Tạo khóa học' }}
            </button>
          </footer>
        </form>
      </div>
    </Transition>
  </div>
</template>

<style scoped src="./CourseManageView.css"></style>
