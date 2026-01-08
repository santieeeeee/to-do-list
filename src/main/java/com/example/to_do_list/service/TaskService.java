package com.example.to_do_list.service;

import com.example.to_do_list.model.Task;
import com.example.to_do_list.repository.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    // Кэшируем список всех задач
    @Cacheable(value = "tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Кэшируем конкретную задачу по ID
    @Cacheable(value = "task", key = "#id")
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Создание задачи → очищаем кэш списка
    @Transactional
    @CacheEvict(value = {"tasks"}, allEntries = true)
    public Task createTask(Task task) {
        if (task.getStatus() == null) {
            task.setStatus("todo");
        }
        return taskRepository.save(task);
    }

    // Полное обновление задачи → обновляем кэш по ID + очищаем список
    @Transactional
    @CachePut(value = "task", key = "#id")
    @CacheEvict(value = "tasks", allEntries = true)
    public Optional<Task> updateTask(Long id, Task task) {
        if (!taskRepository.existsById(id)) {
            return Optional.empty();
        }
        task.setId(id);
        return Optional.of(taskRepository.save(task));
    }

    // Частичное обновление → обновляем кэш по ID + очищаем список
    @Transactional
    @CachePut(value = "task", key = "#id")
    @CacheEvict(value = "tasks", allEntries = true)
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

    // Удаление → очищаем кэш по ID + очищаем список
    @Transactional
    @CacheEvict(value = {"task", "tasks"}, key = "#id", allEntries = true)
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Фильтрация по статусу — не кэшируем, т.к. зависит от getAllTasks()
    public List<Task> getTasksByStatus(String status) {
        return getAllTasks().stream()
                .filter(task -> status.equals(task.getStatus()))
                .toList();
    }
}
