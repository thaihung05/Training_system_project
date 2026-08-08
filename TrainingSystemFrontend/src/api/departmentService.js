import http from '@/api/http'

export default {
    getAll() {
        return http.get('/api/departments');
    },
    create(dept) {
        return http.post('/api/secure/departments', dept);
    },
    update(id, dept) {
        return http.put(`/api/secure/departments/${id}`, dept);
    },
    remove(id) {
        return http.delete(`/api/secure/departments/${id}`);
    }
}
