import http from '@/api/http'

export default {
    getAll(kw, page, size) {
        return http.get('/api/secure/users', { params: { kw, page, size } });
    },
    create(user) {
        return http.post('/api/secure/users', user);
    },
    update(id, user) {
        return http.put(`/api/secure/users/${id}`, user);
    },
    deactivate(id) {
        return http.delete(`/api/secure/users/${id}`);
    },
    reactivate(id) {
        return http.put(`/api/secure/users/${id}/reactivate`);
    },
    bulkImport(file) {
        const formData = new FormData();
        formData.append('file', file);
        return http.post('/api/secure/users/bulk-import', formData);
    }
}
