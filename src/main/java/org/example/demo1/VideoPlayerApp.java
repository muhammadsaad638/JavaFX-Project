package org.example.demo1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class VideoPlayerApp {

    public void playVideo(String videoPath, Stage primaryStage) {
        // Create a Media object using the video path
        Media media = new Media(new File(videoPath).toURI().toString());

        // Create a MediaPlayer to control the playback
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Create a MediaView to display the video
        MediaView mediaView = new MediaView(mediaPlayer);

        // Load play and pause icons as Image objects
        Image playIcon = new Image("/play.png");
        Image pauseIcon = new Image("/pause.png");

        // Create ImageView to display the icon in the button
        ImageView playPauseImageView = new ImageView(playIcon);
        playPauseImageView.setFitWidth(40);
        playPauseImageView.setFitHeight(40);

        // Play/Pause button with the image icon
        Button playPauseButton = new Button();
        playPauseButton.setGraphic(playPauseImageView);
        playPauseButton.setStyle("-fx-background-color: transparent;");

        // Prepare StackPane to overlay button on video
        StackPane videoContainer = new StackPane();

        // Bind MediaView to container size while maintaining aspect ratio
        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(videoContainer.widthProperty());
        mediaView.fitHeightProperty().bind(videoContainer.heightProperty());

        // Position button over video
        playPauseButton.setStyle("-fx-background-color: transparent;");
        StackPane.setAlignment(playPauseButton, Pos.CENTER);

        // Add MediaView and button to container
        videoContainer.getChildren().addAll(mediaView, playPauseButton);

        // Initially set to play
        playPauseButton.setOnAction(e -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseImageView.setImage(playIcon);
            } else {
                mediaPlayer.play();
                playPauseImageView.setImage(pauseIcon);
            }
        });

        // Create a scene that will resize with the window
        Scene scene = new Scene(videoContainer, 800, 450);

        // Adjust stage to fill screen while maintaining video aspect ratio
        primaryStage.setTitle("JavaFX Video Player");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Ensure the media is ready and update UI
        mediaPlayer.setOnReady(() -> {
            // Update button icon to pause when video starts
            playPauseImageView.setImage(pauseIcon);

            // Automatically adjust stage size based on video dimensions
            double videoWidth = media.getWidth();
            double videoHeight = media.getHeight();

            // Calculate aspect ratio
            double aspectRatio = videoWidth / videoHeight;

            // Set a reasonable initial size while maintaining aspect ratio
            double initialWidth = 800;
            double initialHeight = initialWidth / aspectRatio;

            primaryStage.setWidth(initialWidth);
            primaryStage.setHeight(initialHeight);

            // Start playing
            mediaPlayer.play();
        });

        // Handle any errors related to media loading
        mediaPlayer.setOnError(() -> {
            System.out.println("Error loading media: " + mediaPlayer.getError().getMessage());
        });
    }
}
