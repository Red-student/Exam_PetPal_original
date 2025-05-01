package com.example.exam_petpal.models;

import java.util.Date;

public class PremiumSubscription {
    private String userId;
    private boolean isActive;
    private Date startDate;
    private Date endDate;
    private String subscriptionType; // "monthly", "yearly"
    private double price;

    public PremiumSubscription() {
    }

    public PremiumSubscription(String userId, boolean isActive, Date startDate, Date endDate, String subscriptionType, double price) {
        this.userId = userId;
        this.isActive = isActive;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subscriptionType = subscriptionType;
        this.price = price;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
} 