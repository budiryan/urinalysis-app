package com.example.urinalysis.urinalysis.models;

/**
 * Created by budiryan on 2/23/18.
 */

public class Substance {
    private Integer id;
    private String unit_name;
    private Integer value;
    private String category_name;
    private String record_date;
    private String record_time;
    private String notes;

    public Substance(Integer id, String unitName, Integer value, String categoryName,
                     String recordDate, String recordTime, String notes) {
        this.id = id;
        this.unit_name = unitName;
        this.value = value;
        this.category_name = categoryName;
        this.record_date = recordDate;
        this.record_time = recordTime;
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public String getUnitName() {
        return unit_name;
    }

    public Integer getValue() {
        return value;
    }

    public String getCategoryName() {
        return category_name;
    }

    public String getRecordDate() {
        return record_date;
    }

    public String getRecordTime() {
        return record_time;
    }

    public String getNotes() {return notes;}
}
