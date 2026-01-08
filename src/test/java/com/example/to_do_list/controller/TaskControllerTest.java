package com.example.to_do_list.controller;

import com.example.to_do_list.model.Task;
import com.example.to_do_list.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task(1L, "Buy milk", "3.2% fat", "todo");
        task2 = new Task(2L, "Launch API", "Deploy application", "in_progress");
    }

    @Test
    void getAllTasks_shouldReturnTasksList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Buy milk")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Launch API")));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void getTaskById_shouldReturnTask_whenTaskExists() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task1));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Buy milk")))
                .andExpect(jsonPath("$.status", is("todo")));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void getTaskById_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        when(taskService.getTaskById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById(999L);
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        Task newTask = new Task(null, "New Task", "Description", "todo");
        Task savedTask = new Task(3L, "New Task", "Description", "todo");

        when(taskService.createTask(any(Task.class))).thenReturn(savedTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.status", is("todo")));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        Task updatedTask = new Task(null, "Updated Task", "Updated Description", "done");
        Task savedTask = new Task(1L, "Updated Task", "Updated Description", "done");

        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(Optional.of(savedTask));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.status", is("done")));

        verify(taskService, times(1)).updateTask(eq(1L), any(Task.class));
    }

    @Test
    void updateTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        Task updatedTask = new Task(null, "Updated Task", "Description", "done");

        when(taskService.updateTask(eq(999L), any(Task.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateTask(eq(999L), any(Task.class));
    }

    @Test
    void deleteTask_shouldReturnNoContent_whenTaskExists() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void deleteTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        when(taskService.deleteTask(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).deleteTask(999L);
    }

    @Test
    void patchTask_shouldUpdateStatus() throws Exception {
        Task partial = new Task();
        partial.setStatus("done");

        Task saved = new Task(2L, "Launch API", "Deploy application", "done");

        when(taskService.patchTask(eq(2L), any(Task.class))).thenReturn(Optional.of(saved));

        mockMvc.perform(patch("/api/tasks/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.status", is("done")));

        verify(taskService, times(1)).patchTask(eq(2L), any(Task.class));
    }

    @Test
    void patchTask_shouldReturnBadRequest_forInvalidStatus() throws Exception {
        Task partial = new Task();
        partial.setStatus("invalid_status");

        when(taskService.patchTask(eq(2L), any(Task.class))).thenThrow(new IllegalArgumentException("Invalid status"));

        mockMvc.perform(patch("/api/tasks/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partial)))
                .andExpect(status().isBadRequest());

        verify(taskService, times(1)).patchTask(eq(2L), any(Task.class));
    }
}