package com.taskflow.view;

import com.taskflow.controller.TaskController;
import com.taskflow.model.Category;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

public class SidebarView extends VBox {

    private static final Map<Category, String> CAT_DOT_COLOR = new LinkedHashMap<>();
    static {
        CAT_DOT_COLOR.put(Category.KULIAH,  "#93C5FD");
        CAT_DOT_COLOR.put(Category.KERJA,   "#C4B5FD");
        CAT_DOT_COLOR.put(Category.PRIBADI, "#FCA5A5");
        CAT_DOT_COLOR.put(Category.LAINNYA, "#A1A1AA");
    }

    private final TaskController controller;
    private final VBox itemsBox = new VBox();
    private final Label progressLabel = new Label("Progress");
    private final Label progressCount = new Label("0 / 0 done");
    private final ProgressBar progressBar = new ProgressBar(0);

    public SidebarView(TaskController controller) {
        this.controller = controller;
        getStyleClass().add("tf-sidebar");

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("tf-sidebar-header");
        StackPane logo = new StackPane(makeLogoMark());
        logo.getStyleClass().add("tf-sidebar-logo");
        Label title = new Label("TaskFlow");
        title.getStyleClass().add("tf-sidebar-title");
        header.getChildren().addAll(logo, title);
        header.setSpacing(8);
        header.setAlignment(Pos.CENTER_LEFT);

        // Section label
        Label section = new Label("CATEGORIES");
        section.getStyleClass().add("tf-sidebar-section-label");

        itemsBox.getStyleClass().add("tf-sidebar-list");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Progress block
        VBox progressWrap = new VBox();
        progressWrap.getStyleClass().add("tf-sidebar-progress-wrap");
        HBox progressRow = new HBox();
        progressRow.setAlignment(Pos.CENTER_LEFT);
        progressLabel.getStyleClass().add("tf-progress-label");
        progressCount.getStyleClass().add("tf-progress-count");
        Region pSpacer = new Region();
        HBox.setHgrow(pSpacer, Priority.ALWAYS);
        progressRow.getChildren().addAll(progressLabel, pSpacer, progressCount);
        progressBar.getStyleClass().add("tf-progress-bar");
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressWrap.getChildren().addAll(progressRow, progressBar);

        getChildren().addAll(header, section, itemsBox, spacer, progressWrap);

        controller.addRefreshListener(this::refresh);
        refresh();
    }

    private Label makeLogoMark() {
        Label l = new Label("✓"); // check mark
        l.getStyleClass().add("tf-logo-mark");
        return l;
    }

    private void refresh() {
        itemsBox.getChildren().clear();

        // "All tasks"
        itemsBox.getChildren().add(buildItem(null, "All tasks", controller.getTotalOpenCount(), null));
        for (Category cat : Category.values()) {
            itemsBox.getChildren().add(buildItem(
                    cat, cat.getLabel(),
                    controller.getOpenCountByCategory(cat),
                    CAT_DOT_COLOR.get(cat)));
        }

        int done = controller.getCompletedCount();
        int total = controller.getTotalCount();
        progressCount.setText(done + " / " + total + " done");
        progressBar.setProgress(total == 0 ? 0 : (double) done / total);
    }

    private HBox buildItem(Category cat, String label, int count, String dotColor) {
        HBox row = new HBox();
        row.getStyleClass().add("tf-sidebar-item");
        boolean active = (cat == null && controller.getActiveCategory() == null)
                || (cat != null && cat == controller.getActiveCategory());
        if (active) row.getStyleClass().add("active");

        Region marker;
        if (dotColor == null) {
            // "All" — render a small grid-ish glyph using a label
            Label glyph = new Label("≡"); // ☰
            glyph.setStyle("-fx-text-fill: " + (active ? "#18181B" : "#A1A1AA")
                    + "; -fx-font-size: 14px;");
            marker = new StackPane(glyph);
            marker.setMinWidth(14);
            marker.setMaxWidth(14);
        } else {
            Region dot = new Region();
            dot.getStyleClass().add("tf-sidebar-cat-dot");
            dot.setStyle("-fx-background-color: " + dotColor + ";");
            marker = dot;
        }

        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("tf-sidebar-item-label");
        HBox.setHgrow(labelNode, Priority.ALWAYS);
        labelNode.setMaxWidth(Double.MAX_VALUE);

        Label countNode = new Label(count > 0 ? String.valueOf(count) : "");
        countNode.getStyleClass().add("tf-sidebar-item-count");

        row.getChildren().addAll(marker, labelNode, countNode);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setOnMouseClicked(e -> controller.setActiveCategory(cat));
        return row;
    }
}
