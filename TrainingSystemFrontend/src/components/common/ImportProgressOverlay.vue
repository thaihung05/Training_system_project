<script setup>
import { watch } from 'vue'
import { FileSpreadsheet } from '@lucide/vue'

const props = defineProps({
  active: { type: Boolean, default: false },
  label: { type: String, default: 'Đang nhập dữ liệu...' },
})

let previousBodyOverflow = ''
watch(
  () => props.active,
  (active) => {
    if (active) {
      previousBodyOverflow = document.body.style.overflow
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = previousBodyOverflow
    }
  },
)
</script>

<template>
  <Teleport to="body">
    <Transition name="import-overlay">
      <div v-if="active" class="import-overlay-backdrop" @click.stop @keydown.stop>
        <div class="import-overlay-card" role="alert" aria-live="assertive">
          <FileSpreadsheet :size="26" />
          <div class="import-overlay-label">{{ label }}</div>
          <div class="import-overlay-track">
            <div class="import-overlay-fill"></div>
          </div>
          <div class="import-overlay-note">Vui lòng không đóng hoặc tải lại trang cho đến khi hoàn tất.</div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped src="./ImportProgressOverlay.css"></style>
