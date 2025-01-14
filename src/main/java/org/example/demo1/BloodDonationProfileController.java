package org.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BloodDonationProfileController {

    public VBox headerSection;
    public Button skipButton;
    private LocationManager locationManager;

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField ageField;
    @FXML
    private  ComboBox<String> bloodTypeCombo;
    @FXML
    private DatePicker lastDonationDate;
    @FXML
    private Button profileSubmitBtn;
    @FXML
    private ComboBox<String> genderCombo;
    @FXML
    private TextField locationField;
    @FXML
    private ContextMenu suggestionsMenu;




    @FXML
    public void initialize(){
        locationManager = new LocationManager();
        locationManager.setLocationField(locationField);
        locationManager.setSuggestionsMenu(suggestionsMenu);
        locationManager.initialize();
    }


// Storing data on submit
    public void onProfileSubmit() throws IOException {
        if(profileSubmitBtn.getText()=="Update Profile") {
            updateProfile();
        }
        else if (addDonorRecord()) {
            updateProfileCompletionStatus();
            TransitionManager.GoToHomePage((Stage)profileSubmitBtn.getScene().getWindow());
        }

    }

    private boolean updateProfileCompletionStatus(){
        // Update the profileCompletion status of user
        String updateProfileQuery = "UPDATE users SET profileCompleted = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateProfileQuery)) {

            // Setting parameters for the SQL query
            stmt.setInt(1,1);
            stmt.setInt(2,User.id);

            int rowsAffected = stmt.executeUpdate();
            User.setProfileCompleted(true);
            return rowsAffected > 0;  // Return true if submission is successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean addDonorRecord(){
        Donor donor=new Donor(fullNameField.getText(), (String) bloodTypeCombo.getValue(),Integer.parseInt(ageField.getText())
                , (String) genderCombo.getValue(),phoneField.getText(),locationField.getText(),lastDonationDate.getValue(),
                Double.parseDouble(locationManager.getLat()),Double.parseDouble(locationManager.getLon()));

        String insertQuery = "INSERT INTO Donors (full_name,blood_type,age,gender,contact_number,address,last_donation_date,latitude,longitude,id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            // Setting parameters for the SQL query
            stmt.setString(1, donor.getFullName());
            stmt.setString(2, donor.getBloodType());
            stmt.setInt(3, donor.getAge());
            stmt.setString(4,donor.getGender());
            stmt.setString(5,donor.getContactNumber());
            stmt.setString(6,donor.getAddress());
            stmt.setDate(7, Date.valueOf(donor.getLastDonationDate()));
            stmt.setDouble(8,donor.getLat());
            stmt.setDouble(9,donor.getLon());
            stmt.setInt(10,User.id);


            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // Return true if submission is successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile() {
        // Validate inputs
        if (!validateInputs()) {
            return false;
        }

        // Update donor information
        String updateQuery = """
                 UPDATE donors\s
                 SET full_name = ?, blood_type = ?, age = ?, gender = ?,\s
                     contact_number = ?, address = ?, last_donation_date = ?,
                     updated_at = CURRENT_TIMESTAMP
                 WHERE id = ?
                \s""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, fullNameField.getText());
            pstmt.setString(2, bloodTypeCombo.getValue());
            pstmt.setInt(3, Integer.parseInt(ageField.getText()));
            pstmt.setString(4, genderCombo.getValue());
            pstmt.setString(5, phoneField.getText());
            pstmt.setString(6, locationField.getText());
            pstmt.setDate(7, lastDonationDate.getValue() != null ?
                    java.sql.Date.valueOf(lastDonationDate.getValue()) : null);
            pstmt.setInt(8, User.getId());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Profile updated successfully!");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to update profile. Please try again.");

        }
        return false;
    }

    private boolean validateInputs() {
        // Add your validation logic here
        if (fullNameField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                locationField.getText().isEmpty() || bloodTypeCombo.getValue() == null ||
                genderCombo.getValue() == null || ageField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fill in all required fields.");
            return false;
        }

        try {
            int age = Integer.parseInt(ageField.getText());
            if (age < 18 || age > 65) {
                showAlert(Alert.AlertType.ERROR, "Validation Error",
                        "Age must be between 18 and 65.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please enter a valid age.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void onSkip(ActionEvent actionEvent) {
    }

    public TextField getFullNameField() {
        return fullNameField;
    }

    public void setFullNameField(TextField fullNameField) {
        this.fullNameField = fullNameField;
    }

    public TextField getPhoneField() {
        return phoneField;
    }

    public void setPhoneField(TextField phoneField) {
        this.phoneField = phoneField;
    }

    public TextField getAgeField() {
        return ageField;
    }

    public void setAgeField(TextField ageField) {
        this.ageField = ageField;
    }

    public ComboBox<String> getBloodTypeCombo() {
        return bloodTypeCombo;
    }

    public void setBloodTypeCombo(ComboBox<String> bloodTypeCombo) {
        this.bloodTypeCombo = bloodTypeCombo;
    }

    public DatePicker getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(DatePicker lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public ComboBox<String> getGenderCombo() {
        return genderCombo;
    }

    public void setGenderCombo(ComboBox<String> genderCombo) {
        this.genderCombo = genderCombo;
    }

    public TextField getLocationField() {
        return locationField;
    }

    public void setLocationField(TextField locationField) {
        this.locationField = locationField;
    }

    public Button getProfileSubmitBtn() {
        return profileSubmitBtn;
    }

    public void setProfileSubmitBtn(Button profileSubmitBtn) {
        this.profileSubmitBtn = profileSubmitBtn;
    }
}
