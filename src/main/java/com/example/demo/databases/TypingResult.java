package com.example.demo.databases;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TypingResult {
    private final long id;
    private final String targetText;
    private final String typedText;
    private final double elapsedSeconds;
    private final double wordsPerMinute;
    private final double accuracy;
    private final LocalDateTime createdAt;

    public TypingResult(long id,
                        String targetText,
                        String typedText,
                        double elapsedSeconds,
                        double wordsPerMinute,
                        double accuracy,
                        LocalDateTime createdAt) {
        this.id = id;
        this.targetText = Objects.requireNonNull(targetText);
        this.typedText = Objects.requireNonNull(typedText);
        this.elapsedSeconds = elapsedSeconds;
        this.wordsPerMinute = wordsPerMinute;
        this.accuracy = accuracy;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public long getId() {
        return id;
    }

    public String getTargetText() {
        return targetText;
    }

    public String getTypedText() {
        return typedText;
    }

    public double getElapsedSeconds() {
        return elapsedSeconds;
    }

    public double getWordsPerMinute() {
        return wordsPerMinute;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getFormattedCreatedAt() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public String getFormattedDuration() {
        Duration duration = Duration.ofMillis(Math.round(elapsedSeconds * 1000));
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getAccuracyPercent() {
        return String.format("%.1f%%", accuracy * 100);
    }

    public String getWpmRounded() {
        return String.format("%.1f", wordsPerMinute);
    }
}
