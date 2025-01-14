package org.example.demo1;
import java.net.http.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class RouteDistanceCalculator {
    private static final HttpClient client = HttpClient.newHttpClient();
    public static String city;

    public static double calculateRouteDistanceAndCity(double lat1, double lon1, double lat2, double lon2) {
        try {
            // Step 1: Get city name for second coordinates
            city = getCityName(lat2, lon2);
            if (city == null) {
                System.out.println("Failed to retrieve city name.");
                return -1;
            }

            // Step 2: Calculate route distance
            double distance = getRouteDistance(lat1, lon1, lat2, lon2);
            if (distance < 0) {
                System.out.println("Failed to calculate route distance.");
                return -1;
            }

            System.out.println("City Name: " + city);
            System.out.println("Route Distance: " + distance + " km");
            return distance;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static String getCityName(double lat, double lon) {
        try {
            String url = String.format(
                    "https://nominatim.openstreetmap.org/reverse?lat=%f&lon=%f&format=json&addressdetails=1&accept-language=en",
                    lat, lon);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "JavaFX App")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject address = jsonResponse.getAsJsonObject("address");
            if (address != null) {
                // Check for various location keys
                if (address.has("city")) {
                    return address.get("city").getAsString();
                } else if (address.has("town")) {
                    return address.get("town").getAsString();
                } else if (address.has("village")) {
                    return address.get("village").getAsString();
                } else if (address.has("hamlet")) {
                    return address.get("hamlet").getAsString();
                } else {
                    return "Unknown";
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving city name: " + e.getMessage());
        }
        return "Unknown";
    }

    private static double getRouteDistance(double lat1, double lon1, double lat2, double lon2) {
        try {
            String url = String.format(
                    "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                    lon1, lat1, lon2, lat2);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "JavaFX App")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray routes = jsonResponse.getAsJsonArray("routes");
            if (routes != null && routes.size() > 0) {
                JsonObject route = routes.get(0).getAsJsonObject();
                double distanceInMeters = route.has("distance") ? route.get("distance").getAsDouble() : -1;
                return distanceInMeters / 1000.0; // Convert to kilometers
            }
        } catch (Exception e) {
            System.out.println("Error retrieving route distance: " + e.getMessage());
        }
        return -1;
    }

    public static void main(String[] args) {
        // Test the function
        double lat1 = 31.5537996, lon1 = 74.2904653; // Lahore
        double lat2 = 31.5695979, lon2 = 74.3053332; // Nearby location
        double distance = calculateRouteDistanceAndCity(lat1, lon1, lat2, lon2);
        if (distance >= 0) {
            System.out.println("Distance: " + distance + " km");
        }
    }

}