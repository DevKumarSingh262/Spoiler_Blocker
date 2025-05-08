# Spoiler Blocker

Spoiler Blocker is a real-time Java-based application that fetches tweets from Twitter and masks potential spoilers using Natural Language Processing (NLP) techniques. The app detects spoilers like character names, places, and dates in tweets and replaces them with ‘****’ before the user can read them. This project is built using JavaFX for the GUI, Apache OpenNLP for NLP, and OkHttp for connecting to Twitter’s API.

## Table of Contents
- [Project Introduction](#project-introduction)
- [Problem Statement](#problem-statement)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [How It Works (Step-by-Step)](#how-it-works-step-by-step)
- [What Makes It Interesting](#what-makes-it-interesting)
- [Challenges Faced](#challenges-faced)
- [Possible Improvements](#possible-improvements)

## Project Introduction
Spoiler Blocker is a real-time Java-based application designed to protect users from spoilers on social media, particularly Twitter. The app uses Named Entity Recognition (NER) to identify potential spoilers (like character names, places, or dates) and replaces them with ‘****’, ensuring users have a spoiler-free experience. The app is powered by JavaFX for the GUI, Apache OpenNLP for NLP tasks, and OkHttp for connecting to the Twitter API.

## Problem Statement
Spoilers on social media can ruin the enjoyment of entertainment experiences, especially after major releases like episodes or movies. This project solves that issue by automatically detecting and masking spoilers in tweets before the user reads them.

## Tech Stack

| Component         | Technology Used         |
|-------------------|-------------------------|
| GUI               | JavaFX                  |
| NLP (NER)         | Apache OpenNLP          |
| HTTP Client       | OkHttp                  |
| Build Tool        | Maven                   |
| Twitter API       | Twitter v2 + Bearer Token|
| Language          | Java                    |

## Architecture Overview

The app is split into four main modules for a clean separation of concerns:

- **TextPreprocessor**: Cleans tweets and performs Named Entity Recognition (NER) using OpenNLP.
- **SpoilerBlocker**: Applies the logic for masking detected spoilers by replacing names, locations, and dates with ‘****’.
- **SpoilerBlockerApp/Main**: JavaFX interface that fetches tweets and displays the masked results.
- **pom.xml**: Maven configuration for managing dependencies like OpenNLP, OkHttp, and JavaFX.

## How It Works (Step-by-Step)

1. User clicks the **“Fetch Tweets”** button in the JavaFX app.
2. The app calls Twitter’s search endpoint via OkHttp using a Bearer Token.
3. Tweets are cleaned (removing URLs, hashtags, and mentions) using regex.
4. Tokenization and Named Entity Recognition (NER) are performed using OpenNLP to detect names, locations, and dates.
5. Detected spoiler-like entities are replaced with "****".
6. Cleaned tweets are displayed to the user in the GUI.

## What Makes It Interesting
This project is unique because it goes beyond simple keyword-based filtering by using **Named Entity Recognition (NER)** to identify contextually relevant spoiler content such as character names, locations, and dates (e.g., “Jon Snow” or “King’s Landing”). This makes it a smarter solution than traditional keyword filters.

## Challenges Faced

- Handling dynamic content from Twitter and managing rate limits.
- Ensuring that OpenNLP models load correctly across different platforms.
- Balancing between over-filtering (false positives) and missing spoilers.

## Possible Improvements

Some next steps and future enhancements for this project include:

- Replacing OpenNLP with a deep learning model like BERT via a Python microservice for improved context understanding.
- Enhancing the GUI with custom filters, themes, and export features.
- Adding unit tests and better error logging to improve maintainability and robustness.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Apache OpenNLP for Named Entity Recognition.
- OkHttp for making HTTP requests to the Twitter API.
- Twitter API v2 for providing the search endpoint.
