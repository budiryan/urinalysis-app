package com.example.urinalysis.urinalysis.models;

/**
 * Created by budiryan on 2/24/18.
 */

public class Stats {
    private Float avg;
    private Float std;
    private Float latest;
    private String latest_date;
    private String latest_time;
    private Float highest;
    private String highest_date;
    private String highest_time;
    private Float lowest;
    private String lowest_date;
    private String lowest_time;
    private String unit;


    public Stats(Float avg, Float std, Float latest, String latest_date, String latest_time,
                 Float highest, String highest_date, String highest_time, Float lowest,
                 String lowest_date, String lowest_time, String unit) {
        this.avg = avg;
        this.std = std;
        this.latest = latest;
        this.latest_date = latest_date;
        this.latest_time = latest_time;
        this.highest = highest;
        this.highest_date = highest_date;
        this.highest_time = highest_time;
        this.lowest = lowest;
        this.lowest_date = lowest_date;
        this.lowest_time = lowest_time;
        this.unit = unit;
    }

    public Float getAvg() {
        return avg;
    }

    public Float getStd() {
        return std;
    }

    public Float getLatest() {
        return latest;
    }

    public String getLatest_date() {
        return latest_date;
    }

    public String getLatest_time() {
        return latest_time;
    }

    public Float getHighest() {
        return highest;
    }

    public String getHighest_date() {
        return highest_date;
    }

    public String getHighest_time() {
        return highest_time;
    }

    public Float getLowest() {
        return lowest;
    }

    public String getLowest_date() {
        return lowest_date;
    }

    public String getLowest_time() {
        return lowest_time;
    }

    public String getUnit() {
        return unit;
    }
}
