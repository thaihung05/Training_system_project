/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author LENOVO
 */
public class ChatbotClient {
    
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public static String ask(String apiUrl, String question) throws Exception {
        String requestBody = MAPPER.writeValueAsString(Collections.singletonMap("question", question));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/ask"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
            throw  new RuntimeException("Chatbot service trả về status " + response.statusCode());
        Map<?,?> json = MAPPER.readValue(response.body(), Map.class);
        Object answer = json.get("answer");
        return answer != null ? answer.toString() : null;
    }
}
