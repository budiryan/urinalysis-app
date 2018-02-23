package com.example.urinalysis.urinalysis.models;

/**
 * Created by budiryan on 2/23/18.
 */

public class Category {
    private Integer id;
    private String name;

    public Category(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
