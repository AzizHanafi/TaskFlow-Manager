package com.taskflow.view;

import com.taskflow.controller.TaskController;
import com.taskflow.model.Category;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class TaskCardView extends HBox {

    public TaskCardView(Task task, TaskController controller) {
        getStyleClass().add("tf-task-card");
        if (task.isCompleted()) getStyleClass().add("done");
        setAlignment(Pos.CENTER_LEFT);

        // Checkbox
        Button checkbox = new Button(task.isCompleted() ? "✓" : "");
        checkbox.getStyleClass().add("tf-checkbox");
        if (task.isCompleted()) checkbox.getStyleClass().add("checked");
        checkbox.setOnAction(e -> controller.toggleComplete(task.getId()));

        // Middle: title + meta row
        VBox middle = new VBox(0);
        HBox.setHgrow(middle, javafx.scene.layout.Priority.ALWAYS);

        Label title = new Label(task.getTitle());
        title.getStyleClass().add("tf-task-title");
        title.setMaxWidth(Double.MAX_VALUE);

        HBox meta = new HBox();
        meta.getStyleClass().add("tf-task-meta");

        Label badge = new Label(task.getCategory().getLabel());
        badge.getStyleClass().addAll("tf-badge", categoryClass(task.getCategory()));

        Region dot = new Region();
        dot.getStyleClass().addAll("tf-priority-dot", priorityClass(task.getPriority()));

        meta.getChildren().addAll(badge, dot);

        if (task.getDeadline() != null && !task.getDeadline().isBlank()) {
            Label dl = new Label("⏱ " + task.getDeadline());
            dl.getStyleClass().add("tf-task-deadline");
            meta.getChildren().add(dl);
        }
        if (task.isCompleted()) {
            Label done = new Label("Done");
            done.getStyleClass().addAll("tf-badge", "done-badge");
            meta.getChildren().add(done);
        }

        middle.getChildren().addAll(title, meta);

        // Right: actions
        HBox actions = new HBox(2);
        actions.getStyleClass().add("tf-task-actions");
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button delete = new Button("🗑");
        delete.getStyleClass().addAll("tf-icon-btn", "danger");
        delete.setOnAction(e -> confirmAndDelete(task, controller));

        Button archive = new Button("⌫");
        archive.getStyleClass().add("tf-icon-btn");
        archive.setOnAction(e -> {/* archive: out of scope */});

        actions.getChildren().addAll(delete, archive);

        getChildren().addAll(checkbox, middle, actions);
    }

    private void confirmAndDelete(Task task, TaskController controller) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete task");
        alert.setHeaderText(null);
        alert.setContentText("Delete this task?");
        alert.getDialogPane().getStylesheets().addAll(getScene().getStylesheets());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.deleteTask(task.getId());
        }
    }

    private static String categoryClass(Category cat) {
        return switch (cat) {
            case KULIAH -> "cat-kuliah";
            case KERJA -> "cat-kerja";
            case PRIBADI -> "cat-pribadi";
            case LAINNYA -> "cat-lainnya";
        };
    }

    private static String priorityClass(Priority p) {
        return switch (p) {
            case HIGH -> "high";
            case MEDIUM -> "medium";
            case LOW -> "low";
        };
    }
}
