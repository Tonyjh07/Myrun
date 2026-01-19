package com.example.myrun.model;

public class RunRecord {
    private String username;
    private double distance;
    private long time;
    private double calories;

    public RunRecord(String username, double distance, long time, double calories) {
        this.username = username;
        this.distance = distance;
        this.time = time;
        this.calories = calories;
    }

    public String getUsername() {
        return username;
    }

    public double getDistance() {
        return distance;
    }

    public long getTime() {
        return time;
    }

    public double getCalories() {
        return calories;
    }
}