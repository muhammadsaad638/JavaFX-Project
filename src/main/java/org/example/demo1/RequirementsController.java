package org.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RequirementsController {

    @FXML
    private TextField ageField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField hemoglobinField;

    @FXML
    private CheckBox healthCheck;

    @FXML
    private Label resultLabel;

    @FXML
    private void checkEligibility() {
        try {
            int age = Integer.parseInt(ageField.getText());
            double weight = Double.parseDouble(weightField.getText());
            double hemoglobin = Double.parseDouble(hemoglobinField.getText());
            boolean isHealthy = healthCheck.isSelected();

            if (age < 18 || age > 65) {
                resultLabel.setText("You must be between 18 and 65 years old to donate blood.");
            } else if (weight < 50) {
                resultLabel.setText("You must weigh at least 50 kg to donate blood.");
            } else if (hemoglobin < 12.5) {
                resultLabel.setText("Your hemoglobin level must be at least 12.5 g/dL.");
            } else if (!isHealthy) {
                resultLabel.setText("You must be in good health to donate blood.");
            } else {
                resultLabel.setText("You are eligible to donate blood! Thank you for considering donation.");
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Please fill out all fields correctly.");
        }
    }
}
