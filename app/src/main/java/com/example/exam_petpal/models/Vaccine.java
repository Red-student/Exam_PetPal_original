package com.example.exam_petpal.models;

import java.io.Serializable;
import java.util.Date;

public class Vaccine implements Serializable {
    private String name;
    private Date date;
    private Date expiryDate;
    private String description;

    public Vaccine(String name, Date date, Date expiryDate, String description) {
        this.name = name;
        this.date = date;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
} 