package com.example.emsi_app;

public class Message {
    private String id;
    private String title;
    private String content;
    private String date;
    private String sender; // "direction", "admin", etc.
    private String type; // "announcement", "important", "general"
    private boolean isRead;
    private String professorId; // ID du professeur concerné

    // Constructeur vide requis pour Firestore
    public Message() {}

    public Message(String id, String title, String content, String date, String sender, String type, boolean isRead, String professorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.sender = sender;
        this.type = type;
        this.isRead = isRead;
        this.professorId = professorId;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getDate() { return date; }
    public String getSender() { return sender; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public String getProfessorId() { return professorId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setDate(String date) { this.date = date; }
    public void setSender(String sender) { this.sender = sender; }
    public void setType(String type) { this.type = type; }
    public void setRead(boolean read) { isRead = read; }
    public void setProfessorId(String professorId) { this.professorId = professorId; }
}