package com.example.emsi_app;

public class MakeupSession {
    private String id;
    private String courseName;
    private String date;
    private String startTime;
    private String endTime;
    private String room;
    private String professorId;
    private String status; // "planned", "completed", "cancelled"

    // Constructeur vide requis pour Firestore
    public MakeupSession() {}

    public MakeupSession(String id, String courseName, String date, String startTime, String endTime, String room, String professorId, String status) {
        this.id = id;
        this.courseName = courseName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.professorId = professorId;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getCourseName() { return courseName; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getRoom() { return room; }
    public String getProfessorId() { return professorId; }
    public String getStatus() { return status; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setDate(String date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setRoom(String room) { this.room = room; }
    public void setProfessorId(String professorId) { this.professorId = professorId; }
    public void setStatus(String status) { this.status = status; }
} 