package com.spoilerblocker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class SpoilerBlockerApp extends Application {
    private static final String BEARER_TOKEN = System.getenv("TWITTER_BEARER_TOKEN");
    private static final OkHttpClient client = new OkHttpClient();
    private TextArea tweetDisplay;
    private SpoilerBlocker spoilerBlocker;
    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("📵 Spoiler Blocker");

        Label headerLabel = new Label("Spoiler Blocker – Twitter Filter");
        headerLabel.setFont(new Font("Arial", 20));

        Label descriptionLabel = new Label("Click the button below to fetch and block potential spoilers from recent tweets.");
        descriptionLabel.setWrapText(true);

        tweetDisplay = new TextArea();
        tweetDisplay.setEditable(false);
        tweetDisplay.setWrapText(true);
        tweetDisplay.setPrefHeight(400);

        ScrollPane scrollPane = new ScrollPane(tweetDisplay);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Button fetchButton = new Button("🔄 Fetch Tweets");
        fetchButton.setOnAction(e -> {
            statusLabel.setText("⏳ Fetching tweets...");
            fetchTweets();
        });

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: darkgreen;");

        VBox layout = new VBox(10, headerLabel, descriptionLabel, fetchButton, scrollPane, statusLabel);
        layout.setPadding(new Insets(15));
        Scene scene = new Scene(layout, 600, 550);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            String personModelPath = "C:\\Users\\dks31\\OneDrive\\Desktop\\Coding\\Java\\Maven\\spoiler-blocker\\src\\main\\resources\\Models\\en-ner-person.bin";
            String locationModelPath = "C:\\Users\\dks31\\OneDrive\\Desktop\\Coding\\Java\\Maven\\spoiler-blocker\\src\\main\\resources\\Models\\en-ner-location.bin";
            String dateModelPath = "C:\\Users\\dks31\\OneDrive\\Desktop\\Coding\\Java\\Maven\\spoiler-blocker\\src\\main\\resources\\Models\\en-ner-date.bin";
            spoilerBlocker = new SpoilerBlocker(personModelPath, locationModelPath, dateModelPath);
        } catch (IOException e) {
            statusLabel.setText("❌ Error loading NLP models: " + e.getMessage());
        }
    }

    private void fetchTweets() {
        if (BEARER_TOKEN == null || BEARER_TOKEN.isEmpty()) {
            Platform.runLater(() -> statusLabel.setText("❌ Twitter API Bearer Token is missing or invalid."));
            return;
        }

        String apiUrl = "https://api.twitter.com/2/tweets/search/recent?query=spoiler%20OR%20ending%20OR%20finale%20OR%20dies%20OR%20death%20OR%20killed%20OR%20scene%20OR%20twist%20OR%20episode%20OR%20GameOfThrones%20OR%20HouseOfTheDragon&max_results=10";

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + BEARER_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> statusLabel.setText("❌ API Request Failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Platform.runLater(() -> statusLabel.setText("❌ API Error: " + response.code() + " - " + response.message()));
                    return;
                }

                JSONObject jsonResponse = new JSONObject(response.body().string());
                JSONArray tweets = jsonResponse.optJSONArray("data");
                StringBuilder tweetText = new StringBuilder("✅ Filtered Tweets:\n\n");

                if (tweets != null) {
                    for (int i = 0; i < tweets.length(); i++) {
                        String tweet = tweets.getJSONObject(i).getString("text");
                        String maskedTweet = spoilerBlocker.maskSpoilers(tweet);
                        tweetText.append("• ").append(maskedTweet).append("\n\n");
                    }
                } else {
                    tweetText.append("No tweets found.");
                }

                Platform.runLater(() -> {
                    tweetDisplay.setText(tweetText.toString());
                    statusLabel.setText("✅ Tweets fetched successfully.");
                });
            }
        });
    }
}
