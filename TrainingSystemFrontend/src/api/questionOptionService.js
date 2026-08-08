import http from '@/api/http'

export default {
    create(questionId, option) {
        return http.post(`/api/secure/questions/${questionId}/options`, option);
    },
    remove(id) {
        return http.delete(`/api/secure/question-options/${id}`);
    },
    setCorrect(questionId, optionId) {
        return http.put(`/api/secure/questions/${questionId}/correct-option/${optionId}`);
    }
}
