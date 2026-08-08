import http from "@/api/http";

export default {
    ask(question, sessionId) {
        return http.post('/api/secure/chat-history/ask', { question, sessionId });
    },
    getBySession(sessionId) {
        return http.get(`/api/secure/chat-history/session/${sessionId}`);
    },
    getPending() {
        return http.get('/api/secure/chat-history/pending');
    },
    answer(id, answerText) {
        return http.put(`/api/secure/chat-history/${id}/answer`, { answer: answerText });
    }
}