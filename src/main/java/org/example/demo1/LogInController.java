package org.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.mysql.cj.conf.PropertyKey.logger;

public class LogInController {
    @FXML
    public Button loginButton;
    @FXML
    public TextField usernameField;
    @FXML
    public TextField passwordField;

    //private boolean registrationCompletion=false;
    @FXML
    public void OnLogInPress(ActionEvent actionEvent) throws IOException {

        AuthService authService = new AuthService();
        String username = usernameField.getText();
        boolean isSuccess = authService.authenticateUser(username, passwordField.getText());
        if (isSuccess) {
            System.out.println("Authentication passed");
            User.userName = username;
            if (!User.checkProfileCompletionStatus(username)) {
                TransitionManager.switchScreen((Stage) loginButton.getScene().getWindow(), "bloodDonationProfile", "donationProfile");
            } else {
                TransitionManager.switchScreen((Stage) loginButton.getScene().getWindow(), "homePage", "home");
            }
        } else {
            System.out.println("Authentication failed");
        }
    }

    public void BackToMainPage() throws IOException {
        TransitionManager.switchScreen((Stage) loginButton.getScene().getWindow(), "hello-view", "style");
    }

}