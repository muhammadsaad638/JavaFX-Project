package org.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class UserManagementController {

    @FXML
    private CheckBox selectAllCheckbox;

    @FXML
    private VBox listContainer; // The VBox containing all student items

    @FXML
    private Button deleteSelectedButton;

    private List<CheckBox> individualCheckboxes = new ArrayList<>();

    @FXML
    public void initialize() {
        // Find all individual checkboxes and add them to our list
        findCheckboxes(listContainer);

        // Set up the select all checkbox handler
        selectAllCheckbox.setOnAction(event -> handleSelectAll());

        // Set up individual checkbox handlers
        individualCheckboxes.forEach(checkbox ->
                checkbox.setOnAction(event -> handleIndividualSelection()));

        // Set up delete button handler
        deleteSelectedButton.setOnAction(event -> handleDelete());
    }

    private void findCheckboxes(Parent parent) {
        // Recursively find all checkboxes except the "Select All" checkbox
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof CheckBox && node != selectAllCheckbox) {
                individualCheckboxes.add((CheckBox) node);
            }
            if (node instanceof Parent) {
                findCheckboxes((Parent) node);
            }
        }
    }

    private void handleSelectAll() {
        boolean selectAll = selectAllCheckbox.isSelected();
        individualCheckboxes.forEach(checkbox -> checkbox.setSelected(selectAll));
    }

    private void handleIndividualSelection() {
        // Update "Select All" checkbox based on individual selections
        boolean allSelected = individualCheckboxes.stream()
                .allMatch(CheckBox::isSelected);
        boolean noneSelected = individualCheckboxes.stream()
                .noneMatch(CheckBox::isSelected);

        // Set the state of select all checkbox
        selectAllCheckbox.setSelected(allSelected);
        selectAllCheckbox.setIndeterminate(!allSelected && !noneSelected);
    }

    private void handleDelete() {
        // Create a list of VBox items to remove
        List<Node> toRemove = new ArrayList<>();

        // Find parent VBox of each selected checkbox
        individualCheckboxes.forEach(checkbox -> {
            if (checkbox.isSelected()) {
                // Navigate up the scene graph to find the parent VBox of the list item
                Node parent = checkbox.getParent();
                while (parent != null && !(parent.getParent() == listContainer)) {
                    parent = parent.getParent();
                }
                if (parent != null) {
                    toRemove.add(parent);
                }
            }
        });

        // Remove the selected items
        listContainer.getChildren().removeAll(toRemove);

        // Update our list of checkboxes
        individualCheckboxes.clear();
        findCheckboxes(listContainer);

        // Reset select all checkbox
        handleIndividualSelection();
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        // Get the button that was clicked
        Button updateButton = (Button) event.getSource();

        // Find the parent VBox of this item
        Node parent = updateButton.getParent();
        while (parent != null && !(parent.getParent() == listContainer)) {
            parent = parent.getParent();
        }

        if (parent != null) {
            VBox itemContainer = (VBox) parent;
            // Here you can add your update logic
            // For example, you could open a new dialog or navigate to an update view
            System.out.println("Update clicked for item: " +
                    ((Label)((VBox)((HBox)itemContainer.getChildren().get(0))
                            .getChildren().get(1)).getChildren().get(0)).getText());
        }
    }
}