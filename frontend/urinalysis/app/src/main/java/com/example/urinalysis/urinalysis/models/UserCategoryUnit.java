package com.example.urinalysis.urinalysis.models;

import java.util.List;

/**
 * Created by Budi Ryan on 13-Mar-18.
 */

public class UserCategoryUnit {
    private List<User> users;
    private List<Unit> units;
    private List<Category> categories;

    public UserCategoryUnit(List<User> users, List<Unit> units, List<Category> categories) {
        this.users = users;
        this.units = units;
        this.categories = categories;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
