package com.example.urinalysis.urinalysis.models;

/**
 * Created by budiryan on 2/23/18.
 */

public class Unit {
    private Integer id;
    private String name;
    private Integer category;
    private String category_name;

    public Unit(Integer id, String name, Integer category, String categoryName) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.category_name = categoryName;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategoryName(){
        return category_name;
    }

    public Integer getCategory(){
        return category;
    }

}
