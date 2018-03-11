package com.example.urinalysis.urinalysis.models;

/**
 * Created by Budi Ryan on 11-Mar-18.
 */

public class User {
    private Integer id;
    private String name;

    public User(Integer id, String name) {
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