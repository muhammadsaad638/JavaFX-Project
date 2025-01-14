package org.example.demo1;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

public class DistilBERTChatBot {

    // Replace with your Hugging Face API Token
    private static final String HUGGING_FACE_API_TOKEN = "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";

    public static void main(String[] args) {
        // Question and context
        String question = "What is the contact number of the admin?";

        // Application context text
        String context = """
            Welcome to the Blood Donation App, designed to connect blood donors and recipients seamlessly. 
            Key features of the application include:
            - Users can sign up and register as donors, providing details like blood group and availability.
            - Recipients can search for donors near their location and communicate through a built-in chat feature.
            - For assistance, users can contact the admin at hafizsaad@gmail.com or call 03103434343.
            - The app provides calculators for blood donation requirements and blood compatibility.
            - A video tutorial is available on the home page to guide users.

            Blood donation requirements:
            - Minimum age is 18 years.
            - Weight should be at least 50 kg.
            - Donors should be in good health and free from infectious diseases.

            Blood group compatibility:
            - O-negative is the universal donor.
            - AB-positive is the universal recipient.

            Benefits of blood donation:
            - Helps save lives and promotes donor health by stimulating the production of new red blood cells.
            - A single donation can save up to three lives.
            """;

        // Call the Hugging Face API
        String answer = getAnswerFromDistilBERT(question, context);

        // Display the result
        System.out.println("Question: " + question);
        System.out.println("Answer: " + answer);
    }

    public static String getAnswerFromDistilBERT(String question, String context) {
        int maxRetries = 3; // Number of retries
        int retryInterval = 20000; // 20 seconds in milliseconds

        for (int i = 0; i < maxRetries; i++) {
            try {
                // Create JSON payload
                JSONObject payload = new JSONObject();
                payload.put("inputs", new JSONObject()
                        .put("question", question)
                        .put("context", context));

                // HTTP Request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api-inference.huggingface.co/models/distilbert-base-uncased-distilled-squad"))
                        .header("Authorization", "Bearer " + HUGGING_FACE_API_TOKEN)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                        .build();

                // Send Request
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse response
                String responseBody = response.body();
                System.out.println("Response: " + responseBody);

                JSONObject responseObject = new JSONObject(responseBody);
                if (responseObject.has("error") && responseObject.getString("error").contains("currently loading")) {
                    System.out.println("Model is loading. Retrying in " + (retryInterval / 1000) + " seconds...");
                    Thread.sleep(retryInterval); // Wait and retry
                    continue; // Retry the request
                }

                // Return the answer
                return responseObject.getString("answer");

            } catch (Exception e) {
                e.printStackTrace();
                return "Error while processing the request.";
            }
        }
        return "Model failed to load after multiple retries.";
    }

}
