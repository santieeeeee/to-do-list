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
        // No automatic sample tasks created here.
        // Initial sample tasks are stored in `tasks.json` in the repository root.
        return args -> {
            // intentionally left blank to preserve existing `tasks.json` content
        };
    }
}