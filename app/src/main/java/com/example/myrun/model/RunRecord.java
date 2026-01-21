package com.example.myrun.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RunRecord {
    private String id;
    private String username;
    private double distance;
    private long time;
    private int calories;
    private double pace;
    private long timestamp;
    private String date;

    public RunRecord() {
    }

    public RunRecord(String username, double distance, long time, int calories) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.username = username;
        this.distance = distance;
        this.time = time;
        this.calories = calories;
        this.pace = time > 0 && distance > 0 ? (time / 60.0 / distance) : 0;
        this.timestamp = System.currentTimeMillis();
        this.date = formatDate(timestamp);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 格式化时长为 HH:mm:ss
     */
    public String getFormattedTime() {
        long hours = time / 3600;
        long minutes = (time % 3600) / 60;
        long seconds = time % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * 格式化配速为 mm:ss/km
     */
    public String getFormattedPace() {
        if (pace <= 0) return "--:--";
        long paceMin = (long) pace;
        long paceSec = (long) ((pace - paceMin) * 60);
        return String.format(Locale.getDefault(), "%02d:%02d", paceMin, paceSec);
    }
}