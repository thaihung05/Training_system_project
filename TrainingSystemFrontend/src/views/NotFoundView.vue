<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

function goHome() {
  router.push(auth.isLoggedIn ? '/' : { name: 'Login' })
}
</script>

<template>
  <div class="notfound-page">
    <div class="notfound-code">404</div>
    <h1>Không tìm thấy trang</h1>
    <p class="page-subtitle">Đường dẫn này không tồn tại hoặc đã bị thay đổi.</p>
    <button class="btn btn-primary" @click="goHome">Về trang chủ</button>
  </div>
</template>

<style scoped src="./NotFoundView.css"></style>
