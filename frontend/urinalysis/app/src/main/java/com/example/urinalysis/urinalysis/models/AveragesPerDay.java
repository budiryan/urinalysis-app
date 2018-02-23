package com.example.urinalysis.urinalysis.models;

/**
 * Created by budiryan on 2/23/18.
 */

public class AveragesPerDay {
    private Float[] values;
    private String[] dates;

    public AveragesPerDay(Float[] values, String[] dates) {
        values = values;
        dates = dates;
    }

    public Float[] getValues() {
        return values;
    }

    public String[] getDates() {
        return dates;
    }
}
