package com.example.demo.view;

import com.example.demo.LayoutAware;
import com.example.demo.LayoutController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import java.io.IOException;

public class HomeViewController implements LayoutAware {

    @FXML
    private TilePane basicLessons;

    private LayoutController layoutController;

    @Override
    public void setLayoutController(LayoutController layoutController) {
        this.layoutController = layoutController;
    }

    public void initialize() {
        System.out.println("Инициализация Home_view");
        setCardsBasicLessons();
    }

    private void setCardsBasicLessons() {
        for (int i = 0; i < 6; i++) {
            try {
                FXMLLoader loaderCard = new FXMLLoader(getClass().getResource("/com/example/demo/components/study_cards.fxml"));
                AnchorPane pane = loaderCard.load();
                pane.setId("card" + i);

                pane.setOnMouseClicked(mouseEvent -> {
                    System.out.println("Нажата карточка: " + pane.getId());
                    if (layoutController != null) {
                        layoutController.loadScene("activity/blind_printing_activity.fxml");
                    }
                });

                basicLessons.getChildren().add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
