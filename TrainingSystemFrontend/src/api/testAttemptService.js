import http from '@/api/http'

export default {
    start(testId) {
        return http.post(`/api/secure/tests/${testId}/attempts/start`);
    },
    getQuestionsForAttempt(testId) {
        return http.get(`/api/secure/tests/${testId}/questions-for-attempt`);
    },
    submit(attemptId, body) {
        return http.post(`/api/secure/attempts/${attemptId}/submit`, body);
    },
    getById(attemptId) {
        return http.get(`/api/secure/attempts/${attemptId}`);
    },
    getMy(testId) {
        return http.get('/api/secure/attempts/my', { params: { testId } });
    },
    getByTest(testId) {
        return http.get(`/api/secure/tests/${testId}/attempts`);
    }
}
