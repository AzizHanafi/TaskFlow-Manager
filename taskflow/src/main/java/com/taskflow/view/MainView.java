package com.taskflow.view;

import com.taskflow.controller.TaskController;
import com.taskflow.model.Category;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainView extends BorderPane {

    private final TaskController controller;
    private final TaskListView listView;
    private final SidebarView sidebar;
    private final Label titleLabel;
    private final Label subtitleLabel;

    public MainView(TaskController controller) {
        this.controller = controller;
        getStyleClass().add("tf-app-root");

        sidebar = new SidebarView(controller);
        setLeft(sidebar);

        // ----- Content (header + list) -----
        VBox content = new VBox();
        content.getStyleClass().add("tf-content");

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("tf-content-header");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(2);
        titleLabel = new Label("All tasks");
        titleLabel.getStyleClass().add("tf-content-title");
        subtitleLabel = new Label("");
        subtitleLabel.getStyleClass().add("tf-content-subtitle");
        titles.getChildren().addAll(titleLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add task");
        addBtn.getStyleClass().add("tf-btn-primary");
        addBtn.setOnAction(e -> openAddModal());

        header.getChildren().addAll(titles, spacer, addBtn);

        // List
        listView = new TaskListView(controller, this::openAddModal);
        VBox.setVgrow(listView, Priority.ALWAYS);

        content.getChildren().addAll(header, listView);
        setCenter(content);

        controller.addRefreshListener(this::refreshHeader);
        refreshHeader();
    }

    private void refreshHeader() {
        Category cat = controller.getActiveCategory();
        titleLabel.setText(cat == null ? "All tasks" : cat.getLabel());
        int open = cat == null
                ? controller.getTotalOpenCount()
                : controller.getOpenCountByCategory(cat);
        int total = controller.getTotalCount();
        if (total == 0) {
            subtitleLabel.setText("No tasks yet");
        } else {
            subtitleLabel.setText(open + " open • " + controller.getCompletedCount() + " done");
        }
    }

    private void openAddModal() {
        AddTaskModal modal = new AddTaskModal(controller, getScene().getWindow());
        modal.showAndWait();
    }
}
