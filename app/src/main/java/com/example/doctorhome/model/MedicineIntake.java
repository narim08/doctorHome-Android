package com.example.doctorhome.model;

import java.io.Serializable;

//날짜 별 복용 기록 저장
public class MedicineIntake implements Serializable {
    private long id;
    private long scheduleId;
    private String date;
    private boolean completed;

    public MedicineIntake() {
    }

    public MedicineIntake(long id, long scheduleId, String date, boolean completed) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.date = date;
        this.completed = completed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}