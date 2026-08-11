import http from '@/api/http'

export default {
    getCourses(kw, departmentId, page, size, activeOnly) {
        return http.get('/api/secure/courses', { params: { kw, departmentId, page, size, activeOnly } });
    },
    getCourseById(id) {
        return http.get(`/api/secure/courses/${id}`);
    },
    getMyCourses(page, size) {
        return http.get('/api/secure/courses/my', { params: { page, size } });
    },
    create(course) {
        return http.post('/api/secure/courses', course);
    },
    update(id, course) {
        return http.put(`/api/secure/courses/${id}`, course);
    },
    remove(id) {
        return http.delete(`/api/secure/courses/${id}`);
    }
}
