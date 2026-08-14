import http from '@/api/http'

export default {
    getAll() {
        return http.get('/api/secure/chains');
    },
    create(chain) {
        return http.post('/api/secure/chains', chain);
    },
    update(id, chain) {
        return http.put(`/api/secure/chains/${id}`, chain);
    },
    remove(id) {
        return http.delete(`/api/secure/chains/${id}`);
    }
}
