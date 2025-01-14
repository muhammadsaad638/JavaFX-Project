package org.example.demo1;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TransitionManager {


    public static void backToMainScreenLogic(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TransitionManager.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(TransitionManager.class.getResource("/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void goToLoginPage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TransitionManager.class.getResource("logIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(TransitionManager.class.getResource("/login.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void GoToBloodDonationProfile(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TransitionManager.class.getResource("bloodDonationProfile.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(TransitionManager.class.getResource("/donationProfile.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void GoToHomePage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TransitionManager.class.getResource("homePage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(TransitionManager.class.getResource("/home.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

   public static void GoToRequestPage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TransitionManager.class.getResource("requestPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(TransitionManager.class.getResource("/request.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Switches to a different screen while maintaining responsiveness
     * @param stage The current stage
     * @param fxmlName The name of the FXML file without extension (e.g., "hello-view", "logIn")
     * @param cssName The name of the CSS file without extension and forward slash (e.g., "style", "login")
     * @throws IOException If the FXML or CSS file cannot be loaded
     */
    public static void switchScreen(Stage stage, String fxmlName, String cssName) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(TransitionManager.class.getResource(fxmlName + ".fxml"));
        Parent root = fxmlLoader.load();

        // Create new scene
        Scene scene = new Scene(root);

        // Apply CSS
        String cssPath = "/" + cssName + ".css";
        scene.getStylesheets().add(
                Objects.requireNonNull(TransitionManager.class.getResource(cssPath)).toExternalForm()
        );

        // Set new scene
        stage.setScene(scene);
        stage.show();
    }


}
