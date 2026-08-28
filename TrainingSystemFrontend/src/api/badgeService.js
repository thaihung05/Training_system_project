import http from '@/api/http'

export default {
    getAll() {
        return http.get('/api/badges');
    },
    getMy() {
        return http.get('/api/secure/badges/my');
    },
    create(badge) {
        return http.post('/api/secure/badges', badge);
    },
    update(id, badge) {
        return http.put(`/api/secure/badges/${id}`, badge);
    },
    remove(id) {
        return http.delete(`/api/secure/badges/${id}`);
    }
}
