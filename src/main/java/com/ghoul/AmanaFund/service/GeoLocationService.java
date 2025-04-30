package com.ghoul.AmanaFund.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GeoLocationService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public double[] getCoordinates(String address, String city, String governorate) {
        try {
            String query = String.format("%s, %s, %s", address, city, governorate);
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + query.replace(" ", "+");

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.isArray() && jsonNode.size() > 0) {
                double lat = jsonNode.get(0).get("lat").asDouble();
                double lon = jsonNode.get(0).get("lon").asDouble();
                return new double[]{lat, lon};
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new double[]{0.0, 0.0};  // Default if not found
    }
}
