package com.example.urinalysis.urinalysis.models;

import java.util.List;

/**
 * Created by Budi Ryan on 11-Mar-18.
 */

public class UserCategory {
    private String[] users;
    private String[] categories;

    public UserCategory(String[] users, String[] categories) {
        this.users = users;
        this.categories = categories;
    }

    public String[] getUsers() {
        return users;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }
}
