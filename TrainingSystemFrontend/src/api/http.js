import axios from "axios";
import Swal from "sweetalert2";
import { useAuthStore } from "../stores/auth";
import router from "../router";

const http = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let sessionExpiredHandled = false

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const auth = useAuthStore()
      const wasLoggedIn = auth.isLoggedIn
      auth.logout()
      if (wasLoggedIn && !sessionExpiredHandled) {
        sessionExpiredHandled = true
        Swal.fire({
          title: 'Phiên đăng nhập đã kết thúc',
          text: error.response?.data || 'Tài khoản của bạn đã bị đăng xuất. Vui lòng đăng nhập lại.',
          icon: 'info',
          confirmButtonText: 'Đăng nhập lại',
          confirmButtonColor: '#2F3E82',
        }).finally(() => {
          sessionExpiredHandled = false
        })
      }
      router.push({ name: 'Login' })
    }
    return Promise.reject(error)
  },
)

export default http;