package com.example.emsi_app.models;

import java.util.Date;

public class Attendance {
    private String id;
    private String studentId;
    private String groupId;
    private Date date;
    private boolean isPresent;
    private String remarks;

    public Attendance(String id, String studentId, String groupId, Date date, boolean isPresent, String remarks) {
        this.id = id;
        this.studentId = studentId;
        this.groupId = groupId;
        this.date = date;
        this.isPresent = isPresent;
        this.remarks = remarks;
    }

    // Getters
    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getGroupId() { return groupId; }
    public Date getDate() { return date; }
    public boolean isPresent() { return isPresent; }
    public String getRemarks() { return remarks; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setDate(Date date) { this.date = date; }
    public void setPresent(boolean present) { isPresent = present; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
} 