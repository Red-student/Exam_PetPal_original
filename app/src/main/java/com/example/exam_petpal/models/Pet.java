package com.example.exam_petpal.models;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Pet implements Serializable {
    private String id;
    private String name;
    private String type;
    private String breed;
    private Date birthDate;
    private double weight;
    private String gender;
    private String photoUri;
    private boolean isTrial;
    private boolean hidden;
    private List<Vaccine> vaccines;
    private List<Event> events;

    public Pet() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.hidden = false;
        this.vaccines = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public Pet(String name, String type, String breed, Date birthDate, double weight, String gender, String photoUri, boolean isTrial) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.birthDate = birthDate;
        this.weight = weight;
        this.gender = gender;
        this.photoUri = photoUri;
        this.isTrial = isTrial;
        this.hidden = false;
        this.vaccines = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public boolean isTrial() {
        return isTrial;
    }

    public void setTrial(boolean trial) {
        isTrial = trial;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public List<Vaccine> getVaccines() {
        return vaccines;
    }

    public void setVaccines(List<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }

    public void addVaccine(Vaccine vaccine) {
        this.vaccines.add(vaccine);
    }

    public void removeVaccine(Vaccine vaccine) {
        this.vaccines.remove(vaccine);
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void removeEvent(Event event) {
        this.events.remove(event);
    }
} 