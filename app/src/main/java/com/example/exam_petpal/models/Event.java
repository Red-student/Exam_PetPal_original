package com.example.exam_petpal.models;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private String id;
    private String petId;
    private String title;
    private String description;
    private Date date;
    private String type;
    private boolean repeatWeekly;
    private boolean repeatMonthly;
    private boolean notificationEnabled;
    private String notificationTime;

    public Event() {
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public Event(String id, String petId, String title, String description, Date date, String type, boolean repeatWeekly, boolean repeatMonthly) {
        this.id = id;
        this.petId = petId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.repeatWeekly = repeatWeekly;
        this.repeatMonthly = repeatMonthly;
        this.notificationEnabled = false;
    }

    // Геттеры и сеттеры
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRepeatWeekly() {
        return repeatWeekly;
    }

    public void setRepeatWeekly(boolean repeatWeekly) {
        this.repeatWeekly = repeatWeekly;
    }

    public boolean isRepeatMonthly() {
        return repeatMonthly;
    }

    public void setRepeatMonthly(boolean repeatMonthly) {
        this.repeatMonthly = repeatMonthly;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
} 