package com.taskflow.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taskflow.model.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializes the in-memory task list to a JSON file on disk and reads it back
 * on startup. Uses Gson with pretty printing for human-readable storage.
 */
public class JsonStorage {

    private static final String DEFAULT_FILE = "taskflow_data.json";
    private static final Type TASK_LIST_TYPE = new TypeToken<ArrayList<Task>>() {}.getType();

    private final Path file;
    private final Gson gson;

    /**
     * Creates a storage backed by {@code ./taskflow_data.json} in the current
     * working directory.
     */
    public JsonStorage() {
        this(Paths.get(DEFAULT_FILE));
    }

    /**
     * Creates a storage backed by an explicit file path. Useful for tests.
     *
     * @param file absolute or relative path to the JSON data file
     */
    public JsonStorage(Path file) {
        this.file = file;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Reads the persisted tasks from disk. Returns an empty list when the
     * file does not yet exist or contains no tasks.
     *
     * @return mutable list of tasks loaded from disk
     */
    public List<Task> load() {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            if (json.isBlank()) {
                return new ArrayList<>();
            }
            List<Task> tasks = gson.fromJson(json, TASK_LIST_TYPE);
            return tasks != null ? tasks : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("[TaskFlow] Failed to load tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Writes the given task list to disk as pretty-printed JSON, replacing
     * any existing file contents atomically (best-effort on the filesystem).
     *
     * @param tasks the current task list to persist
     */
    public void save(List<Task> tasks) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            String json = gson.toJson(tasks != null ? tasks : new ArrayList<>(), TASK_LIST_TYPE);
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[TaskFlow] Failed to save tasks: " + e.getMessage());
        }
    }

    /**
     * @return the file path this storage reads from / writes to
     */
    public Path getFile() {
        return file;
    }
}
