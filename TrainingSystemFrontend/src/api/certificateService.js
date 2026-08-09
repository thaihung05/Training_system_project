import http from '@/api/http'

export default {
    getMy(page, size) {
        return http.get('/api/secure/certificates/my', { params: { page, size } });
    },
    getByCourse(courseId, page, size) {
        return http.get(`/api/secure/courses/${courseId}/certificates`, { params: { page, size } });
    },
    updatePdfUrl(id, pdfUrl) {
        return http.put(`/api/secure/certificates/${id}/pdf-url`, { pdfUrl });
    }
}
