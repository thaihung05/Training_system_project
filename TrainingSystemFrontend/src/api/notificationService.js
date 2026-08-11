import http from '@/api/http'

export default {
  getMy(unreadOnly, page, size) {
    return http.get('/api/secure/notifications', { params: { unreadOnly, page, size } })
  },
  unreadCount() {
    return http.get('/api/secure/notifications/unread-count')
  },
  markRead(id) {
    return http.put(`/api/secure/notifications/${id}/read`)
  },
  markAllRead() {
    return http.put('/api/secure/notifications/read-all')
  },
  remove(id) {
    return http.delete(`/api/secure/notifications/${id}`)
  }
}