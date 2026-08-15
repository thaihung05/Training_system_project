import http from "@/api/http";

export default {
    ask(question, sessionId) {
        return http.post('/api/secure/chat-history/ask', { question, sessionId });
    },
    getMy(page, size) {
        return http.get('/api/secure/chat-history/my', { params: { page, size } });
    },
    getBySession(sessionId) {
        return http.get(`/api/secure/chat-history/session/${sessionId}`);
    },
    getPending(page, size) {
        return http.get('/api/secure/chat-history/pending', { params: { page, size } });
    },
    getAnswered(page, size) {
        return http.get('/api/secure/chat-history/answered', { params: { page, size } });
    },
    answer(id, answerText) {
        return http.put(`/api/secure/chat-history/${id}/answer`, { answer: answerText });
    }
}