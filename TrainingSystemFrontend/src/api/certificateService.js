import http from '@/api/http'

export default {
    getMy() {
        return http.get('/api/secure/certificates/my');
    },
    getByCourse(courseId) {
        return http.get(`/api/secure/courses/${courseId}/certificates`);
    },
    updatePdfUrl(id, pdfUrl) {
        return http.put(`/api/secure/certificates/${id}/pdf-url`, { pdfUrl });
    }
}
