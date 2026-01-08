package com.example.to_do_list.config;

import com.example.to_do_list.model.Task;
import com.example.to_do_list.store.TaskStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(TaskStore repository) {
        return args -> {
            repository.deleteAll();

            Task task1 = new Task();
            task1.setTitle("Complete Spring Boot Project");
            task1.setDescription("Finish the To-Do List API with H2 database");
            task1.setStatus("in_progress");

            Task task2 = new Task();
            task2.setTitle("Test API Endpoints");
            task2.setDescription("Test all CRUD operations using Swagger UI");
            task2.setStatus("todo");

            Task task3 = new Task();
            task3.setTitle("Deploy Application");
            task3.setDescription("Deploy to cloud platform");
            task3.setStatus("done");

            repository.save(task1);
            repository.save(task2);
            repository.save(task3);

            System.out.println("=== Database Initialized ===");
            System.out.println("Created " + repository.count() + " sample tasks");
        };
    }
}