package com.example.to_do_list.store;

import com.example.to_do_list.model.Task;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class JsonTaskStore implements TaskStore {

    private final Path file = Path.of("tasks.json");
    private final ObjectMapper mapper = new ObjectMapper();

    private synchronized List<Task> readAll() {
        try {
            if (!Files.exists(file)) {
                Files.createFile(file);
                mapper.writeValue(file.toFile(), new ArrayList<Task>());
            }
            List<Task> list = mapper.readValue(file.toFile(), new TypeReference<List<Task>>(){});
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read tasks.json", e);
        }
    }

    private synchronized void writeAll(List<Task> tasks) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), tasks);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write tasks.json", e);
        }
    }

    @Override
    public synchronized List<Task> findAll() {
        return readAll();
    }

    @Override
    public synchronized Optional<Task> findById(Long id) {
        return readAll().stream().filter(t -> t.getId() != null && t.getId().equals(id)).findFirst();
    }

    @Override
    public synchronized Task save(Task task) {
        List<Task> list = readAll();
        if (task.getId() == null) {
            long nextId = list.stream().map(Task::getId).filter(id -> id != null).max(Comparator.naturalOrder()).orElse(0L) + 1;
            task.setId(nextId);
            list.add(task);
        } else {
            boolean updated = false;
            for (int i = 0; i < list.size(); i++) {
                Task t = list.get(i);
                if (t.getId() != null && t.getId().equals(task.getId())) {
                    list.set(i, task);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                list.add(task);
            }
        }
        writeAll(list);
        return task;
    }

    @Override
    public synchronized boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public synchronized void deleteById(Long id) {
        List<Task> list = readAll();
        list.removeIf(t -> t.getId() != null && t.getId().equals(id));
        writeAll(list);
    }

    @Override
    public synchronized void deleteAll() {
        writeAll(new ArrayList<>());
    }

    @Override
    public synchronized long count() {
        return readAll().size();
    }
}
