import http from '@/api/http'

export default {
    getCourses(kw, departmentId) {
        return http.get('/api/secure/courses', { params: { kw, departmentId } });
    },
    getCourseById(id) {
        return http.get(`/api/secure/courses/${id}`);
    },
    getMyCourses() {
        return http.get('/api/secure/courses/my');
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
