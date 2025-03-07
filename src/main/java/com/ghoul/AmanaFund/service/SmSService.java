package com.ghoul.AmanaFund.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SmSService {

    @Value("${infobip.api.url}")
    private String apiUrl;

    @Value("${infobip.api.key}")
    private String apiKey;

    @Value("${infobip.sender}")
    private String senderId;

    private final OkHttpClient client = new OkHttpClient();

    public void sendSms(String phoneNumber, String message) {
        String jsonBody = "{"
                + "\"messages\":[{"
                + "\"destinations\":[{\"to\":\"" + phoneNumber + "\"}],"
                + "\"from\":\"" + senderId + "\","
                + "\"text\":\"" + message + "\""
                + "}]"
                + "}";

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(apiUrl + "/sms/2/text/advanced")
                .post(body)
                .addHeader("Authorization", "App " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("✅ SMS sent successfully: " + response.body().string());
            } else {
                System.err.println("❌ Error sending SMS: " + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
