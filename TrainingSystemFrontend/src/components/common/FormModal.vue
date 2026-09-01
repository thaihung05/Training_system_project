<script setup>
import { nextTick, onMounted, onUnmounted, ref } from 'vue'
import { X } from '@lucide/vue'

defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
  submitLabel: { type: String, default: 'Lưu' },
  saving: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  size: { type: String, default: 'medium' },
  hideSubmit: { type: Boolean, default: false },
})

const emit = defineEmits(['close', 'submit'])
const panel = ref(null)
let previousBodyOverflow = ''

onMounted(async () => {
  previousBodyOverflow = document.body.style.overflow
  document.body.style.overflow = 'hidden'
  await nextTick()
  panel.value?.querySelector('.form-modal-body input:not([type="hidden"]), .form-modal-body textarea, .form-modal-body select')?.focus()
})

onUnmounted(() => {
  document.body.style.overflow = previousBodyOverflow
})
</script>

<template>
  <Teleport to="body">
    <Transition name="form-modal">
      <div class="form-modal-backdrop" @click.self="emit('close')" @keydown.esc="emit('close')">
        <form
          ref="panel"
          class="form-modal-panel"
          :class="`form-modal-panel--${size}`"
          role="dialog"
          aria-modal="true"
          :aria-label="title"
          @submit.prevent="emit('submit')"
        >
          <header class="form-modal-header">
            <div>
              <h2>{{ title }}</h2>
              <p v-if="description">{{ description }}</p>
            </div>
            <button type="button" class="form-modal-close" @click="emit('close')">
              <X :size="20" />
            </button>
          </header>

          <div class="form-modal-body">
            <slot />
          </div>

          <footer class="form-modal-footer">
            <slot name="footer">
              <button type="button" class="btn btn-secondary" @click="emit('close')">Hủy</button>
              <button v-if="!hideSubmit" type="submit" class="btn btn-primary" :disabled="saving || disabled">
                {{ saving ? 'Đang lưu…' : submitLabel }}
              </button>
            </slot>
          </footer>
        </form>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped src="./FormModal.css"></style>
