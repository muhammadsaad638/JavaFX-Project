package org.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class AdminPortalController {
    @FXML
    private StackPane contentPane;

    @FXML
    private void handleDashboardView(ActionEvent event) {
    }

    @FXML
    private void handleUserManagementView(ActionEvent event) {

        loadView("userManagement.fxml");
    }

    @FXML
    private void handleCommunicationsView(ActionEvent event) {
        loadView("communications.fxml");
    }

    @FXML
    private void handleAnalyticsView(ActionEvent event) {
        loadView("analytics.fxml");
    }

    @FXML
    private void handleButtonHover(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: rgba(52, 152, 219, 0.1); -fx-text-fill: #3498DB;  -fx-text-fill: #2C3E50; -fx-font-weight: bold; -fx-padding: 10 15 10 15; -fx-background-radius: 20;");
    }

    @FXML
    private void handleButtonExit(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E50; -fx-font-weight: bold; -fx-padding: 10 15 10 15; -fx-background-radius: 20;");
    }

    private void loadView(String fxmlFile) {
        try {
            // Load UserManagement.fxml
            StackPane userManagementPane = FXMLLoader.load(getClass().getResource(fxmlFile));
            // Clear existing content and set the new one
            contentPane.getChildren().clear();
            contentPane.getChildren().add(userManagementPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
