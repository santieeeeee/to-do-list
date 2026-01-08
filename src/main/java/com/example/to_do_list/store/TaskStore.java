package com.example.to_do_list.store;

import com.example.to_do_list.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskStore {
    List<Task> findAll();
    Optional<Task> findById(Long id);
    Task save(Task task);
    boolean existsById(Long id);
    void deleteById(Long id);
    void deleteAll();
    long count();
}
