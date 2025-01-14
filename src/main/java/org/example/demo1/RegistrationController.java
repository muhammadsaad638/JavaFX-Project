package org.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RegistrationController {

    private boolean validation;// To validate all the credentials are correct
    @FXML
    public GridPane formContainer;
    @FXML
    private Button backButton;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private Label userNameValidationLabel;
    @FXML
    private Label emailValidationLabel;
    @FXML
    private Label invalidCredentialsLabel;


    @FXML
    public void backToMainScreen() throws IOException {
        TransitionManager.backToMainScreenLogic((Stage) backButton.getScene().getWindow());
    }

    public void validateCredentials() {
        initialize();
    }

    @FXML
    public void initialize() {

        // Add a listener to validate input in real-time
        userNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateUsername(newValue);
        });
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEmail(newValue);
        });
    }
    private void validateUsername(String username) {
        // Regular expression to allow only alphabets and numbers
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            validation =false;
            // Display error if invalid input
            userNameValidationLabel.setText("Only letters and numbers allowed!");
            userNameField.setStyle("-fx-border-color: red;");
        } else {
            validation=true;
            // Clear the error message and reset the border color if valid
            userNameValidationLabel.setText("");
            userNameField.setStyle("-fx-border-color: grey;");
        }
    }

    private void validateEmail(String email){
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            validation=false;
            // Display error if the email is invalid
            emailValidationLabel.setText("Invalid email format!");
            emailField.setStyle("-fx-border-color: red;");
        } else {
            validation=true;
            // Clear the error message and reset the border color if valid
            emailValidationLabel.setText("");
            emailField.setStyle("-fx-border-color: grey;");
        }

    }

    public void register() throws IOException {
        if(validation){
            invalidCredentialsLabel.setText("");
            User user = new User(userNameField.getText(), emailField.getText(), passwordField.getText());
            AuthService authService= new AuthService();
            boolean hasRegistered=authService.registerUser(userNameField.getText(), user.getEmail(), user.getPassword());
            if(hasRegistered){
                System.out.println("User has been registered");
                TransitionManager.goToLoginPage((Stage) backButton.getScene().getWindow());
            }

        }
        else{
            invalidCredentialsLabel.setText("Please make sure to add valid credentials");
        }
    }





}