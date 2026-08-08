<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const loading = ref(false)

const auth = useAuthStore()
const router = useRouter()

async function handleSubmit() {
  errorMessage.value = ''
  loading.value = true
  try {
    await auth.login(username.value, password.value)
    if (auth.isAdmin) {
      router.push({ name: 'admin-dashboard' })
    } else {
      router.push('/')
    }
  } catch (err) {
    errorMessage.value = err.response?.data || 'Có lỗi xảy ra, thử lại sau'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="login-mark">TLH</div>
      <div class="login-heading">
        <h1>Đăng nhập hệ thống</h1>
        <p class="page-subtitle">Sử dụng tài khoản được cấp bởi quản trị viên công ty</p>
      </div>

      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-field">
          <label>Tên đăng nhập</label>
          <input v-model="username" type="text" class="input" required />
        </div>
        <div class="form-field">
          <label>Mật khẩu</label>
          <input v-model="password" type="password" class="input" required />
        </div>

        <p v-if="errorMessage" class="alert alert-error">{{ errorMessage }}</p>

        <button type="submit" class="btn btn-primary login-submit" :disabled="loading">
          {{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
        </button>
      </form>

      <div class="login-footer">
        Chưa có tài khoản hoặc quên thông tin đăng nhập?<br />
        Liên hệ phòng IT nội bộ để được cấp/khôi phục tài khoản.
      </div>
    </div>
  </div>
</template>

<style scoped src="./LoginView.css"></style>