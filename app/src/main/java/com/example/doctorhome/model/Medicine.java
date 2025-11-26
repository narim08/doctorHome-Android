package com.example.doctorhome.model;

import java.io.Serializable;

public class Medicine implements Serializable {
    private long id;
    private String name;
    private String expiryDate;
    private int quantity;
    private String medicineUsage;
    private String imageUrl;

    public Medicine () {

    }
    public Medicine(long id, String name, String expiryDate, int quantity, String medicineUsage, String imageUrl) {
        this.id = id;
        this.name = name;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.medicineUsage = medicineUsage;
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMedicineUsage() {
        return medicineUsage;
    }
    public void setMedicineUsage(String medicineUsage) {
        this.medicineUsage = medicineUsage;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
