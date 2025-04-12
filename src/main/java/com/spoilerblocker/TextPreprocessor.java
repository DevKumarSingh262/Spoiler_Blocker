package com.spoilerblocker;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextPreprocessor {
    private NameFinderME personFinder;
    private NameFinderME locationFinder;
    private NameFinderME dateFinder;
    private final SimpleTokenizer tokenizer;

    public TextPreprocessor(String personModelPath, String locationModelPath, String dateModelPath) throws IOException {
        tokenizer = SimpleTokenizer.INSTANCE;
        loadNERModels(personModelPath, locationModelPath, dateModelPath);
    }

    private void loadNERModels(String personModelPath, String locationModelPath, String dateModelPath) throws IOException {
        personFinder = loadModel(personModelPath);
        locationFinder = loadModel(locationModelPath);
        dateFinder = loadModel(dateModelPath);
    }

    private NameFinderME loadModel(String modelPath) throws IOException {
        if (Files.exists(Paths.get(modelPath))) {
            return new NameFinderME(new TokenNameFinderModel(Files.newInputStream(Paths.get(modelPath))));
        } else {
            System.err.println("Model file not found: " + modelPath);
            return null;
        }
    }

    public String[] tokenizeText(String text) {
        return tokenizer.tokenize(text);
    }

    public Span[] findNamedEntities(String[] tokens, NameFinderME finder) {
        return (finder != null) ? finder.find(tokens) : new Span[0];
    }

    public NameFinderME getPersonFinder() {
        return personFinder;
    }

    public NameFinderME getLocationFinder() {
        return locationFinder;
    }

    public NameFinderME getDateFinder() {
        return dateFinder;
    }

    public String cleanTweet(String tweet) {
        return tweet.replaceAll("http\\S+|www\\S+", "")  // Remove URLs
                .replaceAll("@\\w+", "")  // Remove mentions
                .replaceAll("#\\w+", "")  // Remove hashtags
                .replaceAll("[^a-zA-Z0-9 ]", " ")  // Remove special characters
                .trim();
    }
}
