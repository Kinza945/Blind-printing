package com.example.demo.activity;

import com.example.demo.LayoutAware;
import com.example.demo.LayoutController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class BlindPrintingController implements LayoutAware {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button backButton;

    private LayoutController layoutController;

    @Override
    public void setLayoutController(LayoutController layoutController) {
        this.layoutController = layoutController;
    }

    @FXML
    private void initialize() {
        System.out.println("Инициализация BlindPrintingController");

        if (backButton != null) {
            backButton.setOnAction(e -> goBackToHome());
        }
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
}
