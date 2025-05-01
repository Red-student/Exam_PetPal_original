package com.example.exam_petpal.models;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private String id;
    private String petId;
    private String title;
    private Date date;
    private String description;
    private EventType type;
    private boolean repeatWeekly;
    private boolean repeatMonthly;

    public enum EventType {
        VACCINATION("Вакцинация"),
        WALK("Прогулка"),
        FEEDING("Кормление"),
        MEDICATION("Прием лекарств"),
        GROOMING("Груминг"),
        OTHER("Другое");

        private final String displayName;

        EventType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Event() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.repeatWeekly = false;
        this.repeatMonthly = false;
    }

    public Event(String id, String petId, String title, String description, Date date, EventType type, boolean repeatWeekly, boolean repeatMonthly) {
        this.id = id;
        this.petId = petId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.repeatWeekly = repeatWeekly;
        this.repeatMonthly = repeatMonthly;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
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
} 