package com.taskflow.controller;

import com.taskflow.manager.TaskManager;
import com.taskflow.model.Category;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Bridges the JavaFX views with the {@link TaskManager}. Views call
 * controller methods to mutate state and register a refresh listener so
 * they can re-render when anything changes.
 */
public class TaskController {

    public interface RefreshListener {
        void onTasksChanged();
    }

    private final TaskManager manager;
    private final List<RefreshListener> listeners = new ArrayList<>();

    private Category activeCategory; // null means "All"

    public TaskController(TaskManager manager) {
        this.manager = manager;
    }

    public void addRefreshListener(RefreshListener listener) {
        if (listener != null) listeners.add(listener);
    }

    private void fireRefresh() {
        for (RefreshListener l : listeners) l.onTasksChanged();
    }

    public Category getActiveCategory() {
        return activeCategory;
    }

    public void setActiveCategory(Category category) {
        this.activeCategory = category;
        fireRefresh();
    }

    public void addTask(String title, Category category, Priority priority, String deadline) {
        manager.addTask(new Task(title, category, priority, deadline));
        fireRefresh();
    }

    public void deleteTask(String id) {
        manager.deleteTask(id);
        fireRefresh();
    }

    public void toggleComplete(String id) {
        manager.toggleComplete(id);
        fireRefresh();
    }

    public List<Task> getVisibleTasks() {
        return manager.getByCategory(activeCategory);
    }

    public int getTotalCount() {
        return manager.getTotalCount();
    }

    public int getCompletedCount() {
        return manager.getCompletedCount();
    }

    public int getProgressPercent() {
        return manager.getProgressPercent();
    }

    public int getOpenCountByCategory(Category cat) {
        return manager.getOpenCountByCategory(cat);
    }

    public int getTotalOpenCount() {
        return manager.getOpenCountByCategory(null);
    }
}
