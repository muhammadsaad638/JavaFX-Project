package org.example.demo1;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatController {
    public Circle avatarCircle;
    public Label chatName;
    public Label statusLabel;
    public Text avatarText;
    private Label timeLabel;
    private String time;
    @FXML private ListView<String> messageList;
    @FXML private TextField messageInput;

    private String chatWith;

    public void initializeChat(String chatWith) {
        this.chatWith = chatWith;

        registerOpenChat(chatWith);
        loadExistingMessages(chatWith);
        setupCloseHandler();
        setupChatDetails(chatWith);
        setupMessageListCellFactory();
    }

    private void registerOpenChat(String chatWith) {
        MessageManager.registerOpenChat(chatWith, this);
    }

    private void loadExistingMessages(String chatWith) {
        List<String> messages = MessageManager.getMessagesForChat(chatWith);
        messageList.getItems().addAll(messages);
    }

    private void setupCloseHandler() {
        messageList.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs2, oldWin, newWin) -> {
                    if (newWin != null) {
                        Stage stage = (Stage) newWin;
                        stage.setOnCloseRequest(event -> unregisterOpenChat());
                    }
                });
            }
        });
    }

    private void setupChatDetails(String chatWith) {
        chatName.setText(chatWith);
        if(ChatServer.getClients().containsKey(chatWith)){
            statusLabel.setText("Online");
        }else {
            statusLabel.setText("Offline");
        }
        avatarText.setText(chatWith.substring(0, 1).toUpperCase());
    }


    private void unregisterOpenChat() {
        MessageManager.unregisterOpenChat(chatWith);
    }

    private void setupMessageListCellFactory() {
        messageList.setCellFactory(param -> new ListCell<String>() {
            private HBox messageContainer;
            private Label messageLabel;
            private Label timeLabel;
            private VBox messageBox;

            {
                initializeMessageCell();
            }

            @Override
            protected void updateItem(String message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setGraphic(null);
                } else {
                    updateMessageCell(message);
                    setGraphic(messageContainer);
                }
            }

            private void initializeMessageCell() {
                messageContainer = new HBox(5);
                messageLabel = new Label();
                timeLabel = new Label();
                timeLabel.getStyleClass().add("message-time");

                messageBox = new VBox(3);
                messageBox.getChildren().addAll(messageLabel, timeLabel);
                messageBox.getStyleClass().add("message-bubble");

                messageContainer.getChildren().add(messageBox);
            }

            private void updateMessageCell(String message) {
                boolean isSent = message.startsWith("You:");

                // Set alignment and styles
                messageContainer.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                messageBox.getStyleClass().removeAll("sent", "received");
                messageBox.getStyleClass().add(isSent ? "sent" : "received");

                // Set message content
                String[] parts = message.split("@");
                String content = parts[0].substring(parts[0].indexOf(":") + 1).trim(); // Extract the message
                String messageTime = parts.length > 1 ? parts[1].trim() : ""; // Extract the time

                // Set the message text and time
                messageLabel.setText(content);
                timeLabel.setText(messageTime);
                setGraphic(messageContainer); // Set the cell graphic

            }
        });
    }





//    public void initializeChat(String chatWith) {
//        this.chatWith = chatWith;
//
//        // Register this chat window as open
//        MessageManager.registerOpenChat(chatWith, this);
//
//        // Load existing messages
//        List<String> messages = MessageManager.getMessagesForChat(chatWith);
//        messageList.getItems().addAll(messages);
//
//        // Add window close handler
//        messageList.sceneProperty().addListener((obs, oldScene, newScene) -> {
//            if (newScene != null) {
//                newScene.windowProperty().addListener((obs2, oldWin, newWin) -> {
//                    if (newWin != null) {
//                        Stage stage = (Stage) newWin;
//                        stage.setOnCloseRequest(event -> {
//                            // Unregister chat window when closed
//                            MessageManager.unregisterOpenChat(chatWith);
//                        });
//                    }
//                });
//            }
//        });
//
//        // Set up the chat name and avatar
//        chatName.setText(chatWith);
//        statusLabel.setText("Online");
//        avatarText.setText(chatWith.substring(0, 1).toUpperCase());
//        // Set up custom cell factory for messages
//        messageList.setCellFactory(param -> new ListCell<String>() {
//            private HBox messageContainer;
//            private Label messageLabel;
//            private VBox messageBox;
//            {
//                messageContainer = new HBox(5);
//                messageLabel = new Label();
//                timeLabel = new Label();
//                timeLabel.getStyleClass().add("message-time");
//
//                messageBox = new VBox(3);
//                messageBox.getChildren().addAll(messageLabel, timeLabel);
//                messageBox.getStyleClass().add("message-bubble");
//
//                messageContainer.getChildren().add(messageBox);
//            }
//
//            @Override
//            protected void updateItem(String message, boolean empty) {
//                super.updateItem(message, empty);
//
//                if (empty || message == null) {
//                    setGraphic(null);
//                } else {
//                    // Determine if message is sent or received
//                    boolean isSent = message.startsWith("You:");
//
//                    // Set message alignment and style
//                    messageContainer.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
//                    messageBox.getStyleClass().removeAll("sent", "received");
//                    messageBox.getStyleClass().add(isSent ? "sent" : "received");
//
//                    // Set message content
//                    String content = message.substring(message.indexOf(":") + 1).trim();
//                    messageLabel.setText(content);
//
//                    // Set time (you should implement actual message time tracking)
//                    String time=ClientConnection.time;
//                    timeLabel.setText(time);
//
//                    setGraphic(messageContainer);
//                }
//            }
//        });
//    }
//

    public void appendMessage(String message) {

        messageList.getItems().add(message);
    }

    @FXML
    private void sendMessage() {
        String message = messageInput.getText();
        if (!message.isEmpty()) {
            // Send message to server
//            ClientConnection.getWriter().println(chatWith + ":" + message);

            // Get the current time in the desired format
            LocalTime currentTime = LocalTime.now();
            String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("hh:mm a"));

            // Append the time to the message
            String messageWithTime = message + "@" + formattedTime;

            // Send the message with time to the server
            ClientConnection.getWriter().println(chatWith + ":" + messageWithTime);

            // Add message to local display
            messageList.getItems().add("You: " + messageWithTime);
            messageInput.clear();
            messageInput.requestFocus();
            MessageDatabaseManager.storeMessage(User.userName,chatWith,"You"+messageWithTime);
            // Store message locally
            MessageManager.getMessagesForChat(chatWith).add("You: " + messageWithTime);
        }
    }

}