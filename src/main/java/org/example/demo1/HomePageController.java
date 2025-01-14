package org.example.demo1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HomePageController {
    public Button bot;
    public Button tutorialBtn;
    @FXML
    private StackPane mainStackPane;

    @FXML
    public Label inboxLabel;
    public Button inboxBtn;
//    @FXML
//    private static Label staticInboxLabel;

    public Button requirementsBtn;
    public Button eligibilityBtn;

    public static Label staticNotificationBadge;
    @FXML
    private Label notificationBadge;

    @FXML
    public void initialize(){
        new ClientConnection().connectToServer();
        staticNotificationBadge=notificationBadge;
//        staticInboxLabel = inboxLabel;
        updateInboxLabel();
        if(User.checkProfileCompletionStatus(User.userName)){
            createProfileBtn.setText("See Your Profile");
        }
        System.out.println("Notification Badge:"+ notificationBadge);

    }
    public Button createProfileBtn;
    @FXML
    private Button requestBloodBtn;


    public static void updateInboxLabel() {
            int totalUnread = MessageManager.getTotalUnreadCount();
            Platform.runLater(() -> {
                if (totalUnread > 0) {
                    System.out.println("In the condition");
                    staticNotificationBadge.setText(String.format("%d",totalUnread));
                    staticNotificationBadge.setVisible(true);
                } else {
                    staticNotificationBadge.setVisible(false);
                }
            });
        System.out.println(totalUnread);
    }

    @FXML
    private void openInbox() {
        try {
            Stage inboxStage = new Stage();

            // Main layout
            VBox mainLayout = new VBox();
            mainLayout.setStyle("-fx-background-color: white;");

            // Header
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(15, 20, 15, 20));
            header.setStyle("-fx-background-color: #1a73e8;");

            Label titleLabel = new Label("Inbox");
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button refreshBtn = new Button("⟳");
            refreshBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16;");

            header.getChildren().addAll(titleLabel, spacer, refreshBtn);

            // Scroll container for chat previews
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: white;");

            VBox chatsContainer = new VBox();
            chatsContainer.setStyle("-fx-background-color: white;");

            Map<String, List<String>> allChats = MessageManager.getAllChats();
            Map<String, Integer> unreadMessages = MessageManager.getUnreadCounts();

// Define a DateTimeFormatter for the time format (e.g., "hh:mm a")
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

// Sort chats by the last message's time (newest first)
            List<Map.Entry<String, List<String>>> sortedChats = new ArrayList<>(allChats.entrySet());
            sortedChats.sort((a, b) -> {
                // Get the last message from each chat
                String lastMessageA = a.getValue().isEmpty() ? "" : a.getValue().get(a.getValue().size() - 1);
                String lastMessageB = b.getValue().isEmpty() ? "" : b.getValue().get(b.getValue().size() - 1);

                // Extract the time portion from each message
                String timePartA = lastMessageA.contains("@") ? lastMessageA.split("@")[1].trim() : "12:00 AM";
                String timePartB = lastMessageB.contains("@") ? lastMessageB.split("@")[1].trim() : "12:00 AM";

                // Parse the time strings into LocalTime objects
                LocalTime timeA = LocalTime.parse(timePartA, timeFormatter);
                LocalTime timeB = LocalTime.parse(timePartB, timeFormatter);

                // Compare times (newest first)
                return timeB.compareTo(timeA);
            });

// Populate the UI with sorted chats
            for (Map.Entry<String, List<String>> entry : sortedChats) {
                String sender = entry.getKey();
                List<String> messages = entry.getValue();
                int unreadCount = unreadMessages.getOrDefault(sender, 0);

                // Get last message and time
                String lastMessage = messages.isEmpty() ? "" : messages.get(messages.size() - 1);
                String time = lastMessage.contains("@") ? lastMessage.split("@")[1].trim() : "12:00 AM";

                ChatPreview chatPreview = new ChatPreview(sender, lastMessage.split("@")[0].trim(), time, unreadCount);
                chatPreview.setOnMouseClicked(e -> openChat(sender));

                chatsContainer.getChildren().add(chatPreview);
            }
            scrollPane.setContent(chatsContainer);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            // Empty state
            if (allChats.isEmpty()) {
                VBox emptyState = new VBox(20);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(50));

                Label emptyLabel = new Label("No messages yet");
                emptyLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #666666;");

                Label subLabel = new Label("Start a new chat to begin messaging");
                subLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #999999;");

                emptyState.getChildren().addAll(emptyLabel, subLabel);
                mainLayout.getChildren().addAll(header, emptyState);
            } else {
                mainLayout.getChildren().addAll(header, scrollPane);
            }

            Scene inboxScene = new Scene(mainLayout, 400, 600);
            inboxStage.setTitle("Inbox");
            inboxStage.setScene(inboxScene);
            inboxStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openChat(String chatWith) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatWindow.fxml"));
            Parent chatRoot = loader.load();

            ChatController controller = loader.getController();
            controller.initializeChat(chatWith);

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat with " + chatWith);
            Scene scene=new Scene(chatRoot);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/chat.css")).toExternalForm());
            chatStage.setScene(scene);
            chatStage.show();

            // Mark messages as read when opening chat
            MessageManager.markAsRead(chatWith);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void openRequestPage(ActionEvent actionEvent) throws IOException {
        TransitionManager.GoToRequestPage((Stage) requestBloodBtn.getScene().getWindow());

    }

    public void goToProfilePage(ActionEvent actionEvent) throws IOException {
        if(createProfileBtn.getText()=="See Your Profile"){
            loadProfileView();
        }
        else {
            TransitionManager.GoToBloodDonationProfile((Stage) createProfileBtn.getScene().getWindow());
        }
    }

    public void goToRequirementsPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TransitionManager.class.getResource("requirementsPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage =new Stage();
        stage.setScene(scene);
        stage.show();
    }

    public void openEligibilityInfo(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TransitionManager.class.getResource("compatibility.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage =new Stage();
        stage.setScene(scene);
        stage.show();
    }

    public void logout(ActionEvent actionEvent) {
    }

    public void openProfile(ActionEvent actionEvent) {
    }

    public void openBot(ActionEvent actionEvent) throws IOException {
        ChatBotController.openChatBot();
    }

    public void playTutorial(ActionEvent actionEvent) {
        VideoPlayerApp videoPlayerApp = new VideoPlayerApp();
        String videoPath = "C:\\Users\\Muhammad Saad\\Videos\\Search.mp4"; // Provide the path to your video
        Stage videoStage = new Stage(); // Create a new stage for the video playback
        videoPlayerApp.playVideo(videoPath, videoStage); // Play the video in the new stage
          }


    // Chat preview inner class
    private static class ChatPreview extends HBox {
        public ChatPreview(String sender, String lastMessage, String time, int unreadCount) {
            this.setAlignment(Pos.CENTER_LEFT);
            this.setPadding(new Insets(10));
            this.setSpacing(10);
            this.setPrefHeight(70);
            this.setStyle("-fx-background-color: white; -fx-border-color: #E8E8E8; -fx-border-width: 0 0 1 0;");

            // Profile Circle
            Circle profilePic = new Circle(25);
            profilePic.setFill(Color.LIGHTGRAY);
            Text initials = new Text(sender.substring(0, 1).toUpperCase());
            initials.setFill(Color.WHITE);
            StackPane profile = new StackPane(profilePic, initials);

            // Message Content
            VBox contentBox = new VBox(5);
            contentBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(contentBox, Priority.ALWAYS);

            // Header with name and time
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label(sender);
            nameLabel.setStyle("-fx-font-weight: bold;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label timeLabel = new Label(time);
            timeLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12;");
            header.getChildren().addAll(nameLabel, spacer, timeLabel);

            // Message preview
            Label messagePreview = new Label(lastMessage);
            messagePreview.setStyle("-fx-text-fill: #666666;");
            messagePreview.setMaxWidth(Double.MAX_VALUE);
            messagePreview.setTextOverrun(OverrunStyle.ELLIPSIS);

            contentBox.getChildren().addAll(header, messagePreview);

            // Unread count badge
            if (unreadCount > 0) {
                StackPane badge = new StackPane();
                Circle badgeCircle = new Circle(10);
                badgeCircle.setFill(Color.valueOf("#1a73e8"));
                Label countLabel = new Label(String.valueOf(unreadCount));
                countLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
                badge.getChildren().addAll(badgeCircle, countLabel);

                this.getChildren().addAll(profile, contentBox, badge);
            } else {
                this.getChildren().addAll(profile, contentBox);
            }

            // Hover effect
            this.setOnMouseEntered(e ->
                    this.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E8E8E8; -fx-border-width: 0 0 1 0;"));
            this.setOnMouseExited(e ->
                    this.setStyle("-fx-background-color: white; -fx-border-color: #E8E8E8; -fx-border-width: 0 0 1 0;"));
        }
    }

    public void loadProfileView() {
        try {
            // Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bloodDonationProfile.fxml"));
            Parent profileView = loader.load();

            // Get the controller
            BloodDonationProfileController profileController = loader.getController();

            // Get donor data for current user
            getDonorData(User.getId()).ifPresent(donor -> {
                // Customize header
                VBox headerSection = profileController.headerSection;
                headerSection.getChildren().clear(); // Remove existing header content

                // Create custom header with profile icon and username
                HBox customHeader = createCustomHeader(User.userName);
                headerSection.getChildren().add(customHeader);

                // Populate form fields
                populateFormFields(profileController, donor);

                // Update submit button text
                Button submitButton = profileController.getProfileSubmitBtn();
                submitButton.setText("Update Profile");
            });

            // Set the profile view in the content area
            Scene scene= new Scene(profileView);
            Stage stage= new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/profile.png")));
            stage.setTitle("UPDATE PROFILE");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle error appropriately
        }
    }

    private HBox createCustomHeader(String username) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Create profile icon
        Circle profileIcon = new Circle(25);
        profileIcon.setFill(new ImagePattern(new Image("/profile.png")));

        // Create username label
        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #d32f2f;");

        header.getChildren().addAll(profileIcon, usernameLabel);
        System.out.println("Custom Header Created");
        return header;
    }

    private Optional<Donor> getDonorData(int userId) {
        String query = "SELECT * FROM donors WHERE id = ?";
        Donor donor = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                donor = new Donor(
                        rs.getString("full_name"),
                        rs.getString("blood_type"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("contact_number"),
                        rs.getString("address"),
                        rs.getDate("last_donation_date").toLocalDate(),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error appropriately
        }

        return Optional.ofNullable(donor);
    }

    private void populateFormFields(BloodDonationProfileController controller, Donor donor) {
        // Set text fields
        controller.getFullNameField().setText(donor.getFullName());
        controller.getPhoneField().setText(donor.getContactNumber());
        controller.getLocationField().setText(donor.getAddress());
        controller.getAgeField().setText(String.valueOf(donor.getAge()));

        // Set combo boxes
        controller.getBloodTypeCombo().setValue(donor.getBloodType());
        controller.getGenderCombo().setValue(donor.getGender());

        // Set date picker
        if (donor.getLastDonationDate() != null) {
            controller.getLastDonationDate().setValue(donor.getLastDonationDate());
        }

        System.out.println("Fields set");

        // Store donor ID for update operation
//        controller.setdonorId(donor.getDonorId());
    }



}
