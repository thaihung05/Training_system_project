import http from '@/api/http'

export default {
    getByCourse(courseId) {
        return http.get(`/api/secure/courses/${courseId}/tests`);
    },
    create(courseId, test) {
        return http.post(`/api/secure/courses/${courseId}/tests`, test);
    },
    update(id, test) {
        return http.put(`/api/secure/tests/${id}`, test);
    }
}
