import http from '@/api/http'

export default {
    uploadPdf(file) {
        const formData = new FormData()
        formData.append('file', file)
        return http.post('/api/secure/uploads/pdf', formData)
    },
    uploadImage(file) {
        const formData = new FormData()
        formData.append('file', file)
        return http.post('/api/secure/uploads/image', formData)
    }
}
