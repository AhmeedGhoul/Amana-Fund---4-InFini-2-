package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Payment;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FraudDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionService.class);

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_API_KEY = "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"; // Remplacez par votre clé API

    /**
     * Vérifie si une transaction est frauduleuse en utilisant l'API OpenAI.
     *
     * @return true si la transaction est frauduleuse, sinon false.
     * @throws FraudDetectionException Si une erreur survient lors de la détection de fraude.
     */


        public boolean isFraudulent(Payment payment) throws FraudDetectionException {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(OPENAI_API_URL);
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Authorization", "Bearer " + OPENAI_API_KEY);

                // Construction de la requête
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", "gpt-3.5-turbo");

                JsonObject systemMessage = new JsonObject();
                systemMessage.addProperty("role", "system");
                systemMessage.addProperty("content", "Vous êtes un système de détection de fraude. Analysez la transaction et déterminez si elle est frauduleuse.");

                JsonObject userMessage = new JsonObject();
                userMessage.addProperty("role", "user");
                userMessage.addProperty("content", String.format(
                        "Transaction :\n- Montant : %.2f\n- Agent : %s\n- Date : %s\n- Statut : %s\nCette transaction est-elle frauduleuse ?",
                        payment.getAmount(), payment.getAgent(), payment.getDate_payment(), payment.getStatus()
                ));

                JsonArray messages = new JsonArray();
                messages.add(systemMessage);
                messages.add(userMessage);

                requestBody.add("messages", messages);
                httpPost.setEntity(new StringEntity(requestBody.toString()));

                // Envoyer la requête et récupérer la réponse
                String response = EntityUtils.toString(httpClient.execute(httpPost).getEntity());
                logger.info("Réponse de l'API OpenAI : {}", response);

                // Parser la réponse JSON
                JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

                // Extraire la réponse du modèle
                String content = jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString().toLowerCase();

                // Analyser la réponse pour déterminer si c'est une fraude
                return content.contains("oui") || content.contains("frauduleux") || content.contains("suspect");
            } catch (IOException e) {
                logger.error("Erreur lors de la communication avec l'API OpenAI", e);
                throw new FraudDetectionException("Erreur lors de la communication avec l'API OpenAI", e);
            } catch (Exception e) {
                logger.error("Erreur inattendue lors de la détection de fraude", e);
                throw new FraudDetectionException("Erreur inattendue lors de la détection de fraude", e);
            }
        }}