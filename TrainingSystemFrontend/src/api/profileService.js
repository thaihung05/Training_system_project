import http from '@/api/http'

export default {
  getMy() {
    return http.get('/api/secure/profile')
  },
  changePassword(oldPassword, newPassword) {
    return http.put('/api/secure/profile/password', { oldPassword, newPassword })
  }
}
