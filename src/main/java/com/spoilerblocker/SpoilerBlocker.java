package com.spoilerblocker;

import opennlp.tools.util.Span;
import java.io.IOException;

public class SpoilerBlocker {
    private final TextPreprocessor textPreprocessor;

    public SpoilerBlocker(String personModelPath, String locationModelPath, String dateModelPath) throws IOException {
        textPreprocessor = new TextPreprocessor(personModelPath, locationModelPath, dateModelPath);
    }

    /**
     * Masks spoilers in a given tweet by replacing detected names, locations, and dates with "****".
     */
    public String maskSpoilers(String tweet) {
        // Preprocess tweet (cleaning, tokenizing)
        String cleanedTweet = textPreprocessor.cleanTweet(tweet);
        String[] tokens = textPreprocessor.tokenizeText(cleanedTweet);

        // Find named entities and mask them
        maskEntities(tokens, textPreprocessor.findNamedEntities(tokens, textPreprocessor.getPersonFinder()));
        maskEntities(tokens, textPreprocessor.findNamedEntities(tokens, textPreprocessor.getLocationFinder()));
        maskEntities(tokens, textPreprocessor.findNamedEntities(tokens, textPreprocessor.getDateFinder()));

        // Rebuild and return the masked tweet
        return String.join(" ", tokens);
    }

    /**
     * Replaces detected named entities in tokens with "****".
     */
    private void maskEntities(String[] tokens, Span[] entities) {
        for (Span entity : entities) {
            for (int i = entity.getStart(); i < entity.getEnd(); i++) {
                tokens[i] = "****";  // Replace detected entity with asterisks
            }
        }
    }
}
