package org.example.demo1;

import javafx.application.Platform;
import java.util.*;

public class MessageManager {
    // Store messages for each chat conversation
    private static Map<String, List<String>> chatMessages = new HashMap<>();
    // Store unread message counts
    private static Map<String, Integer> unreadCounts = new HashMap<>();

    // ... other existing methods ...

    // Add this new method to get all chats
    public static Map<String, List<String>> getAllChats() {
        return new HashMap<>(chatMessages);
    }

    // Track open chat windows
    private static Map<String, ChatController> openChats = new HashMap<>();

    public static void registerOpenChat(String sender, ChatController controller) {
        openChats.put(sender, controller);
    }

    public static void unregisterOpenChat(String sender) {
        openChats.remove(sender);
    }

    public static void handleIncomingMessage(String message) {
        // Format: "sender: message"
        String[] parts = message.split(":", 2);
        String sender = parts[0].trim();

        // Store message
        chatMessages.putIfAbsent(sender, new ArrayList<>());
        chatMessages.get(sender).add(message);

        // Check if chat window is open
        ChatController openChatController = openChats.get(sender);
        if (openChatController != null) {
            System.out.println(openChatController);
            // If chat is open, append message directly to chat window
            Platform.runLater(() -> {
                openChatController.appendMessage(message);
            });
        } else {
            System.out.println("Controller is null");
            // If chat is not open, increment unread count
            unreadCounts.merge(sender, 1, Integer::sum);
            Platform.runLater(() -> {
                HomePageController.updateInboxLabel();
            });
        }
    }

    public static List<String> getMessagesForChat(String sender) {
        return chatMessages.getOrDefault(sender, new ArrayList<>());
    }

    public static Map<String, Integer> getUnreadCounts() {
        return new HashMap<>(unreadCounts);
    }

    public static void markAsRead(String sender) {
        unreadCounts.remove(sender);
        Platform.runLater(() -> {
            HomePageController.updateInboxLabel();
        });
    }

    public static int getTotalUnreadCount() {
        return unreadCounts.values().stream().mapToInt(Integer::intValue).sum();
    }
}