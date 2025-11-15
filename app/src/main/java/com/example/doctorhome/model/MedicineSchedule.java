package com.example.doctorhome.model;

import java.io.Serializable;

public class MedicineSchedule implements Serializable {
    private long id;
    private String medicineName;
    private String daysOfWeek; //월,수,금 또는 매일
    private String startDate;
    private String endDate;

    public MedicineSchedule() {
    }

    public MedicineSchedule(long id, String medicineName, String daysOfWeek, String startDate, String endDate) {
        this.id = id;
        this.medicineName = medicineName;
        this.daysOfWeek = daysOfWeek;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
