package com.example.myrun.model;

public class RankingRecord {
    private String username;
    private int totalRuns;
    private double totalDistance;
    private long totalTime;

    public RankingRecord(String username, int totalRuns, double totalDistance, long totalTime) {
        this.username = username;
        this.totalRuns = totalRuns;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
    }

    public String getUsername() {
        return username;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public long getTotalTime() {
        return totalTime;
    }
}