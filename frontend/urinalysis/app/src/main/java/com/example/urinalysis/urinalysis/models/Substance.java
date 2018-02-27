package com.example.urinalysis.urinalysis.models;

/**
 * Created by budiryan on 2/23/18.
 */

public class Substance {
    private Integer id;
    private Integer unit;
    private String unit_name;
    private Integer category;
    private String category_name;
    private Integer value;
    private String record_date;
    private String record_time;
    private String notes;

    public Substance(Integer id, Integer unit, String unitName, Integer category,
                     String categoryName, Integer value,
                     String recordDate, String recordTime, String notes) {
        this.id = id;
        this.value = value;
        this.unit = unit;
        this.unit_name = unitName;
        this.category = category;
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

    public String getRecordDate() {return record_date;}

    public String getRecordTime() {
        return record_time;
    }

    public String getNotes() {return notes;}

    public Integer getCategory() {return category;}

    public Integer getUnit() {return unit;}

    public String getUnit_name() {return unit_name;}

    public String getCategory_name() {return category_name;}

    public String getRecord_date() {return record_date;}

    public String getRecord_time() {return record_time;}

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    public void setRecord_time(String record_time) {
        this.record_time = record_time;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
