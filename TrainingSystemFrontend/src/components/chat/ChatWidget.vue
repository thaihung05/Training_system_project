<script setup>
import { ref, nextTick } from 'vue'
import { MessageCircle, X, Send } from '@lucide/vue'
import chatService from '@/api/chatService'
import { renderChatAnswer } from '@/utils/chatMarkdown'

function getSessionId() {
  let id = localStorage.getItem('chat_session_id')
  if (!id) {
    id = crypto.randomUUID()
    localStorage.setItem('chat_session_id', id)
  }
  return id
}

const sessionId = getSessionId()
const isOpen = ref(false)
const messages = ref([])
const loading = ref(false)
const sending = ref(false)
const sendError = ref('')
const questionText = ref('')
const messageListEl = ref(null)

async function scrollToBottom() {
  await nextTick()
  if (messageListEl.value) {
    messageListEl.value.scrollTop = messageListEl.value.scrollHeight
  }
}

async function loadHistory() {
  loading.value = true
  try {
    const res = await chatService.getBySession(sessionId)
    messages.value = res.data
    await scrollToBottom()
  } finally {
    loading.value = false
  }
}

function toggleOpen() {
  isOpen.value = !isOpen.value
  if (isOpen.value) loadHistory()
}

async function sendQuestion() {
  const text = questionText.value.trim()
  if (!text || sending.value) return
  sending.value = true
  sendError.value = ''
  questionText.value = ''

  const pendingMsg = { id: `pending-${Date.now()}`, question: text, answer: null, pending: true }
  messages.value.push(pendingMsg)
  await scrollToBottom()

  try {
    const res = await chatService.ask(text, sessionId)
    const idx = messages.value.indexOf(pendingMsg)
    if (idx !== -1) messages.value.splice(idx, 1, res.data)
    await scrollToBottom()
  } catch (err) {
    const idx = messages.value.indexOf(pendingMsg)
    if (idx !== -1) messages.value.splice(idx, 1)
    questionText.value = text
    sendError.value = err.response?.data || 'Gửi câu hỏi thất bại, thử lại nhé.'
  } finally {
    sending.value = false
  }
}
</script>

<template>
  <div class="chat-widget">
    <div v-if="isOpen" class="chat-panel">
      <div class="chat-panel-header">
        <span>Trợ lý ảo</span>
        <button class="chat-close-btn" @click="toggleOpen"><X :size="15" /></button>
      </div>

      <div ref="messageListEl" class="chat-messages">
        <p v-if="loading" class="state-text">Đang tải...</p>
        <div v-else-if="messages.length === 0" class="chat-empty">
          Hỏi tôi bất cứ điều gì về hệ thống đào tạo nhé!
        </div>
        <template v-else>
          <div v-for="m in messages" :key="m.id" class="chat-message-group">
            <div class="chat-bubble chat-bubble--user">{{ m.question }}</div>
            <div v-if="m.pending" class="chat-bubble chat-bubble--typing">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </div>
            <div v-else-if="m.answer" class="chat-bubble chat-bubble--bot" v-html="renderChatAnswer(m.answer)"></div>
            <div v-else class="chat-bubble chat-bubble--pending">
              Trợ lý ảo chưa tìm thấy câu trả lời cho câu hỏi này. Câu hỏi của bạn đã được chuyển cho trainer, sẽ được phản hồi sớm nhất.
            </div>
          </div>
        </template>
      </div>

      <p v-if="sendError" class="chat-send-error">{{ sendError }}</p>
      <form class="chat-input-row" @submit.prevent="sendQuestion">
        <input v-model="questionText" type="text" placeholder="Nhập câu hỏi..." :disabled="sending" />
        <button type="submit" class="btn btn-primary btn-sm" :disabled="sending || !questionText.trim()">
          <Send :size="14" />
        </button>
      </form>
    </div>

    <button class="chat-toggle-btn" @click="toggleOpen">
      <X v-if="isOpen" :size="22" />
      <MessageCircle v-else :size="22" />
    </button>
  </div>
</template>

<style scoped src="./ChatWidget.css"></style>
