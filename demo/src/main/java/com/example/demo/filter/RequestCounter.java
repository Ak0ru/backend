package com.example.demo.filter;

public class RequestCounter {
    private long windowStart;
    private int count;

    public RequestCounter(long windowStart, int count) {
        this.windowStart = windowStart;
        this.count = count;
    }

    public long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(long windowStart) {
        this.windowStart = windowStart;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
