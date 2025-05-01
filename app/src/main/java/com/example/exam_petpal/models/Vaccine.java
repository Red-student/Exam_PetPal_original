package com.example.exam_petpal.models;

import java.util.Date;
import java.util.UUID;

public class Vaccine {
    private String id;
    private String petId;
    private String name;
    private Date date;
    private Date nextDate;
    private String notes;
    private Date expirationDate;
    private String description;

    public Vaccine() {
        this.id = UUID.randomUUID().toString();
    }

    public Vaccine(String petId, String name, Date date, Date nextDate, String notes) {
        this.id = UUID.randomUUID().toString();
        this.petId = petId;
        this.name = name;
        this.date = date;
        this.nextDate = nextDate;
        this.notes = notes;
    }

    public Vaccine(String name, Date date, Date expirationDate, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.date = date;
        this.expirationDate = expirationDate;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getNextDate() {
        return nextDate;
    }

    public void setNextDate(Date nextDate) {
        this.nextDate = nextDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 