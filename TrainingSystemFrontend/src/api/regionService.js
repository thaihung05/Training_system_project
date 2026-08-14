import http from '@/api/http'

export default {
    getAll() {
        return http.get('/api/secure/regions');
    },
    create(region) {
        return http.post('/api/secure/regions', region);
    },
    update(id, region) {
        return http.put(`/api/secure/regions/${id}`, region);
    },
    remove(id) {
        return http.delete(`/api/secure/regions/${id}`);
    }
}
