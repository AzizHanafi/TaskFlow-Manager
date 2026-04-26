package com.taskflow.view;

import com.taskflow.controller.TaskController;
import com.taskflow.model.Task;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class TaskListView extends StackPane {

    private final TaskController controller;
    private final Runnable onAddRequested;
    private final VBox listBox = new VBox(8);
    private final ScrollPane scroll = new ScrollPane(listBox);

    public TaskListView(TaskController controller, Runnable onAddRequested) {
        this.controller = controller;
        this.onAddRequested = onAddRequested;

        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("tf-scroll");

        getChildren().add(scroll);

        controller.addRefreshListener(this::refresh);
        refresh();
    }

    private void refresh() {
        listBox.getChildren().clear();
        List<Task> tasks = controller.getVisibleTasks();
        if (tasks.isEmpty()) {
            EmptyStateView empty = new EmptyStateView(onAddRequested);
            scroll.setContent(empty);
            return;
        }
        if (scroll.getContent() != listBox) {
            scroll.setContent(listBox);
        }
        for (Task t : tasks) {
            listBox.getChildren().add(new TaskCardView(t, controller));
        }
    }
}
