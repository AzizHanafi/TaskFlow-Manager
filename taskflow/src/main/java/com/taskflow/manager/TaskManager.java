package com.taskflow.manager;

import com.taskflow.model.Category;
import com.taskflow.model.Task;
import com.taskflow.persistence.JsonStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns the in-memory task list and is the single source of truth for CRUD
 * operations. Every mutating method writes the current list back through
 * the supplied {@link JsonStorage} so the disk is always in sync.
 */
public class TaskManager {

    private final ArrayList<Task> tasks;
    private final JsonStorage storage;

    /**
     * Creates a manager backed by the given storage. The storage is loaded
     * immediately so the manager starts in the persisted state.
     *
     * @param storage persistence layer used for save/load
     */
    public TaskManager(JsonStorage storage) {
        this.storage = storage;
        this.tasks = new ArrayList<>(storage.load());
    }

    /**
     * Adds a new task to the list and persists. The task's title is validated
     * by {@link Task#setTitle(String)}; a blank title throws
     * {@link IllegalArgumentException}.
     *
     * @param task the task to add (must be non-null with a non-blank title)
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title cannot be blank");
        }
        tasks.add(task);
        storage.save(tasks);
    }

    /**
     * Removes the task with the given id, if present, and persists.
     *
     * @param id UUID string of the task to remove
     */
    public void deleteTask(String id) {
        if (id == null) return;
        boolean removed = tasks.removeIf(t -> id.equals(t.getId()));
        if (removed) {
            storage.save(tasks);
        }
    }

    /**
     * Flips the completion state of the task with the given id and persists.
     * No-op if no task matches.
     *
     * @param id UUID string of the task to toggle
     */
    public void toggleComplete(String id) {
        if (id == null) return;
        for (Task t : tasks) {
            if (id.equals(t.getId())) {
                t.setCompleted(!t.isCompleted());
                storage.save(tasks);
                return;
            }
        }
    }

    /**
     * @return an unmodifiable view of all tasks in insertion order
     */
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns tasks belonging to a single category. Pass {@code null} to get
     * the same result as {@link #getAll()}.
     *
     * @param cat category filter, or {@code null} for all
     * @return new list of matching tasks
     */
    public List<Task> getByCategory(Category cat) {
        if (cat == null) {
            return new ArrayList<>(tasks);
        }
        List<Task> out = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getCategory() == cat) out.add(t);
        }
        return out;
    }

    /**
     * @return total number of tasks regardless of state
     */
    public int getTotalCount() {
        return tasks.size();
    }

    /**
     * @return number of tasks marked complete
     */
    public int getCompletedCount() {
        int n = 0;
        for (Task t : tasks) if (t.isCompleted()) n++;
        return n;
    }

    /**
     * Calculates progress as a whole percentage of completed vs. total tasks.
     * Returns 0 when there are no tasks (avoids division by zero).
     *
     * @return integer percent in the range [0, 100]
     */
    public int getProgressPercent() {
        int total = getTotalCount();
        if (total == 0) return 0;
        return (int) Math.round((getCompletedCount() * 100.0) / total);
    }

    /**
     * Counts unfinished tasks for a given category. Used by the sidebar to
     * show per-category badges.
     *
     * @param cat category to count, or {@code null} for all open tasks
     * @return number of incomplete tasks matching the filter
     */
    public int getOpenCountByCategory(Category cat) {
        int n = 0;
        for (Task t : tasks) {
            if (t.isCompleted()) continue;
            if (cat == null || t.getCategory() == cat) n++;
        }
        return n;
    }
}
