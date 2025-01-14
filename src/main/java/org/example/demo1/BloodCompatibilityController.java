package org.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import java.util.Map;
import java.util.HashMap;

public class BloodCompatibilityController {
    @FXML
    private ComboBox<String> bloodTypeCombo;

    @FXML
    private Label canDonateLabel;

    @FXML
    private Label canReceiveLabel;

    private Map<String, String[]> donateToMap = new HashMap<>();
    private Map<String, String[]> receiveFromMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialize blood type options
        bloodTypeCombo.setItems(FXCollections.observableArrayList(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));

        // Initialize compatibility maps
        setupCompatibilityMaps();
    }

    private void setupCompatibilityMaps() {
        // Who can receive from each blood type
        donateToMap.put("A+", new String[]{"A+", "AB+"});
        donateToMap.put("A-", new String[]{"A+", "A-", "AB+", "AB-"});
        donateToMap.put("B+", new String[]{"B+", "AB+"});
        donateToMap.put("B-", new String[]{"B+", "B-", "AB+", "AB-"});
        donateToMap.put("AB+", new String[]{"AB+"});
        donateToMap.put("AB-", new String[]{"AB+", "AB-"});
        donateToMap.put("O+", new String[]{"O+", "A+", "B+", "AB+"});
        donateToMap.put("O-", new String[]{"Everyone"}); // Universal donor

        // Who can donate to each blood type
        receiveFromMap.put("A+", new String[]{"A+", "A-", "O+", "O-"});
        receiveFromMap.put("A-", new String[]{"A-", "O-"});
        receiveFromMap.put("B+", new String[]{"B+", "B-", "O+", "O-"});
        receiveFromMap.put("B-", new String[]{"B-", "O-"});
        receiveFromMap.put("AB+", new String[]{"Everyone"}); // Universal recipient
        receiveFromMap.put("AB-", new String[]{"A-", "B-", "AB-", "O-"});
        receiveFromMap.put("O+", new String[]{"O+", "O-"});
        receiveFromMap.put("O-", new String[]{"O-"});
    }

    @FXML
    private void checkCompatibility() {
        String selectedBloodType = bloodTypeCombo.getValue();
        if (selectedBloodType != null) {
            String[] canDonateTo = donateToMap.get(selectedBloodType);
            String[] canReceiveFrom = receiveFromMap.get(selectedBloodType);

            canDonateLabel.setText(String.join(", ", canDonateTo));
            canReceiveLabel.setText(String.join(", ", canReceiveFrom));
        }
    }


}