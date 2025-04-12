package com.spoilerblocker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {
    private static final String BEARER_TOKEN = System.getenv("TWITTER_BEARER_TOKEN");
    private static final OkHttpClient client = new OkHttpClient();
    private TextArea tweetDisplay;
    private SpoilerBlocker spoilerBlocker;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spoiler Blocker");

        tweetDisplay = new TextArea();
        tweetDisplay.setEditable(false);

        Button fetchButton = new Button("Fetch Tweets");
        fetchButton.setOnAction(e -> fetchTweets());

        VBox layout = new VBox(10, fetchButton, tweetDisplay);
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            String basePath = "src/main/resources/Models/";
            spoilerBlocker = new SpoilerBlocker(basePath + "en-ner-person.bin", basePath + "en-ner-location.bin", basePath + "en-ner-date.bin");
        } catch (IOException e) {
            tweetDisplay.setText("❌ Error loading NLP models: " + e.getMessage());
        }
    }

    private void fetchTweets() {
        if (BEARER_TOKEN == null) {
            tweetDisplay.setText("❌ Error: Twitter API Bearer Token is missing.");
            return;
        }

        Request request = new Request.Builder()
                .url("https://api.twitter.com/2/tweets/search/recent?query=spoiler OR ending OR finale OR leaked&max_results=10")
                .addHeader("Authorization", "Bearer " + BEARER_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                tweetDisplay.setText("❌ API Request Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    tweetDisplay.setText("❌ API Request Failed: " + response.code() + " - " + response.message());
                    return;
                }

                assert response.body() != null;
                JSONObject jsonResponse = new JSONObject(response.body().string());
                JSONArray tweets = jsonResponse.optJSONArray("data");
                StringBuilder tweetText = new StringBuilder("✅ Recent Tweets:\n");

                if (tweets != null) {
                    for (int i = 0; i < tweets.length(); i++) {
                        String tweet = tweets.getJSONObject(i).getString("text");
                        String maskedTweet = spoilerBlocker.maskSpoilers(tweet);
                        tweetText.append("Tweet: ").append(maskedTweet).append("\n-------------------------------------------------\n");
                    }
                } else {
                    tweetText.append("No tweets found.");
                }

                tweetDisplay.setText(tweetText.toString());
            }
        });
    }
}
