package com.example.demo.view;

import com.example.demo.databases.DBUserResults;
import com.example.demo.databases.TypingResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ProgressViewController {

    @FXML
    private TableView<TypingResult> resultsTable;

    @FXML
    private TableColumn<TypingResult, String> dateColumn;

    @FXML
    private TableColumn<TypingResult, String> durationColumn;

    @FXML
    private TableColumn<TypingResult, String> wpmColumn;

    @FXML
    private TableColumn<TypingResult, String> accuracyColumn;

    @FXML
    private Label emptyLabel;

    @FXML
    private void initialize() {
        configureTable();
        loadResults();
    }

    private void configureTable() {
        resultsTable.setPlaceholder(new Label("Результатов пока нет"));
        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFormattedCreatedAt()));
        durationColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFormattedDuration()));
        wpmColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getWpmRounded()));
        accuracyColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAccuracyPercent()));
    }

    private void loadResults() {
        List<TypingResult> results = DBUserResults.getRecentResults(50);
        resultsTable.getItems().setAll(results);
        boolean empty = results.isEmpty();
        emptyLabel.setVisible(empty);
        emptyLabel.setManaged(empty);
    }
}
