import http from '@/api/http'

export default {
    getForCompose(testId, page, size) {
        return http.get(`/api/secure/tests/${testId}/questions`, { params: { page, size } });
    },
    create(testId, question) {
        return http.post(`/api/secure/tests/${testId}/questions`, question);
    },
    update(id, question) {
        return http.put(`/api/secure/questions/${id}`, question);
    },
    remove(id) {
        return http.delete(`/api/secure/questions/${id}`);
    },
    bulkImport(testId, file) {
        const formData = new FormData();
        formData.append('file', file);
        return http.post(`/api/secure/tests/${testId}/questions/bulk-import`, formData);
    }
}
