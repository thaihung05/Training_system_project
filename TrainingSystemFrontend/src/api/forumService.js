import http from '@/api/http'

export default {
  getByCourse(courseId, page, size) {
    return http.get(`/api/secure/courses/${courseId}/forum/questions`, { params: { page, size } })
  },
  ask(courseId, content) {
    return http.post(`/api/secure/courses/${courseId}/forum/questions`, { content })
  },
  answer(questionId, content) {
    return http.post(`/api/secure/forum/questions/${questionId}/answers`, { content })
  },
}
