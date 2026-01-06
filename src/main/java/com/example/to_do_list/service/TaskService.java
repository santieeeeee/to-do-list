package com.example.to_do_list.service;

import com.example.to_do_list.model.Task;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {
    private final Map<Long, Task> tasks = new HashMap<>();
    private long nextId = 1;

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Optional<Task> getTaskById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public Task createTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<Task> updateTask(Long id, Task task) {
        if (tasks.containsKey(id)) {
            task.setId(id);
            tasks.put(id, task);
            return Optional.of(task);
        }
        return Optional.empty();
    }

    public Optional<Task> patchTask(Long id, Task partial) {
        Task existing = tasks.get(id);
        if (existing != null) {
            if (partial.getTitle() != null) existing.setTitle(partial.getTitle());
            if (partial.getDescription() != null) existing.setDescription(partial.getDescription());
            if (partial.getStatus() != null) existing.setStatus(partial.getStatus());
            return Optional.of(existing);
        }
        return Optional.empty();
    }

    public boolean deleteTask(Long id) {
        return tasks.remove(id) != null;
    }
}
