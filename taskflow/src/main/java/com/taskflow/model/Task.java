package com.taskflow.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private Category category;
    private Priority priority;
    private String deadline;
    private boolean isCompleted;
    private String createdAt;

    public Task() {
    }

    public Task(String title, Category category, Priority priority, String deadline) {
        this.id = UUID.randomUUID().toString();
        setTitle(title);
        this.category = category != null ? category : Category.LAINNYA;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.deadline = (deadline != null && !deadline.isBlank()) ? deadline.trim() : null;
        this.isCompleted = false;
        this.createdAt = LocalDateTime.now().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be blank");
        }
        this.title = title.trim();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = (deadline != null && !deadline.isBlank()) ? deadline.trim() : null;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
