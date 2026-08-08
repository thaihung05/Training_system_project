<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import testService from '@/api/testService'
import questionService from '@/api/questionService'
import questionOptionService from '@/api/questionOptionService'
import { confirmDialog, showError } from '@/utils/alerts'
import { ChevronLeft } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const courseId = Number(route.params.courseId)
const testId = Number(route.params.testId)

const { data: tests } = useAsyncData(() => testService.getByCourse(courseId))
const currentTest = computed(() => (tests.value || []).find((t) => t.id === testId) || null)

const { data: questions, loading, refresh } = useAsyncData(() => questionService.getForCompose(testId))

const editingQuestionId = ref(null)
const questionDraft = ref('')
const newQuestionContent = ref('')
const newOptionText = ref({})

function startEditQuestion(q) {
  editingQuestionId.value = q.id
  questionDraft.value = q.content
}

async function saveQuestion(q) {
  await questionService.update(q.id, { content: questionDraft.value, isActive: q.isActive })
  editingQuestionId.value = null
  refresh()
}

async function toggleQuestionActive(q) {
  try {
    await questionService.update(q.id, { content: q.content, isActive: !q.isActive })
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Có lỗi xảy ra.')
  }
}

async function removeQuestion(q) {
  if (!(await confirmDialog('Xoá câu hỏi này?'))) return
  try {
    await questionService.remove(q.id)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá câu hỏi này.')
  }
}

async function addQuestion() {
  if (!newQuestionContent.value.trim()) return
  await questionService.create(testId, { content: newQuestionContent.value })
  newQuestionContent.value = ''
  refresh()
}

async function addOption(q) {
  const text = (newOptionText.value[q.id] || '').trim()
  if (!text) return
  await questionOptionService.create(q.id, { optionText: text })
  newOptionText.value[q.id] = ''
  refresh()
}

async function setCorrect(q, opt) {
  try {
    await questionOptionService.setCorrect(q.id, opt.id)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Có lỗi xảy ra.')
  }
}

async function removeOption(opt) {
  if (!(await confirmDialog('Xoá đáp án này?'))) return
  try {
    await questionOptionService.remove(opt.id)
    refresh()
  } catch (err) {
    showError(err.response?.data || 'Không thể xoá đáp án này.')
  }
}

const importInput = ref(null)
const importing = ref(false)
const importResults = ref(null)

function openImportPicker() {
  importInput.value.click()
}

async function onImportFileChange(e) {
  const file = e.target.files[0]
  e.target.value = ''
  if (!file) return
  importing.value = true
  importResults.value = null
  try {
    const res = await questionService.bulkImport(testId, file)
    importResults.value = res.data
    refresh()
  } catch (err) {
    await showError(err.response?.data || 'Nhập file thất bại.')
  } finally {
    importing.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="detail-header">
      <button class="back-btn" @click="router.push({ name: 'manage-tests', params: { courseId } })"><ChevronLeft :size="18" /></button>
      <div class="detail-heading">
        <h1>Câu hỏi — {{ currentTest?.title }}</h1>
        <div class="detail-meta">{{ questions?.length ?? 0 }} câu hỏi</div>
      </div>
      <button class="btn btn-secondary" :disabled="importing" @click="openImportPicker">
        {{ importing ? 'Đang nhập...' : 'Nhập từ Excel' }}
      </button>
      <input ref="importInput" type="file" accept=".xlsx" class="qmg-import-input" @change="onImportFileChange" />
    </div>

    <div v-if="importResults" class="card qmg-import-results">
      <h2>Kết quả nhập file</h2>
      <div v-for="r in importResults" :key="r.row" class="qmg-import-row">
        <span class="qmg-import-row-num">Dòng {{ r.row }}</span>
        <span class="badge" :class="r.status === 'success' ? 'badge-success' : 'badge-danger'">
          {{ r.status === 'success' ? 'Thành công' : r.message }}
        </span>
      </div>
      <button class="btn btn-secondary btn-sm" @click="importResults = null">Đóng</button>
    </div>

    <div class="manage-layout">
      <div class="qmg-main">
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="questions.length === 0" class="empty-state">Chưa có câu hỏi nào.</div>
        <div v-else class="qmg-list">
          <div v-for="q in questions" :key="q.id" class="card qmg-card">
            <div class="qmg-card-header">
              <span class="badge" :class="q.isActive ? 'badge-success' : 'badge-neutral'">
                {{ q.isActive ? 'Đang hoạt động' : 'Tắt' }}
              </span>
              <div class="qmg-card-actions">
                <span class="manage-action" @click="startEditQuestion(q)">Sửa</span>
                <span class="manage-action" @click="toggleQuestionActive(q)">{{ q.isActive ? 'Tắt' : 'Bật' }}</span>
                <span class="manage-action manage-action--danger" @click="removeQuestion(q)">Xoá</span>
              </div>
            </div>

            <div v-if="editingQuestionId === q.id" class="qmg-edit">
              <textarea v-model="questionDraft" class="input" rows="2"></textarea>
              <div class="qmg-edit-actions">
                <button class="btn btn-primary btn-sm" @click="saveQuestion(q)">Lưu</button>
                <button class="btn btn-secondary btn-sm" @click="editingQuestionId = null">Huỷ</button>
              </div>
            </div>
            <div v-else class="qmg-content">{{ q.content }}</div>

            <div class="qmg-options">
              <label v-for="opt in q.options" :key="opt.id" class="qmg-option-row">
                <input
                  type="radio"
                  :name="'correct-' + q.id"
                  :checked="opt.isCorrect"
                  @change="setCorrect(q, opt)"
                />
                <span class="qmg-option-text">{{ opt.optionText }}</span>
                <span class="manage-action manage-action--danger" @click="removeOption(opt)">Xoá</span>
              </label>

              <div class="qmg-add-option">
                <input
                  v-model="newOptionText[q.id]"
                  type="text"
                  class="input"
                  placeholder="Thêm đáp án..."
                  @keyup.enter="addOption(q)"
                />
                <button class="btn btn-secondary btn-sm" @click="addOption(q)">Thêm</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="manage-form card">
        <h2>Thêm câu hỏi mới</h2>
        <div class="form-field">
          <label>Nội dung câu hỏi</label>
          <textarea v-model="newQuestionContent" class="input" rows="3" placeholder="Nhập nội dung câu hỏi..."></textarea>
        </div>
        <button class="btn btn-primary" @click="addQuestion">Thêm câu hỏi</button>
      </div>
    </div>
  </div>
</template>

<style scoped src="./QuestionManageView.css"></style>
