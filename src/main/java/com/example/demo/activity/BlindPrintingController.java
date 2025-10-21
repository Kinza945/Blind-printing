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
    private long startNanoTime;
    private Timeline timeline;
    private boolean running;

    @Override
    public void setLayoutController(LayoutController layoutController) {
        this.layoutController = layoutController;
    }

    @FXML
    private void initialize() {
        System.out.println("Инициализация BlindPrintingController");

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
        running = true;
        statusLabel.setText("Печатаем... Старайтесь не смотреть на клавиатуру!");
        startButton.setText("Завершить");

        if (timeline != null) {
            timeline.stop();
        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> updateMetrics()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        updateMetrics();
    }

    private void finishTest() {
        if (!running) {
            return;
        }

        running = false;
        if (timeline != null) {
            timeline.stop();
        }

        updateMetrics();
        inputArea.setDisable(true);
        startButton.setText("Начать");

        double elapsedSeconds = getElapsedSeconds();
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
        if (timeline != null) {
            timeline.stop();
        }

        startNanoTime = 0L;
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

    private void updateMetrics() {
        double elapsedSeconds = getElapsedSeconds();
        timerLabel.setText(formatElapsed(elapsedSeconds));

        String typed = inputArea.getText();
        int charactersTyped = typed.length();
        double minutes = elapsedSeconds / 60.0;
        double wpm = minutes > 0 ? (charactersTyped / 5.0) / minutes : 0.0;
        wpmLabel.setText(String.format("%.1f WPM", wpm));

        if (activeLesson != null && !activeLesson.isEmpty()) {
            int matched = 0;
            int maxLength = Math.min(typed.length(), activeLesson.length());
            for (int i = 0; i < maxLength; i++) {
                if (typed.charAt(i) == activeLesson.charAt(i)) {
                    matched++;
                }
            }
            int total = Math.max(activeLesson.length(), typed.length());
            double accuracy = total > 0 ? (double) matched / total : 0.0;
            accuracyLabel.setText(String.format("%.1f%% точность", accuracy * 100));
        } else {
            accuracyLabel.setText("0.0% точность");
        }
    }

    private double getElapsedSeconds() {
        if (!running) {
            return startNanoTime > 0 ? (System.nanoTime() - startNanoTime) / 1_000_000_000.0 : 0.0;
        }
        return (System.nanoTime() - startNanoTime) / 1_000_000_000.0;
    }

    private String formatElapsed(double elapsedSeconds) {
        long totalSeconds = (long) elapsedSeconds;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private double parseLabelValue(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }
        String numeric = text.replaceAll("[^0-9.,]", "").replace(',', '.');
        return numeric.isEmpty() ? 0.0 : Double.parseDouble(numeric);
    }

    private double parsePercentageValue(String text) {
        return parseLabelValue(text) / 100.0;
    }
}
