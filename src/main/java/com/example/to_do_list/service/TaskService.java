package com.example.to_do_list.service;

import com.example.to_do_list.exception.NotFoundException;
import com.example.to_do_list.model.Task;
import com.example.to_do_list.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional
    public Task createTask(Task task) {
        if (task.getStatus() == null) {
            task.setStatus("todo");
        }
        return taskRepository.save(task);
    }

    @Transactional
    public Optional<Task> updateTask(Long id, Task task) {
        if (!taskRepository.existsById(id)) {
            return Optional.empty();
        }
        task.setId(id);
        return Optional.of(taskRepository.save(task));
    }

    @Transactional
    public Optional<Task> patchTask(Long id, Task partial) {
        return taskRepository.findById(id)
                .map(existing -> {
                    if (partial.getTitle() != null && !partial.getTitle().isEmpty()) {
                        existing.setTitle(partial.getTitle());
                    }
                    if (partial.getDescription() != null) {
                        existing.setDescription(partial.getDescription());
                    }
                    if (partial.getStatus() != null && !partial.getStatus().isEmpty()) {
                        existing.setStatus(partial.getStatus());
                    }
                    return taskRepository.save(existing);
                });
    }

    @Transactional
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Дополнительные методы для расширения функционала
    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findAll().stream()
                .filter(task -> status.equals(task.getStatus()))
                .toList();
    }
}