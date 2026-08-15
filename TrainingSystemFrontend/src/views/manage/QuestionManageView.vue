<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAsyncData } from '@/composables/useAsyncData'
import { useLazyList } from '@/composables/useLazyList'
import courseService from '@/api/courseService'
import testService from '@/api/testService'
import questionService from '@/api/questionService'
import questionOptionService from '@/api/questionOptionService'
import enrollmentService from '@/api/enrollmentService'
import testAttemptService from '@/api/testAttemptService'
import { confirmDialog, showError } from '@/utils/alerts'
import TrainerCourseNav from '@/components/trainer/TrainerCourseNav.vue'
import FormModal from '@/components/common/FormModal.vue'
import ImportProgressOverlay from '@/components/common/ImportProgressOverlay.vue'
import { CirclePlus, FileSpreadsheet, Pencil, Plus, Power, Trash2 } from '@lucide/vue'

const route = useRoute()
const courseId = Number(route.params.courseId)
const testId = Number(route.params.testId)

const { data: course } = useAsyncData(() => courseService.getCourseById(courseId))
const { data: tests } = useAsyncData(() => testService.getByCourse(courseId))
const currentTest = computed(() => (tests.value || []).find((t) => t.id === testId) || null)
const { data: roster } = useAsyncData(() => enrollmentService.getRoster(courseId))
const { data: attempts } = useAsyncData(() => testAttemptService.getByTest(testId))

const locked = computed(() => (roster.value || []).length > 0 || (attempts.value || []).length > 0)
const lockedReason = computed(() => {
  if ((roster.value || []).length > 0) return 'Khóa học đã có người ghi danh, không thể thay đổi câu hỏi.'
  if ((attempts.value || []).length > 0) return 'Bài kiểm tra này đã có người làm bài, không thể thay đổi câu hỏi.'
  return ''
})

const { items: questions, loading, loadingMore, hasMore, loadMore, reload: refresh } = useLazyList(
  (page, size) => questionService.getForCompose(testId, page, size),
  5,
)

const editingQuestionId = ref(null)
const questionDraft = ref('')
const questionFormOpen = ref(false)
const newQuestionContent = ref('')
const questionSaving = ref(false)
const questionError = ref('')
const optionQuestion = ref(null)
const optionText = ref('')
const optionIsCorrect = ref(false)
const optionSaving = ref(false)
const optionError = ref('')

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

function openQuestionForm() {
  newQuestionContent.value = ''
  questionError.value = ''
  questionFormOpen.value = true
}

function closeQuestionForm() {
  questionFormOpen.value = false
  questionError.value = ''
}

async function addQuestion() {
  if (!newQuestionContent.value.trim()) return
  questionSaving.value = true
  questionError.value = ''
  try {
    await questionService.create(testId, { content: newQuestionContent.value.trim() })
    closeQuestionForm()
    newQuestionContent.value = ''
    refresh()
  } catch (err) {
    questionError.value = err.response?.data || 'Không thể thêm câu hỏi. Kiểm tra nội dung và thử lại.'
  } finally {
    questionSaving.value = false
  }
}

function openOptionForm(q) {
  optionQuestion.value = q
  optionText.value = ''
  optionIsCorrect.value = false
  optionError.value = ''
}

function closeOptionForm() {
  optionQuestion.value = null
  optionText.value = ''
  optionIsCorrect.value = false
  optionError.value = ''
}

async function addOption() {
  const text = optionText.value.trim()
  if (!text) return
  optionSaving.value = true
  optionError.value = ''
  try {
    const questionId = optionQuestion.value.id
    const response = await questionOptionService.create(questionId, { optionText: text })
    if (optionIsCorrect.value) {
      await questionOptionService.setCorrect(questionId, response.data.id)
    }
    closeOptionForm()
    refresh()
  } catch (err) {
    optionError.value = err.response?.data || 'Không thể thêm đáp án. Kiểm tra nội dung và thử lại.'
  } finally {
    optionSaving.value = false
  }
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
  <div class="page trainer-page">
    <ImportProgressOverlay :active="importing" label="Đang nhập câu hỏi..." />
    <TrainerCourseNav :course="course" active="tests" />
    <div class="trainer-context-header">
      <div>
        <h2>{{ currentTest?.title || 'Đang tải bài kiểm tra…' }}</h2>
        <p>{{ questions?.length ?? 0 }} câu hỏi trong trang hiện tại.</p>
      </div>
      <div class="trainer-context-actions">
        <button class="btn btn-secondary" :disabled="importing || locked" @click="openImportPicker">
          <FileSpreadsheet :size="16" /> {{ importing ? 'Đang nhập...' : 'Nhập từ Excel' }}
        </button>
        <button class="btn btn-primary" :disabled="locked" :title="lockedReason" @click="openQuestionForm"><Plus :size="16" /> Thêm câu hỏi</button>
      </div>
      <input ref="importInput" type="file" accept=".xlsx" class="qmg-import-input" @change="onImportFileChange" />
    </div>

    <p v-if="locked" class="alert alert-error qmg-locked-alert">{{ lockedReason }} Hãy tạo bài kiểm tra hoặc khóa học mới nếu cần thay đổi nội dung.</p>

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

    <div class="qmg-main">
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="questions.length === 0" class="empty-state">Chưa có câu hỏi nào.</div>
        <div v-else class="qmg-list">
          <div v-for="(q, index) in questions" :key="q.id" class="trainer-panel qmg-card">
            <div class="qmg-card-header">
              <div class="qmg-card-identity">
                <span class="qmg-card-number">Câu {{ index + 1 }}</span>
                <span class="badge" :class="q.isActive ? 'badge-success' : 'badge-neutral'">
                  {{ q.isActive ? 'Đang hoạt động' : 'Tắt' }}
                </span>
              </div>
              <div class="row-action-group">
                <button class="row-action-btn" :disabled="locked" @click="startEditQuestion(q)"><Pencil :size="13" /> Sửa</button>
                <button class="row-action-btn" :disabled="locked" @click="toggleQuestionActive(q)"><Power :size="13" /> {{ q.isActive ? 'Tắt' : 'Bật' }}</button>
                <button class="row-action-btn row-action-btn--danger" :disabled="locked" @click="removeQuestion(q)"><Trash2 :size="13" /> Xoá</button>
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
              <div class="qmg-options-heading">
                <div>
                  <strong>Danh sách đáp án</strong>
                  <span>Chọn một đáp án đúng bằng nút tròn bên dưới.</span>
                </div>
                <button class="btn btn-secondary btn-sm" :disabled="locked" @click="openOptionForm(q)"><CirclePlus :size="14" /> Thêm đáp án</button>
              </div>
              <div v-if="!q.options?.length" class="qmg-options-empty">Chưa có đáp án. Thêm ít nhất hai đáp án trước khi mở bài kiểm tra.</div>
              <div v-for="opt in q.options" :key="opt.id" class="qmg-option-row" :class="{ 'qmg-option-row--correct': opt.isCorrect }">
                <input
                  type="radio"
                  :name="'correct-' + q.id"
                  :checked="opt.isCorrect"
                  :disabled="locked"
                  @change="setCorrect(q, opt)"
                />
                <span class="qmg-option-text">{{ opt.optionText }}</span>
                <button type="button" class="qmg-option-remove" :disabled="locked" @click="removeOption(opt)">Xoá</button>
              </div>
            </div>
          </div>
        </div>
        <button v-if="hasMore" class="btn btn-secondary qmg-load-more" :disabled="loadingMore" @click="loadMore">
          {{ loadingMore ? 'Đang tải...' : 'Tải thêm' }}
        </button>
    </div>

    <FormModal
      v-if="questionFormOpen"
      title="Thêm câu hỏi mới"
      description="Viết một ý rõ ràng cho mỗi câu hỏi. Đáp án sẽ được bổ sung ngay trong danh sách sau khi lưu."
      submit-label="Thêm câu hỏi"
      :saving="questionSaving"
      :disabled="!newQuestionContent.trim()"
      @close="closeQuestionForm"
      @submit="addQuestion"
    >
      <section class="qmg-form-section">
        <div class="qmg-form-heading">
          <h3>Nội dung câu hỏi</h3>
          <p>Tránh câu quá dài hoặc chứa nhiều ý khiến người học khó xác định yêu cầu.</p>
        </div>
        <p v-if="questionError" class="alert alert-error">{{ questionError }}</p>
        <div class="form-field">
          <label for="new-question-content">Câu hỏi</label>
          <textarea id="new-question-content" v-model="newQuestionContent" class="input" rows="5" placeholder="Ví dụ: Nhân viên cần thực hiện bước nào trước khi tư vấn sản phẩm?" required></textarea>
        </div>
      </section>
    </FormModal>

    <FormModal
      v-if="optionQuestion"
      size="small"
      title="Thêm đáp án"
      description="Đáp án mới sẽ được thêm vào câu hỏi đang chọn. Sau đó đánh dấu đáp án đúng trong danh sách."
      submit-label="Thêm đáp án"
      :saving="optionSaving"
      :disabled="!optionText.trim()"
      @close="closeOptionForm"
      @submit="addOption"
    >
      <div class="qmg-option-context">
        <span>Câu hỏi</span>
        <strong>{{ optionQuestion.content }}</strong>
      </div>
      <p v-if="optionError" class="alert alert-error">{{ optionError }}</p>
      <div class="form-field">
        <label for="new-option-text">Nội dung đáp án</label>
        <textarea id="new-option-text" v-model="optionText" class="input" rows="3" placeholder="Nhập nội dung đáp án…" required></textarea>
      </div>
      <label class="qmg-correct-choice">
        <input v-model="optionIsCorrect" type="checkbox" />
        <span><strong>Đặt làm đáp án đúng</strong><small>Nếu chọn, đáp án đúng hiện tại của câu hỏi sẽ được thay thế.</small></span>
      </label>
    </FormModal>
  </div>
</template>

<style scoped src="./QuestionManageView.css"></style>
