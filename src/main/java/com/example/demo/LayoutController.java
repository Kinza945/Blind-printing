package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class LayoutController {

    @FXML
    private AnchorPane scenePane;

    @FXML
    public void initialize() {
        System.out.println("Инициализация LayoutController");
        loadScene("activity/main_activity.fxml");
    }

    public <T> T loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(resolveResource(fxmlPath));
            Node pane = loader.load();
            attachToScene(pane);

            Object controller = loader.getController();
            if (controller instanceof LayoutAware) {
                ((LayoutAware) controller).setLayoutController(this);
            }

            @SuppressWarnings("unchecked")
            T typedController = (T) controller;
            return typedController;
        } catch (IOException e) {
            System.err.println("Ошибка загрузки сцены: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    private URL resolveResource(String fxmlPath) {
        URL resource = getClass().getResource("/com/example/demo/" + fxmlPath);
        if (resource == null) {
            throw new IllegalArgumentException("Ресурс не найден: " + fxmlPath);
        }
        return resource;
    }

    private void attachToScene(Node pane) {
        scenePane.getChildren().setAll(pane);
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
    }
}
