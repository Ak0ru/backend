package com.example.demo.spec;

import com.example.demo.model.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<Task> hasTitle(String title) {
        return (root, query, cb) -> cb.equal(root.get("title"), title);
    }

    public static Specification<Task> titleLike(String titlePart) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), titlePart.toLowerCase() + "%");
    }

    public static Specification<Task> hasCompleted(Boolean completed) {
        return (root, query, cb) -> cb.equal(root.get("completed"), completed);
    }

    public static Specification<Task> hasDescription(String description) {
        return (root, query, cb) -> cb.equal(root.get("description"), description);
    }
}
