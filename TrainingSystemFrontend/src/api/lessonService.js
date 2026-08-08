import http from '@/api/http'

export default {
    getByCourse(courseId) {
        return http.get(`/api/secure/courses/${courseId}/lessons`);
    },
    create(courseId, lesson) {
        return http.post(`/api/secure/courses/${courseId}/lessons`, lesson);
    },
    update(id, lesson) {
        return http.put(`/api/secure/lessons/${id}`, lesson);
    },
    remove(id) {
        return http.delete(`/api/secure/lessons/${id}`);
    },
    reorder(courseId, orderedLessonIds) {
        return http.put(`/api/secure/courses/${courseId}/lessons/reorder`, { orderedLessonIds });
    }
}
