package com.example.demo.service;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.TaskFilterResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.spec.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        System.out.println("Task saved with ID: " + savedTask.getId());
        return savedTask;
    }

    public PageResponse<Task> getPaginatedTasks(Pageable pageable) {
        Page<Task> page = taskRepository.findAll(pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }

    @Transactional
    public Task updateTask(Long id, Task taskDetails) {
        Task existing = getTaskById(id);
        existing.setTitle(taskDetails.getTitle());
        existing.setDescription(taskDetails.getDescription());
        existing.setCompleted(taskDetails.isCompleted());
        existing.setTags(taskDetails.getTags());
        return taskRepository.save(existing);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task existing = getTaskById(id);
        taskRepository.delete(existing);
    }

    public TaskFilterResponse filterTasks(
            String title,
            String titleLike,
            Boolean completed,
            String description,
            Pageable pageable
    ) {
        Specification<Task> spec = Specification.where(null);
        Map<String, String> filters = new HashMap<>();

        if (title != null) {
            spec = spec.and(TaskSpecification.hasTitle(title));
            filters.put("title", title);
        }
        if (titleLike != null) {
            spec = spec.and(TaskSpecification.titleLike(titleLike));
            filters.put("title_like", titleLike);
        }
        if (completed != null) {
            spec = spec.and(TaskSpecification.hasCompleted(completed));
            filters.put("completed", completed.toString());
        }
        if (description != null) {
            spec = spec.and(TaskSpecification.hasDescription(description));
            filters.put("description", description);
        }

        var page = taskRepository.findAll(spec, pageable);
        return new TaskFilterResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                filters
        );
    }

    /**
     * Project Part #5: Search tasks by query in title or description
     */
    public List<Task> searchTasks(String query) {
        if (query == null || query.trim().length() < 1) {
            throw new IllegalArgumentException("Query must be at least 1 character");
        }
        return taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }
}
