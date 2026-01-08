package com.example.to_do_list.service;

import com.example.to_do_list.model.Task;
import com.example.to_do_list.store.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskStore taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task(1L, "Task 1", "Description 1", "todo");
        task2 = new Task(2L, "Task 2", "Description 2", "in_progress");
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Task 1");
        assertThat(result.get(1).getTitle()).isEqualTo("Task 2");
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        // Act
        Optional<Task> result = taskService.getTaskById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_shouldReturnEmpty_whenNotExists() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Task> result = taskService.getTaskById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void createTask_shouldSaveTask() {
        // Arrange
        Task newTask = new Task(null, "New Task", "Description", null);
        Task savedTask = new Task(3L, "New Task", "Description", "todo");

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task result = taskService.createTask(newTask);

        // Assert
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getTitle()).isEqualTo("New Task");
        assertThat(result.getStatus()).isEqualTo("todo");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_shouldSetDefaultStatus_whenStatusIsNull() {
        // Arrange
        Task newTask = new Task(null, "New Task", "Description", null);
        Task savedTask = new Task(1L, "New Task", "Description", "todo");

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task result = taskService.createTask(newTask);

        // Assert
        assertThat(result.getStatus()).isEqualTo("todo");
    }

    @Test
    void updateTask_shouldReturnUpdatedTask_whenExists() {
        // Arrange
        Task updatedTask = new Task(null, "Updated Task", "Updated Desc", "done");
        Task savedTask = new Task(1L, "Updated Task", "Updated Desc", "done");

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Optional<Task> result = taskService.updateTask(1L, updatedTask);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getTitle()).isEqualTo("Updated Task");
        assertThat(result.get().getStatus()).isEqualTo("done");
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_shouldReturnEmpty_whenNotExists() {
        // Arrange
        Task updatedTask = new Task(null, "Updated Task", "Description", "done");

        when(taskRepository.existsById(999L)).thenReturn(false);

        // Act
        Optional<Task> result = taskService.updateTask(999L, updatedTask);

        // Assert
        assertThat(result).isEmpty();
        verify(taskRepository, times(1)).existsById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void patchTask_shouldUpdateOnlyProvidedFields() {
        // Arrange
        Task existingTask = new Task(1L, "Original Title", "Original Desc", "todo");
        Task partialUpdate = new Task(null, "Updated Title", null, "in_progress");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        // Act
        Optional<Task> result = taskService.patchTask(1L, partialUpdate);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Updated Title");
        assertThat(result.get().getDescription()).isEqualTo("Original Desc");
        assertThat(result.get().getStatus()).isEqualTo("in_progress");
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void deleteTask_shouldReturnTrue_whenExists() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // Act
        boolean result = taskService.deleteTask(1L);

        // Assert
        assertThat(result).isTrue();
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_shouldReturnFalse_whenNotExists() {
        // Arrange
        when(taskRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = taskService.deleteTask(999L);

        // Assert
        assertThat(result).isFalse();
        verify(taskRepository, times(1)).existsById(999L);
        verify(taskRepository, never()).deleteById(anyLong());
    }
}