import http from '@/api/http'

export default {
    getAll() {
        return http.get('/api/secure/stores');
    },
    create(store) {
        return http.post('/api/secure/stores', store);
    },
    update(id, store) {
        return http.put(`/api/secure/stores/${id}`, store);
    },
    remove(id) {
        return http.delete(`/api/secure/stores/${id}`);
    }
}
