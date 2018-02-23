package com.example.urinalysis.urinalysis.models;

/**
 * Created by budiryan on 2/23/18.
 */

public class Substance {
    private Integer id;
    private String unitName;
    private Integer value;
    private String categoryName;
    private String recordDate;
    private String recordTime;

    public Substance(Integer id, String unitName, Integer value, String categoryName, String recordDate, String recordTime) {
        this.id = id;
        this.unitName = unitName;
        this.value = value;
        this.categoryName = categoryName;
        this.recordDate = recordDate;
        this.recordTime = recordTime;
    }
}
