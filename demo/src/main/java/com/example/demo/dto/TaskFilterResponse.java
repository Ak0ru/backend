package com.example.demo.dto;

import com.example.demo.model.Task;
import java.util.List;
import java.util.Map;

public class TaskFilterResponse {
    private List<Task> content;
    private long totalElements;
    private int totalPages;
    private Map<String,String> filtersApplied;

    public TaskFilterResponse() { }

    public TaskFilterResponse(List<Task> content, long totalElements, int totalPages, Map<String,String> filtersApplied) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.filtersApplied = filtersApplied;
    }

    public List<Task> getContent() { return content; }
    public void setContent(List<Task> content) { this.content = content; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public Map<String, String> getFiltersApplied() { return filtersApplied; }
    public void setFiltersApplied(Map<String, String> filtersApplied) { this.filtersApplied = filtersApplied; }
}
