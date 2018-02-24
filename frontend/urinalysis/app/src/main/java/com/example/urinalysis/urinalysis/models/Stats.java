package com.example.urinalysis.urinalysis.models;

/**
 * Created by budiryan on 2/24/18.
 */

public class Stats {
    private Float avg;
    private Float std;
    private Integer latest;
    private String latestDate;
    private String latestTime;
    private Integer highest;
    private String highestDate;
    private String highestTime;
    private Integer lowest;
    private String lowestDate;
    private String lowestTime;
    private String unit;

    public Stats(Float avg, Float std, Integer latest, String latestDate,
                 String latestTime, Integer highest, String highestDate,
                 String highestTime, Integer lowest, String lowestDate,
                 String lowestTime, String unit) {
        this.avg = avg;
        this.std = std;
        this.latest = latest;
        this.latestDate = latestDate;
        this.latestTime = latestTime;
        this.highest = highest;
        this.highestDate = highestDate;
        this.highestTime = highestTime;
        this.lowest = lowest;
        this.lowestDate = lowestDate;
        this.lowestTime = lowestTime;
        this.unit = unit;
    }

    public Float getAvg() {
        return avg;
    }

    public Float getStd() {
        return std;
    }

    public Integer getLatest() {
        return latest;
    }

    public String getLatestDate() {
        return latestDate;
    }

    public String getLatestTime() {
        return latestTime;
    }

    public Integer getHighest() {
        return highest;
    }

    public String getHighestDate() {
        return highestDate;
    }

    public String getHighestTime() {
        return highestTime;
    }

    public Integer getLowest() {
        return lowest;
    }

    public String getLowestDate() {
        return lowestDate;
    }

    public String getLowestTime() {
        return lowestTime;
    }

    public String getUnit() {
        return unit;
    }
}
