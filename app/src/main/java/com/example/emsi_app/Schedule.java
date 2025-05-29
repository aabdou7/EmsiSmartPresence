package com.example.emsi_app;

public class Schedule {
    private String day;
    private String startTime;
    private String endTime;
    private String subject;
    private String groupName;
    private String siteName;
    private String room;
    private String type;

    public Schedule(String day, String startTime, String endTime, String subject, 
                   String groupName, String siteName, String room, String type) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.groupName = groupName;
        this.siteName = siteName;
        this.room = room;
        this.type = type;
    }

    // Getters
    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSubject() {
        return subject;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getRoom() {
        return room;
    }

    public String getType() {
        return type;
    }

    // Format pour l'affichage
    public String getTimeDisplay() {
        return startTime + " - " + endTime;
    }

    public String getFullDisplay() {
        return subject + "\n" +
               groupName + " - " + siteName + "\n" +
               room + " (" + type + ")";
    }
} 