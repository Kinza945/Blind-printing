package com.example.demo.activity;

import com.example.demo.LayoutAware;
import com.example.demo.LayoutController;
import com.example.demo.databases.DBUserResults;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlindPrintingController implements LayoutAware {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button backButton;

    @FXML
    private Button startButton;

    @FXML
    private Button resetButton;

    @FXML
    private TextArea targetTextArea;

    @FXML
    private TextArea inputArea;

    @FXML
    private Label timerLabel;

    @FXML
    private Label wpmLabel;

    @FXML
    private Label accuracyLabel;

    @FXML
    private Label statusLabel;

    private LayoutController layoutController;
    private final Random random = new Random();
    private final List<String> lessonTexts = Arrays.asList(
            "Каждый охотник желает знать где сидит фазан",
            "Слепая печать развивается с практикой и вниманием к осанке",
            "Быстрый набор текста требует точности и расслабленных плеч",
            "Тренируйтесь печатать без подсказок чтобы улучшить скорость",
            "Регулярные упражнения помогают мозгу запоминать расположение клавиш"
    );

    private String activeLesson;
    private Timeline timeline;
    private long startNanoTime;
    private double completedSeconds;
    private boolean running;

    @Override
    public void setLayoutController(LayoutController layoutController) {
        this.layoutController = layoutController;
    }

    @FXML
    private void initialize() {
        inputArea.setDisable(true);
        inputArea.textProperty().addListener((obs, oldText, newText) -> {
            if (!running) {
                return;
            }

            updateMetrics();
            if (activeLesson != null && newText.length() >= activeLesson.length()) {
                finishTest();
            }
        });

        inputArea.setDisable(true);
        inputArea.textProperty().addListener((obs, oldText, newText) -> {
            if (running) {
                updateMetrics();
                if (activeLesson != null && newText.length() >= activeLesson.length()) {
                    finishTest();
                }
            }
        });

        if (backButton != null) {
            backButton.setOnAction(e -> goBackToHome());
        }

        startButton.setOnAction(e -> {
            if (running) {
                finishTest();
            } else {
                startTest();
            }
        });

        resetButton.setOnAction(e -> resetTest());
        resetTest();
    }

    private void startTest() {
        activeLesson = lessonTexts.get(random.nextInt(lessonTexts.size()));
        targetTextArea.setText(activeLesson);
        inputArea.clear();
        inputArea.setDisable(false);
        inputArea.requestFocus();

        startNanoTime = System.nanoTime();
        completedSeconds = 0.0;
        running = true;
        statusLabel.setText("Печатаем... Старайтесь не смотреть на клавиатуру!");
        startButton.setText("Завершить");

        startMetricsTimeline();
        updateMetrics();
    }

    private void finishTest() {
        if (!running) {
            return;
        }

        running = false;
        completedSeconds = calculateElapsedSeconds();
        stopMetricsTimeline();
        updateMetrics();

        inputArea.setDisable(true);
        startButton.setText("Начать");

        double elapsedSeconds = completedSeconds;
        double wpm = parseLabelValue(wpmLabel.getText());
        double accuracy = parsePercentageValue(accuracyLabel.getText());

        DBUserResults.insertResult(
                activeLesson != null ? activeLesson : "",
                inputArea.getText(),
                elapsedSeconds,
                wpm,
                accuracy
        );

        String summary = String.format("Сеанс завершен: %.1f WPM, точность %.1f%%.", wpm, accuracy * 100);
        if (activeLesson != null && activeLesson.equals(inputArea.getText())) {
            statusLabel.setText(summary + " Отлично! Текст напечатан без ошибок.");
        } else {
            statusLabel.setText(summary + " Проверьте статистику и попробуйте снова.");
        }
    }

    private void resetTest() {
        running = false;
        activeLesson = null;
        stopMetricsTimeline();

        startNanoTime = 0L;
        completedSeconds = 0.0;
        targetTextArea.clear();
        inputArea.clear();
        inputArea.setDisable(true);
        timerLabel.setText("00:00");
        wpmLabel.setText(String.format("%.1f WPM", 0.0));
        accuracyLabel.setText(String.format("%.1f%% точность", 0.0));
        startButton.setText("Начать");
        statusLabel.setText("Нажмите \"Начать\", чтобы приступить к тренировке слепой печати.");
    }

    private void goBackToHome() {
        if (layoutController != null) {
            layoutController.loadScene("activity/main_activity.fxml");
        }
    }

    public void loadAnotherView(String fxmlPath) {
        if (layoutController != null) {
            layoutController.loadScene(fxmlPath);
        }
    }

    private void startMetricsTimeline() {
        stopMetricsTimeline();
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> updateMetrics()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopMetricsTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    private void updateMetrics() {
        double elapsedSeconds = calculateElapsedSeconds();
        timerLabel.setText(formatElapsed(elapsedSeconds));

        String typed = inputArea.getText();
        double minutes = elapsedSeconds / 60.0;
        double wpm = (!typed.isEmpty() && minutes > 0) ? (typed.length() / 5.0) / minutes : 0.0;
        wpmLabel.setText(String.format("%.1f WPM", wpm));

        if (activeLesson != null && !typed.isEmpty()) {
            int matched = 0;
            int maxLength = Math.min(typed.length(), activeLesson.length());
            for (int i = 0; i < maxLength; i++) {
                if (typed.charAt(i) == activeLesson.charAt(i)) {
                    matched++;
                }
            }
            int typedLength = typed.length();
            double accuracy = typedLength > 0 ? (double) matched / typedLength : 0.0;
            accuracyLabel.setText(String.format("%.1f%% точность", accuracy * 100));
        } else {
            accuracyLabel.setText(String.format("%.1f%% точность", 0.0));
        }
    }

    private double calculateElapsedSeconds() {
        if (running) {
            return (System.nanoTime() - startNanoTime) / 1_000_000_000.0;
        }
        return completedSeconds;
    }

    private String formatElapsed(double elapsedSeconds) {
        long totalSeconds = Math.max(0, (long) elapsedSeconds);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private double parseLabelValue(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }
        String numeric = text.replaceAll("[^0-9.,]", "").replace(',', '.');
        if (numeric.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(numeric);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private double parsePercentageValue(String text) {
        return parseLabelValue(text) / 100.0;
    }
}
