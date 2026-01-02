package com.pixelquest.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PlayerLevel {

    @Column(name = "total_score")
    private int totalScore = 0;

    @Column(name = "current_rank") // Renamed to avoid SQL keyword issues
    private String level = "Beginner";

    public PlayerLevel() {}

    public PlayerLevel(int totalScore) {
        this.totalScore = totalScore;
        this.level = computeRank(totalScore);
    }


    public int getTotalScore() { return totalScore; }
    public String getLevel() { return level; }


    public void addScore(int score) {
        this.totalScore += score;
        this.level = computeRank(this.totalScore);
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
        this.level = computeRank(totalScore);
    }


    public void setLevel(String level) {
        this.level = level;
    }

    private String computeRank(int score) {
        if (score < 500) return "Beginner";
        if (score < 2000) return "Intermediate";
        if (score < 5000) return "Advanced";
        if (score < 10000) return "Expert";
        return "Pro";
    }
}