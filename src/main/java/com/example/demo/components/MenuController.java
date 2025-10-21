package com.example.demo.components;

import com.example.demo.activity.MainActivityController;
import com.example.demo.view.HomeViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.Arrays;
import java.util.List;

public class MenuController {
    @FXML private Button home;
    @FXML private Button progress;
    @FXML private Button info;
    @FXML private Button award;
    @FXML private Button settings;

    private MainActivityController activityController;
    private List<Button> buttonList;

    public void setActivityController(MainActivityController activityController) {
        this.activityController = activityController;
    }

    @FXML
    private void initialize() {
        System.out.println("Инициализация MenuController");

        buttonList = Arrays.asList(home, progress, info, award, settings);

        home.setOnAction(e -> openHome());
        progress.setOnAction(e -> openContent("view/progress_view.fxml", progress));
        info.setOnAction(e -> openContent("view/info_view.fxml", info));
        award.setOnAction(e -> openContent("view/award_view.fxml", award));
        settings.setOnAction(e -> openContent("view/settings_view.fxml", settings));
    }

    public void showHome() {
        openHome();
    }

    private void openHome() {
        if (activityController != null) {
            activityController.loadContentWithController("view/home_view.fxml", HomeViewController.class);
            setActiveButton(home);
        }
    }

    private void openContent(String fxmlPath, Button button) {
        if (activityController != null) {
            activityController.loadContent(fxmlPath);
            setActiveButton(button);
        }
    }

    private void setActiveButton(Button activeBtn) {
        for (Button btn : buttonList) {
            btn.getStyleClass().remove("active-button-menu");
        }
        activeBtn.getStyleClass().add("active-button-menu");
        System.out.println("Нажата кнопка меню: " + activeBtn.getId());
    }
}
