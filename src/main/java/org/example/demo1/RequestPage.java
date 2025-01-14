package org.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class RequestPage {

    public Button chatBtn;
    public Button backBtn;

    @FXML
    private void onChat(String chatWith) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatWindow.fxml"));
            Parent chatRoot = loader.load();

            ChatController controller= loader.getController();
            controller.initializeChat(chatWith);

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat with " + chatWith);
            chatStage.setScene(new Scene(chatRoot));
            chatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openChat(ActionEvent actionEvent) {
        onChat("test");

    }

    public void backToMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
