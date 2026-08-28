<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import profileService from '@/api/profileService'

const auth = useAuthStore()

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

async function submit() {
  errorMsg.value = ''
  successMsg.value = ''
  if (newPassword.value !== confirmPassword.value) {
    errorMsg.value = 'Mật khẩu mới nhập lại không khớp'
    return
  }
  saving.value = true
  try {
    await profileService.changePassword(oldPassword.value, newPassword.value)
    successMsg.value = 'Đổi mật khẩu thành công'
    oldPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } catch (err) {
    errorMsg.value = err.response?.data || 'Có lỗi xảy ra.'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Thông tin cá nhân</h1>
    </div>

    <div class="profile-body">
      <div class="card">
        <h2>Thông tin tài khoản</h2>
        <div class="profile-info-row">
          <span class="profile-info-label">Họ tên</span>
          <span class="profile-info-value">{{ auth.user?.name }}</span>
        </div>
        <div class="profile-info-row">
          <span class="profile-info-label">Username</span>
          <span class="profile-info-value">{{ auth.user?.username }}</span>
        </div>
        <div class="profile-info-row">
          <span class="profile-info-label">Email</span>
          <span class="profile-info-value">{{ auth.user?.email }}</span>
        </div>
        <div class="profile-info-row">
          <span class="profile-info-label">Vai trò</span>
          <span class="profile-info-value">{{ auth.user?.role }}</span>
        </div>
        <div class="profile-info-row">
          <span class="profile-info-label">Siêu thị</span>
          <span class="profile-info-value">{{ auth.user?.storeId ? auth.user.storeId.name : '-' }}</span>
        </div>
      </div>

      <div class="card">
        <h2>Đổi mật khẩu</h2>
        <p v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</p>
        <p v-if="successMsg" class="alert alert-success">{{ successMsg }}</p>
        <div class="form-field">
          <label>Mật khẩu hiện tại</label>
          <input v-model="oldPassword" type="password" class="input" />
        </div>
        <div class="form-field">
          <label>Mật khẩu mới</label>
          <input v-model="newPassword" type="password" class="input" />
        </div>
        <div class="form-field">
          <label>Nhập lại mật khẩu mới</label>
          <input v-model="confirmPassword" type="password" class="input" />
        </div>
        <div class="manage-form-actions">
          <button class="btn btn-primary" :disabled="saving" @click="submit">Đổi mật khẩu</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./ProfileView.css"></style>
