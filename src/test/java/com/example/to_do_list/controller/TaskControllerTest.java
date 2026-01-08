package com.example.to_do_list.controller;

import com.example.to_do_list.model.Task;
import com.example.to_do_list.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// No Mockito extension: use manual stubs to avoid inline-mock/ByteBuddy issues
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
// Mockito removed: tests use simple stub implementations
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskControllerTest {

    private MockMvc mockMvc;

    private com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task(1L, "Buy milk", "3.2% fat", "todo");
        task2 = new Task(2L, "Launch API", "Deploy application", "in_progress");
    }

    @Test
    void getAllTasks_shouldReturnTasksList() throws Exception {
        TaskService stub = new TaskService(null) {
            @Override
            public java.util.List<Task> getAllTasks() {
                return Arrays.asList(task1, task2);
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub)).build();

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Buy milk")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Launch API")));
    }

    @Test
    void getTaskById_shouldReturnTask_whenTaskExists() throws Exception {
        TaskService stub = new TaskService(null) {
            @Override
            public java.util.Optional<Task> getTaskById(Long id) {
                return Optional.of(task1);
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub)).build();

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Buy milk")))
                .andExpect(jsonPath("$.status", is("todo")));
    }

    @Test
    void getTaskById_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        TaskService stub = new TaskService(null) {
            @Override
            public java.util.Optional<Task> getTaskById(Long id) {
                return Optional.empty();
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub)).build();

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        Task newTask = new Task(null, "New Task", "Description", "todo");
        Task savedTask = new Task(3L, "New Task", "Description", "todo");
        TaskService stub = new TaskService(null) {
            @Override
            public Task createTask(Task task) {
                return savedTask;
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub)).build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.status", is("todo")));
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        Task updatedTask = new Task(null, "Updated Task", "Updated Description", "done");
        Task savedTask = new Task(1L, "Updated Task", "Updated Description", "done");
        TaskService stub = new TaskService(null) {
            @Override
            public java.util.Optional<Task> updateTask(Long id, Task task) {
                return Optional.of(savedTask);
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub)).build();

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.status", is("done")));
    }

    @Test
    void updateTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        Task updatedTask = new Task(null, "Updated Task", "Description", "done");
        TaskService stub = new TaskService(null) {
            @Override
            public java.util.Optional<Task> updateTask(Long id, Task task) {
                return Optional.empty();
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub)).build();

        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_shouldReturnNoContent_whenTaskExists() throws Exception {
        TaskService stub = new TaskService(null) {
            @Override
            public boolean deleteTask(Long id) {
                return true;
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub)).build();

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        TaskService stub = new TaskService(null) {
            @Override
            public boolean deleteTask(Long id) {
                return false;
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub)).build();

        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchTask_shouldUpdateStatus() throws Exception {
        Task partial = new Task();
        partial.setStatus("done");

        Task saved = new Task(2L, "Launch API", "Deploy application", "done");
        TaskService stub = new TaskService(null) {
            @Override
            public java.util.Optional<Task> patchTask(Long id, Task partialTask) {
                return Optional.of(saved);
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub)).build();

        mockMvc.perform(patch("/api/tasks/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.status", is("done")));
    }

    @Test
    void patchTask_shouldReturnBadRequest_forInvalidStatus() throws Exception {
        Task partial = new Task();
        partial.setStatus("invalid_status");
        TaskService stub = new TaskService(null) {
            @Override
            public java.util.Optional<Task> patchTask(Long id, Task partialTask) {
                throw new IllegalArgumentException("Invalid status");
            }
        };
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(stub))
                .setControllerAdvice(new com.example.to_do_list.exception.GlobalExceptionHandler())
                .build();

        mockMvc.perform(patch("/api/tasks/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partial)))
                .andExpect(status().isBadRequest());
    }
}