package org.example.demo1;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloController {

    @FXML
    public Button LogInButton;
    @FXML
    private Button RegisterButton;

    @FXML
    private AnchorPane formContainer;

    @FXML
    public void openRegistrationForm(ActionEvent event) {
        try {
// Create an FXMLLoader instance
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registrationForm.fxml"));
            // Load the FXML file
            Parent registrationFormFxml = loader.load();
            // Get the controller instance from the loader
            RegistrationController registrationController = loader.getController();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Create a new scene with the registration form
            Scene scene = new Scene(registrationFormFxml);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/registration.css")).toExternalForm());

            // Set the new scene on the same stage
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void OnPressSignIn(ActionEvent actionEvent) throws IOException {
        TransitionManager.switchScreen((Stage) ((Node) actionEvent.getSource()).getScene().getWindow(), "logIn", "login");
    }
}

