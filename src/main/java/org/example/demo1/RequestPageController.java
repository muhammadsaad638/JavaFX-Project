package org.example.demo1;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RequestPageController {
    public Button backButton;
    public Text matchesText;

    private LocationManager locationManager;
    @FXML
    public TextField fullNameField;
    @FXML
    public TextField phoneNumberField;
    @FXML
    public ComboBox bloodTypeCombo;
    @FXML
    public TextField hospitalAddressField;
    @FXML
    public ComboBox relationshipCombo;
    @FXML
    public Button searchButton;
    @FXML
    public FlowPane donorCardsContainer;
    @FXML
    public ContextMenu suggestionsMenu;
    @FXML
    public List<VBox> cards= new ArrayList<>();
    @FXML
    private ComboBox<String> sortComboBox;





    @FXML
    public void initialize(){
        locationManager=new LocationManager();
        locationManager.setLocationField(hospitalAddressField);
        locationManager.setSuggestionsMenu(suggestionsMenu);
        locationManager.initialize();
        // Add a listener for sorting
        sortComboBox.setOnAction(event -> sortCards());
    }




    public void showDonors(ActionEvent actionEvent) {
        // Getting requester's lat and lon
        double requestorLon= Double.parseDouble(locationManager.getLon());
        double requestorLat= Double.parseDouble(locationManager.getLat());

        String bloodType=(String) bloodTypeCombo.getValue();
        // Save requester details
        saveRequesterDetailsToDatabase(
                fullNameField.getText(),
                bloodType,
                hospitalAddressField.getText(),
                (String) relationshipCombo.getValue(),
                phoneNumberField.getText(),
                requestorLon,
                requestorLat
        );

        // Get donors matching the specified blood types
        List<String> bloodTypes = Arrays.asList(bloodType);
        List<Donor> donors = getDonorsByBloodTypes(bloodTypes);

        // Clear existing donor cards
        donorCardsContainer.getChildren().clear();

        // Add a card for each donor
        for (Donor donor : donors) {
            // Fetch the donor's username
            String username = getUsernameById(donor.getUserId());
            System.out.println(username);
            //System.out.println(donor.getGender());

            // Calculate distance and city
            double distance = RouteDistanceCalculator.calculateRouteDistanceAndCity(
                    requestorLat, requestorLon, donor.getLat(), donor.getLon()
            );
            String city = RouteDistanceCalculator.city;

            // Create a VBox to represent the donor card
            VBox donorCard = new VBox();
            donorCard.getStyleClass().add("donor-card");

            // Create the donor header
            HBox donorHeader = new HBox(12);
            donorHeader.getStyleClass().add("donor-header");

            // Donor profile image (placeholder)

            ImageView donorAvatar = new ImageView();
            donorAvatar.setFitHeight(48);
            donorAvatar.setFitWidth(48);
            donorAvatar.getStyleClass().add("donor-avatar");
            if(Objects.equals(donor.getGender(), "Male")){
                System.out.println("Male");
                // Set placeholder image (adjust path if needed)
                donorAvatar.setImage(new Image("male.png"));
                donorCard.getChildren().add(donorAvatar);
            }else if(Objects.equals(donor.getGender(), "Female")){
                System.out.println("Female");
                donorAvatar.setImage(new Image("female.png"));
                donorCard.getChildren().add(donorAvatar);
            }
            // Donor information
            VBox donorInfo = new VBox(4);
            Text nameText = new Text(donor.getFullName());
            nameText.getStyleClass().add("donor-name");

            HBox donorMeta = new HBox(8);
            donorMeta.getStyleClass().add("donor-meta");
            Text locationText = new Text(city);
            locationText.getStyleClass().add("donor-location");
            Text statusText = new Text("Active 5h ago"); // Hardcoded status
            statusText.getStyleClass().add("donor-status");
            donorMeta.getChildren().addAll(locationText, statusText);

            donorInfo.getChildren().addAll(nameText, donorMeta);
            donorHeader.getChildren().addAll( donorInfo);

            // Donor stats
            HBox donorStats = new HBox(12);
            donorStats.getStyleClass().add("donor-stats");

            // Blood Type Stat
            VBox bloodTypeStat = new VBox();
            bloodTypeStat.getStyleClass().add("stat-item");
            Text bloodTypeValue = new Text(donor.getBloodType());
            bloodTypeValue.getStyleClass().add("stat-value");
            Text bloodTypeLabel = new Text("Blood Type");
            bloodTypeLabel.getStyleClass().add("stat-label");
            bloodTypeStat.getChildren().addAll(bloodTypeValue, bloodTypeLabel);

            // Distance Stat
            VBox distanceStat = new VBox();
            distanceStat.getStyleClass().add("stat-item");
            Text distanceValue = new Text(String.format("%.1f km", distance));
            distanceValue.getStyleClass().add("stat-value");
            Text distanceLabel = new Text("Distance");
            distanceLabel.getStyleClass().add("stat-label");
            distanceStat.getChildren().addAll(distanceValue, distanceLabel);

            // Age Stat
            VBox donationsStat = new VBox();
            donationsStat.getStyleClass().add("stat-item");
            Text age = new Text(String.format("%d ", donor.getAge())); // Hardcoded example
            age.getStyleClass().add("stat-value");
            Text donationsLabel = new Text("Age");
            donationsLabel.getStyleClass().add("stat-label");
            donationsStat.getChildren().addAll(age, donationsLabel);

            donorStats.getChildren().addAll(bloodTypeStat, distanceStat, donationsStat);

            // Chat button
            Button chatButton = new Button("Chat with donor");
            chatButton.getStyleClass().add("request-button");

            // Update the chat button handler in the showDonors method
            chatButton.setOnAction(event -> {
                onChat(username);
            });


            // Assemble the donor card
            donorCard.getChildren().addAll(donorHeader, donorStats, chatButton);

            // Store cards in a list for further sorting
            this.cards.add(donorCard);

            // Add the donor card to the FlowPane container
            donorCardsContainer.getChildren().add(donorCard);



        }
        matchesText.setText("("+donors.size()+" "+"matches found"+")");

    }


    private void saveRequesterDetailsToDatabase(
            String full_name, String blood_type, String hospitalAddress,
            String relationship, String phone, double longitude, double latitude) {

        String query = "INSERT INTO RequesterDetails (id, full_name, blood_type, hospital_address, relationship, phone, latitude, longitude) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); // assuming a method that provides a DB connection
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set values for the prepared statement
            pstmt.setInt(1, 112); // Fetch the user ID from the relevant method or context
            pstmt.setString(2, full_name);
            pstmt.setString(3, blood_type);
            pstmt.setString(4, hospitalAddress);
            pstmt.setString(5, relationship);
            pstmt.setString(6, phone);
            pstmt.setDouble(7, latitude);
            pstmt.setDouble(8, longitude);

            // Execute the query
            pstmt.executeUpdate();
            System.out.println("Requester details saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving requester details: " + e.getMessage());
        }
    }

    public List<Donor> getDonorsByBloodTypes(List<String> bloodTypes) {
        List<Donor> donors = new ArrayList<>();

        // Construct the SQL query with placeholders for blood types
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM donors WHERE blood_type IN (");
        for (int i = 0; i < bloodTypes.size(); i++) {
            queryBuilder.append("?");
            if (i < bloodTypes.size() - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(")");

        String query = queryBuilder.toString();

        try (Connection conn = DatabaseConnection.getConnection(); // assuming a method that provides a DB connection
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            // Set the blood type parameters
            for (int i = 0; i < bloodTypes.size(); i++) {
                preparedStatement.setString(i + 1, bloodTypes.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                // Map each row to a Donor object
                String fullName = resultSet.getString("full_name");
                String bloodType = resultSet.getString("blood_type");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                String contactNumber = resultSet.getString("contact_number");
                String address = resultSet.getString("address");
                LocalDate lastDonationDate = resultSet.getDate("last_donation_date").toLocalDate();
                double lat = resultSet.getDouble("latitude");
                double lon = resultSet.getDouble("longitude");

                // Create Donor object
                Donor donor = new Donor(fullName, bloodType, age, gender, contactNumber, address, lastDonationDate, lat, lon);
                donor.setDonorId(resultSet.getInt("donor_id"));
                donor.setUserId(resultSet.getInt("id"));
                donor.setAvailabilityStatus(resultSet.getBoolean("availability_status"));

                // Add the Donor to the list
                donors.add(donor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return donors;
    }

    public static String getUsernameById(int userId) {
        String username = null;

        // SQL query
        String query = "SELECT username FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the userId parameter
            stmt.setInt(1, userId);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Retrieve the username from the result set
            if (rs.next()) {
                username = rs.getString("username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return username;
    }


    public void handleBack(ActionEvent actionEvent) throws IOException {
        TransitionManager.switchScreen((Stage) searchButton.getScene().getWindow(),"homePage","home");
    }


    // Handling Chat things
    @FXML
    private void onChat(String chatWith) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatWindow.fxml"));
            Parent chatRoot = loader.load();

            ChatController controller= loader.getController();
            controller.initializeChat(chatWith);

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat with " + chatWith);
            Scene scene=new Scene(chatRoot);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/chat.css")).toExternalForm());
            chatStage.setScene(scene);
            chatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sorting things
    private void sortCards() {
        // Get the selected sorting criteria
        String selectedCriteria = sortComboBox.getValue();

        if (selectedCriteria != null) {
            // Sort cards based on the selected criteria
            if (selectedCriteria.equals("Distance")) {
                // Sort by distance in ascending order
                cards.sort(Comparator.comparingDouble(this::extractDistance));

            } else if (selectedCriteria.equals("Age")) {
                // Sort by age (or donations count) in ascending order
                cards.sort(Comparator.comparingInt(this::extractAge));
            }

            // Update the UI: Clear and re-add sorted cards to the container
            donorCardsContainer.getChildren().clear();
            donorCardsContainer.getChildren().addAll(cards);
        }
    }

    private double extractDistance(VBox card) {
        // Extract the distance value from the donor card
        HBox stats = (HBox) card.getChildren().get(1); // Stats HBox is the second child
        VBox distanceStat = (VBox) stats.getChildren().get(1); // Distance stat is the second stat
        Text distanceText = (Text) distanceStat.getChildren().get(0); // Stat value is the first child
        String distanceString = distanceText.getText().replace(" km", ""); // Remove " km"
        return Double.parseDouble(distanceString);
    }

    private int extractAge(VBox card) {
        // Extract the donations (or age equivalent) value from the donor card
        HBox stats = (HBox) card.getChildren().get(1); // Stats HBox is the second child
        VBox ageStat = (VBox) stats.getChildren().get(2); // Donations stat is the third stat
        Text ageText = (Text) ageStat.getChildren().get(0); // Stat value is the first child
        return Integer.parseInt(ageText.getText().trim());
    }






}
