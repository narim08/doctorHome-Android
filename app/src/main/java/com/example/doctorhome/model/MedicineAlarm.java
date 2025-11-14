package com.example.doctorhome.model;

import java.io.Serializable;

public class MedicineAlarm implements Serializable {
    private long id;
    private String medicineName;
    private String usage; //복용법
    private String time; //알림 시간(HH:mm 형식)
    private boolean isActive; //알림 활성화 여부

    public MedicineAlarm() {
    }

    public MedicineAlarm(long id, String medicineName, String usage, String time, boolean isActive) {
        this.id = id;
        this.medicineName = medicineName;
        this.usage = usage;
        this.time = time;
        this.isActive = isActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}