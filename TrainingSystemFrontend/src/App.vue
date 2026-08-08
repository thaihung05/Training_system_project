<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRoute } from 'vue-router'
import AppNavbar from '@/components/nav/AppNavbar.vue'
import ChatWidget from '@/components/chat/ChatWidget.vue'

const auth = useAuthStore()
const route = useRoute()
</script>

<template>
  <AppNavbar v-if="auth.isLoggedIn && !route.meta.hideNavbar" />
  <RouterView />
  <ChatWidget v-if="auth.isLoggedIn" />
</template>
