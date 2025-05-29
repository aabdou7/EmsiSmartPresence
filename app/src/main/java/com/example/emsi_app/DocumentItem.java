package com.example.emsi_app;

public class DocumentItem {
    private String name;
    private String type;
    private String url;

    public DocumentItem(String name, String type, String url) {
        this.name = name;
        this.type = type;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
} 