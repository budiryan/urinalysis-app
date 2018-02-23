package com.example.urinalysis.urinalysis.models;

/**
 * Created by budiryan on 2/23/18.
 */

public class AveragesPerDay {
    private Float[] Values;
    private String[] Dates;

    public AveragesPerDay(Float[] values, String[] dates) {
        Values = values;
        Dates = dates;
    }
}
