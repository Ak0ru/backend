package com.example.demo.controller;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.TaskFilterResponse;
import com.example.demo.model.AppUser;
import com.example.demo.model.Task;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AppUserRepository userRepository;

    // 1. Pagination & Sorting
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public PageResponse<Task> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction dir = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort[0]));
        return taskService.getPaginatedTasks(pageable);
    }

    // 2. Filtering
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public TaskFilterResponse filterTasks(
            @RequestParam(required = false) String title,
            @RequestParam(value = "title_like", required = false) String titleLike,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction dir = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort[0]));

        return taskService.filterTasks(title, titleLike, completed, description, pageable);
    }

    // 3. Search by query (title/description)
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam(name = "query") String query) {
        try {
            List<Task> result = taskService.searchTasks(query);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    // 4. Get by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, Principal principal) {
        Task task = taskService.getTaskById(id);
        AppUser user = userRepository.findByUsername(principal.getName()).orElseThrow();
        boolean allowed = user.getRole().equalsIgnoreCase("ADMIN")
                || user.getRole().equalsIgnoreCase("TEACHER")
                || task.getOwner().getUsername().equals(user.getUsername());
        if (!allowed) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(task);
    }

    // 5. Create
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Task> createTask(@RequestBody Task task, Principal principal) {
        AppUser user = userRepository.findByUsername(principal.getName()).orElseThrow();
        task.setOwner(user);
        Task created = taskService.createTask(task);
        return ResponseEntity.ok(created);
    }

    // 6. Update
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @RequestBody Task updated,
                                           Principal principal) {
        Task existing = taskService.getTaskById(id);
        AppUser user = userRepository.findByUsername(principal.getName()).orElseThrow();
        boolean allowed = user.getRole().equalsIgnoreCase("ADMIN")
                || user.getRole().equalsIgnoreCase("TEACHER")
                || existing.getOwner().getUsername().equals(user.getUsername());
        if (!allowed) {
            return ResponseEntity.status(403).build();
        }
        updated.setOwner(existing.getOwner()); // сохранить владельца
        Task saved = taskService.updateTask(id, updated);
        return ResponseEntity.ok(saved);
    }

    // 7. Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Principal principal) {
        Task existing = taskService.getTaskById(id);
        AppUser user = userRepository.findByUsername(principal.getName()).orElseThrow();
        boolean allowed = user.getRole().equalsIgnoreCase("ADMIN")
                || user.getRole().equalsIgnoreCase("TEACHER")
                || existing.getOwner().getUsername().equals(user.getUsername());
        if (!allowed) {
            return ResponseEntity.status(403).build();
        }
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
