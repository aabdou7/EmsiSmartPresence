package com.example.emsi_app.models;

public class Student {
    private String id;
    private String name;
    private String groupId;

    public Student(String id, String name, String groupId) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getGroupId() { return groupId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
} 