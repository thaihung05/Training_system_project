import http from '@/api/http'

export default {
    getMyEnrollments(page, size) {
        return http.get('/api/secure/enrollments/my', { params: { page, size } });
    },
    getProgress(enrollmentId) {
        return http.get(`/api/secure/enrollments/${enrollmentId}/progress`);
    },
    getRoster(courseId, page, size) {
        return http.get(`/api/secure/courses/${courseId}/enrollments`, { params: { page, size } });
    },
    enrollUsers(courseId, userIds) {
        return http.post(`/api/secure/courses/${courseId}/enrollments`, { userIds });
    },
    enrollByDepartment(courseId, departmentId) {
        return http.post(`/api/secure/courses/${courseId}/enrollments/by-department`, { departmentId });
    },
    unenroll(id) {
        return http.delete(`/api/secure/enrollments/${id}`);
    }
}
