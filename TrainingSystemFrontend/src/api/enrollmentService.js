import http from '@/api/http'

export default {
    getMyEnrollments() {
        return http.get('/api/secure/enrollments/my');
    },
    getProgress(enrollmentId) {
        return http.get(`/api/secure/enrollments/${enrollmentId}/progress`);
    },
    getRoster(courseId) {
        return http.get(`/api/secure/courses/${courseId}/enrollments`);
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
