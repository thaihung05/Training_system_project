import http from "@/api/http";

export default {
    markComplete(id) {
        return http.put(`/api/secure/lesson-progress/${id}/complete`);
    }
}