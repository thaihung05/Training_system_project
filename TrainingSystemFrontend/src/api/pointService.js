import http from '@/api/http'

export default {
  getMy() {
    return http.get('/api/secure/points/my')
  },
  getLeaderboard(limit) {
    return http.get('/api/secure/points/leaderboard', { params: { limit } })
  },
  getTotalIssued() {
    return http.get('/api/secure/points/total-issued')
  },
  getUserPoints(userId) {
    return http.get(`/api/secure/points/user/${userId}`)
  },
}
