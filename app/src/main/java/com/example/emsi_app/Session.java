package com.example.emsi_app;

import com.google.firebase.Timestamp;

public class Session {
    private String id;
    private Timestamp timestamp;
    private String deviceInfo;

    public Session() {
        // Constructeur vide requis pour Firestore
    }

    public Session(String id, Timestamp timestamp, String deviceInfo) {
        this.id = id;
        this.timestamp = timestamp;
        this.deviceInfo = deviceInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
} 