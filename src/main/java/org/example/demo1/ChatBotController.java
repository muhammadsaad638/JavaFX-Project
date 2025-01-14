package org.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ChatBotController {
    @FXML private VBox chatWindow;
    @FXML private  VBox messagesArea;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;
    @FXML private Button minimizeButton;

    private DistilBERTChatBot chatBot;

    @FXML
    public void initialize() {
        chatBot = new DistilBERTChatBot();

        // Initialize send button
        sendButton.setOnAction(event -> sendMessage());

        // Enable sending message with Enter key
        messageInput.setOnAction(event -> sendMessage());
    }

    private void toggleChatWindow() {
        chatWindow.setVisible(!chatWindow.isVisible());
        if (chatWindow.isVisible()) {
            messageInput.requestFocus();
        }
    }

    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (message.isEmpty()) return;

        // Add user message to chat
        addUserMessage(message);
        messageInput.clear();

        // Process message and get response
        CompletableFuture.supplyAsync(() -> {
            // Using the existing context from DistilBERTChatBot
            String context ="""
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
            """; // Add your full context here
            return chatBot.getAnswerFromDistilBERT(message, context);
        }).thenAccept(response -> {
            Platform.runLater(() -> addBotMessage(response));
        });
    }

    private void addUserMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: black;");
        messageLabel.setStyle("-fx-background-color: #ADD8E6");
        messagesArea.getChildren().add(messageLabel);
    }

    private void addBotMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: black;");
        messagesArea.getChildren().add(messageLabel);
    }

    public void handleButtonHover(MouseEvent mouseEvent) {
    }

    public void handleButtonExit(MouseEvent mouseEvent) {
    }

    public static void openChatBot() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatBotController.class.getResource("ChatBot.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        // Adding first message to chatbot


        Stage stage =new Stage();
        stage.setScene(scene);
        stage.getIcons().add(new Image(ChatBotController.class.getResourceAsStream("/gpt.png")));
        stage.show();
    }
}