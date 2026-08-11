import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '@/api/http'
import profileService from '@/api/profileService'

export const useAuthStore = defineStore('auth', () => {
    function readStoredUser() {
        try {
            const raw = localStorage.getItem('user')
            return raw ? JSON.parse(raw) : null
        } catch {
            localStorage.removeItem('user')
            return null
        }
    }

    const token = ref(localStorage.getItem('token') || null);
    const user = ref(readStoredUser());
    const isLoggedIn = computed(() => !!token.value);
    const role = computed(() => user.value?.role || null);
    const isAdmin = computed(() => role.value === 'ADMIN');
    const isTrainer = computed(() => role.value === 'TRAINER');
    const isTrainerOrAdmin = computed(() => isAdmin.value || isTrainer.value);
    const isEmployee = computed(() => role.value === 'EMPLOYEE');

    async function login(username, password) {
        const res = await http.post('/api/users/login', { username, password });
        token.value = res.data.token;
        localStorage.setItem('token', token.value);
        try {
            const profileRes = await profileService.getMy()
            user.value = profileRes.data;
            localStorage.setItem('user', JSON.stringify(user.value));
        } catch (err) {
            token.value = null;
            user.value = null;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            throw err;
        }
    }

    function logout() {
        token.value = null;
        user.value = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }

    return { token, user, isLoggedIn, role, isAdmin, isTrainer, isTrainerOrAdmin, isEmployee, login, logout }
})