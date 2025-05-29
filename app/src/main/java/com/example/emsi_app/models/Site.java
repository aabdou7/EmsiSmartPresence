package com.example.emsi_app.models;

public class Site {
    private String id;
    private String name;

    public Site(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }
} 