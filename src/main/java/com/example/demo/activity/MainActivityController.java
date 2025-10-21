package com.example.demo.activity;

import com.example.demo.LayoutAware;
import com.example.demo.LayoutController;
import com.example.demo.components.MenuController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class MainActivityController implements LayoutAware {

    @FXML
    private AnchorPane menuPane;

    @FXML
    private AnchorPane contentPane;

    private LayoutController layoutController;
    private MenuController menuController;

    @FXML
    private void initialize() {
        menuController = loadMenu("components/menu.fxml");
        if (menuController != null) {
            menuController.setActivityController(this);
        }
    }

    @Override
    public void setLayoutController(LayoutController layoutController) {
        this.layoutController = layoutController;
        if (menuController != null) {
            menuController.showHome();
        }
    }

    public void loadContent(String fxmlPath) {
        loadContentInternal(fxmlPath, null);
    }

    public <T> T loadContentWithController(String fxmlPath, Class<T> controllerClass) {
        return loadContentInternal(fxmlPath, controllerClass);
    }

    private <T> T loadContentInternal(String fxmlPath, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(resolveResource(fxmlPath));
            Node pane = loader.load();
            attachToContainer(contentPane, pane);

            Object controller = loader.getController();
            if (controller instanceof LayoutAware && layoutController != null) {
                ((LayoutAware) controller).setLayoutController(layoutController);
            }

            if (controllerClass != null && controllerClass.isInstance(controller)) {
                return controllerClass.cast(controller);
            }
            return null;
        } catch (IOException e) {
            System.err.println("Ошибка загрузки контента: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    private MenuController loadMenu(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(resolveResource(fxmlPath));
            VBox pane = loader.load();
            attachToContainer(menuPane, pane);
            return loader.getController();
        } catch (IOException e) {
            System.err.println("Ошибка загрузки меню: " + fxmlPath);
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

    private void attachToContainer(AnchorPane container, Node pane) {
        container.getChildren().setAll(pane);
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
    }
}
