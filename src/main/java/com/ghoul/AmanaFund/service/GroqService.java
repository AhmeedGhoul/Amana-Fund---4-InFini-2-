package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ActivityLog;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GroqService {

    private String groqApiKey="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";

    private final OkHttpClient client = new OkHttpClient();

    public String analyzeLogs(List<ActivityLog> logs) throws IOException {
        // Construct a structured log summary for better analysis
        StringBuilder logDetails = new StringBuilder("Analyze these system logs and detect any suspicious activity.\n");
        for (ActivityLog log : logs) {
            logDetails.append("Timestamp: ").append(log.getActivityDate())
                    .append(", User: ").append(log.getUser())
                    .append(", Action: ").append(log.getActivityDescription())
                    .append(", IP: ").append(log.getIpAddress())
                    .append(", Country: ").append(log.getCountry())
                    .append("\n");
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("messages", List.of(
                new JSONObject().put("role", "user").put("content", logDetails.toString())
        ));
        requestBody.put("model", "llama-3.3-70b-versatile");

        Request request = new Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                .addHeader("Authorization", "Bearer " + groqApiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                // Parse response and clean output
                JSONObject jsonResponse = new JSONObject(response.body().string());
                String content = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                // Clean the response, keeping only the relevant assistant feedback
                return content.trim(); // Trim removes extra spaces or line breaks
            }
        }
        return "No response from AI.";
    }

}
