package com.example.emsi_app.models;

public class Group {
    private String id;
    private String name;
    private String siteId;

    public Group(String id, String name, String siteId) {
        this.id = id;
        this.name = name;
        this.siteId = siteId;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSiteId() { return siteId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    @Override
    public String toString() {
        return name;
    }
} 