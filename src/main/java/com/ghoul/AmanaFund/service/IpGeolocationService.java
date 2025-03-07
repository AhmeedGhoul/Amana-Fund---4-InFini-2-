package com.ghoul.AmanaFund.service;

import org.springframework.stereotype.Service;
import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class IpGeolocationService {

    private static final String IPIFY_URL = "https://api.ipify.org?format=json"; // IP address service
    private static final String GEOLOCATION_URL = "http://ip-api.com/json/"; // Geolocation service

    // Get IP address from Ipify service
    public String getIpFromIpify() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(IPIFY_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return parseIpFromResponse(responseBody); // Parse and return the IP address
            } else {
                return null; // In case of failure, return null
            }
        }
    }

    // Get country by IP fetched from Ipify
    public String getCountryByIp(String ipAddress) throws IOException {
        if (ipAddress != null) {
            return getCountryFromGeolocationApi(ipAddress); // Fetch country based on IP
        } else {
            return "Unknown"; // Return Unknown if IP is not found
        }
    }

    // Parse IP address from Ipify response
    private String parseIpFromResponse(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(responseBody);

        // Extract IP from the response JSON
        if (node.has("ip")) {
            return node.get("ip").asText();
        } else {
            return null; // Return null if IP is not present
        }
    }

    // Fetch country based on the given IP address using Geolocation API
    public String getCountryFromGeolocationApi(String ipAddress) throws IOException {
        String url = GEOLOCATION_URL + ipAddress;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return parseCountryFromResponse(responseBody); // Parse and return the country
            } else {
                return "Unknown"; // In case of failure, return a fallback value
            }
        }
    }

    // Parse country from the geolocation API response
    private String parseCountryFromResponse(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(responseBody);

        // Extract the country name from the response JSON
        if (node.has("country")) {
            return node.get("country").asText();
        } else {
            return "Unknown"; // Return "Unknown" if country is not present
        }
    }
}
