package com.taskflow.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EmptyStateView extends VBox {

    public EmptyStateView(Runnable onAdd) {
        getStyleClass().add("tf-empty-state");
        setAlignment(Pos.CENTER);

        Label glyph = new Label("☰");
        glyph.getStyleClass().add("tf-empty-state-glyph");

        Label title = new Label("No tasks here");
        title.getStyleClass().add("tf-empty-state-title");

        Label sub = new Label("Add a task to get started.");
        sub.getStyleClass().add("tf-empty-state-sub");

        Button add = new Button("+ Add task");
        add.getStyleClass().add("tf-btn-primary");
        add.setOnAction(e -> { if (onAdd != null) onAdd.run(); });

        getChildren().addAll(glyph, title, sub, add);
    }
}
