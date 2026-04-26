package com.taskflow;

import com.taskflow.controller.TaskController;
import com.taskflow.manager.TaskManager;
import com.taskflow.persistence.JsonStorage;
import com.taskflow.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        JsonStorage storage = new JsonStorage();
        TaskManager manager = new TaskManager(storage);
        TaskController controller = new TaskController(manager);

        MainView root = new MainView(controller);
        Scene scene = new Scene(root, 1000, 680);
        scene.getStylesheets().add(
                getClass().getResource("/taskflow.css").toExternalForm());

        stage.setTitle("TaskFlow");
        stage.setScene(scene);
        stage.setMinWidth(820);
        stage.setMinHeight(540);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
