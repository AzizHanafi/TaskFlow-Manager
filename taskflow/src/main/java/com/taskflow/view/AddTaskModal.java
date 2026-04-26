package com.taskflow.view;

import com.taskflow.controller.TaskController;
import com.taskflow.model.Category;
import com.taskflow.model.Priority;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class AddTaskModal {

    private final Stage stage;
    private final TaskController controller;

    private Category selectedCategory = Category.KULIAH;
    private Priority selectedPriority = Priority.MEDIUM;

    public AddTaskModal(TaskController controller, Window owner) {
        this.controller = controller;
        this.stage = new Stage(StageStyle.TRANSPARENT);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);

        // Backdrop + centered card
        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        backdrop.setPadding(new Insets(40));

        VBox card = new VBox();
        card.getStyleClass().add("tf-modal-card");
        card.setMaxWidth(440);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Add task");
        title.getStyleClass().add("tf-modal-title");
        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, javafx.scene.layout.Priority.ALWAYS);
        Button close = new Button("✕");
        close.getStyleClass().add("tf-icon-btn");
        close.setOnAction(e -> stage.close());
        header.getChildren().addAll(title, hSpacer, close);

        // Title field
        Label titleLbl = new Label("Task title");
        titleLbl.getStyleClass().add("tf-field-label");
        TextField titleInput = new TextField();
        titleInput.setPromptText("What needs to be done?");
        titleInput.getStyleClass().add("tf-text-field");

        // Category chips
        Label catLbl = new Label("Category");
        catLbl.getStyleClass().add("tf-field-label");
        HBox catChips = new HBox(6);
        for (Category cat : Category.values()) {
            Button chip = chip(cat.getLabel(), categoryChipClass(cat), cat == selectedCategory);
            chip.setOnAction(e -> {
                selectedCategory = cat;
                rerenderChips(catChips, cat, true);
            });
            catChips.getChildren().add(chip);
        }

        // Priority chips
        Label priLbl = new Label("Priority");
        priLbl.getStyleClass().add("tf-field-label");
        HBox priChips = new HBox(6);
        for (Priority p : Priority.values()) {
            Button chip = chip(p.getLabel(), priorityChipClass(p), p == selectedPriority);
            chip.setOnAction(e -> {
                selectedPriority = p;
                rerenderChips(priChips, p, false);
            });
            priChips.getChildren().add(chip);
        }

        // Deadline
        Label dlLbl = new Label("Deadline (optional)");
        dlLbl.getStyleClass().add("tf-field-label");
        TextField dlInput = new TextField();
        dlInput.setPromptText("e.g. Due May 10");
        dlInput.getStyleClass().add("tf-text-field");

        // Footer
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("tf-btn-secondary");
        cancel.setOnAction(e -> stage.close());

        Button submit = new Button("Add task");
        submit.getStyleClass().add("tf-btn-primary");
        submit.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> titleInput.getText() == null || titleInput.getText().trim().isEmpty(),
                        titleInput.textProperty()));
        submit.setOnAction(e -> {
            String t = titleInput.getText() == null ? "" : titleInput.getText().trim();
            if (t.isEmpty()) return;
            controller.addTask(t, selectedCategory, selectedPriority,
                    dlInput.getText() == null ? null : dlInput.getText().trim());
            stage.close();
        });

        HBox footer = new HBox(8, cancel, submit);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(4, 0, 0, 0));

        card.getChildren().addAll(
                header,
                new VBox(titleLbl, titleInput),
                new VBox(catLbl, wrap(catChips)),
                new VBox(priLbl, wrap(priChips)),
                new VBox(dlLbl, dlInput),
                footer
        );

        backdrop.getChildren().add(card);
        backdrop.setOnMouseClicked(e -> {
            if (e.getTarget() == backdrop) stage.close();
        });

        Scene scene = new Scene(backdrop);
        scene.setFill(Color.TRANSPARENT);
        if (owner != null && owner.getScene() != null) {
            scene.getStylesheets().setAll(owner.getScene().getStylesheets());
        }
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) stage.close();
        });
        stage.setScene(scene);
        stage.setWidth(560);
        stage.setHeight(520);

        // Center over owner
        if (owner != null) {
            stage.setX(owner.getX() + (owner.getWidth() - 560) / 2.0);
            stage.setY(owner.getY() + (owner.getHeight() - 520) / 2.0);
        }

        titleInput.requestFocus();
    }

    public void showAndWait() {
        stage.showAndWait();
    }

    private VBox wrap(HBox row) {
        row.setPadding(new Insets(2, 0, 0, 0));
        return new VBox(row);
    }

    private Button chip(String label, String typeClass, boolean active) {
        Button b = new Button(label);
        b.getStyleClass().addAll("tf-chip", typeClass);
        if (active) b.getStyleClass().add("active");
        return b;
    }

    private void rerenderChips(HBox container, Object selected, boolean isCategory) {
        for (int i = 0; i < container.getChildren().size(); i++) {
            Button b = (Button) container.getChildren().get(i);
            b.getStyleClass().remove("active");
            if (isCategory) {
                if (Category.values()[i] == selected) b.getStyleClass().add("active");
            } else {
                if (Priority.values()[i] == selected) b.getStyleClass().add("active");
            }
        }
    }

    private static String categoryChipClass(Category cat) {
        return switch (cat) {
            case KULIAH -> "cat-kuliah";
            case KERJA -> "cat-kerja";
            case PRIBADI -> "cat-pribadi";
            case LAINNYA -> "cat-lainnya";
        };
    }

    private static String priorityChipClass(Priority p) {
        return switch (p) {
            case HIGH -> "pri-high";
            case MEDIUM -> "pri-medium";
            case LOW -> "pri-low";
        };
    }
}
