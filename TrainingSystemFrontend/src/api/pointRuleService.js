import http from '@/api/http'

export default {
    getAll() {
        return http.get('/api/secure/point-rules');
    },
    create(rule) {
        return http.post('/api/secure/point-rules', rule);
    },
    update(id, rule) {
        return http.put(`/api/secure/point-rules/${id}`, rule);
    },
    remove(id) {
        return http.delete(`/api/secure/point-rules/${id}`);
    }
}
