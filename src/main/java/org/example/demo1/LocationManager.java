package org.example.demo1;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;

import javafx.scene.control.MenuItem;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class LocationManager {
    private String lat;
    private String lon;


    private TextField locationField;


    private ContextMenu suggestionsMenu;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public TextField getLocationField() {
        return locationField;
    }

    public void setLocationField(TextField locationField) {
        this.locationField = locationField;
    }

    public ContextMenu getSuggestionsMenu() {
        return suggestionsMenu;
    }

    public void setSuggestionsMenu(ContextMenu suggestionsMenu) {
        this.suggestionsMenu = suggestionsMenu;
    }


    public void initialize() {
        // Set up event listener for user typing in the TextField
        locationField.setOnKeyReleased(event -> {
            String input = locationField.getText();
            if (!input.isEmpty()) {
                fetchSuggestions(input);
            }
        });
    }

    private final Map<String, String> cache = new HashMap<>();
    private final AtomicLong lastRequestTimestamp = new AtomicLong(0);
    private static final long REQUEST_THRESHOLD_MS = 1000;

    private void fetchSuggestions(String query) {
        if (query == null || query.trim().length() < 3) {
            return; // Ignore short or empty queries
        }

        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp - lastRequestTimestamp.get() < REQUEST_THRESHOLD_MS) {
            return; // Skip if within request threshold
        }

        lastRequestTimestamp.set(currentTimestamp); // Update timestamp

        if (cache.containsKey(query)) {
            updateSuggestionsMenu(cache.get(query));
            return; // Return cached result
        }

        scheduler.schedule(() -> {
            try {
                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
                String url = "https://nominatim.openstreetmap.org/search?q=" + encodedQuery
                        + "&format=json&limit=5&viewbox=74.1502,31.5193,74.3996,31.6295&bounded=1&accept-language=en";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "JavaFX App")
                        .build();

                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenAccept(response -> {
                            if (currentTimestamp == lastRequestTimestamp.get()) {
                                cache.put(query, response); // Cache response
                                updateSuggestionsMenu(response);
                            } else {
                                System.out.println("Ignored outdated response");
                            }
                        })
                        .exceptionally(e -> {
                            System.out.println("Error in API request: " + e.getMessage());
                            e.printStackTrace();
                            return null;
                        });
            } catch (Exception e) {
                System.out.println("Error encoding URL or sending request: " + e.getMessage());
                e.printStackTrace();
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }


    private void updateSuggestionsMenu(String jsonResponse) {
        Platform.runLater(() -> {
            suggestionsMenu.getItems().clear();

            JsonArray results = JsonParser.parseString(jsonResponse).getAsJsonArray();
            results.forEach(result -> {
                JsonObject locationObject = result.getAsJsonObject();
                // Now have the fields you need
                String displayName = locationObject.get("display_name").getAsString();
                String latitude = locationObject.get("lat").getAsString();
                String longitude = locationObject.get("lon").getAsString();

                // Create a MenuItem with the location's display name
                MenuItem suggestionItem = new MenuItem(displayName);

                // Store coordinates in the MenuItem's user data
                suggestionItem.setUserData(new String[]{latitude, longitude});

                // Set the action to update the TextField and handle coordinates
                suggestionItem.setOnAction(event -> {
                    // Update the TextField with the display name
                    locationField.setText(displayName);

                    // Retrieve and use the coordinates
                    String[] coordinates = (String[]) suggestionItem.getUserData();
                    this.lat = coordinates[0];
                    this.lon = coordinates[1];
                    if((lat==null || lon==null)){
                        lat="0";
                        lon="0";
                    }

                    System.out.println("Selected Coordinates: Latitude = " + lat + ", Longitude = " + lon);

                    // Here, you can add any additional functionality using the coordinates, such as
                    // passing them to another method for further processing
                });

                suggestionsMenu.getItems().add(suggestionItem);
            });

            // Show suggestions if there are any
            if (!suggestionsMenu.getItems().isEmpty()) {
                Bounds screenBounds = locationField.localToScreen(locationField.getBoundsInLocal());
                double x = screenBounds.getMinX();
                double y = screenBounds.getMaxY();
                suggestionsMenu.show(locationField, x, y);
            } else {
                suggestionsMenu.hide();
            }
        });
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}