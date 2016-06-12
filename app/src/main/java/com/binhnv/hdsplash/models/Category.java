package com.binhnv.hdsplash.models;

import java.io.Serializable;

/**
 * Created by sev_user on 1/27/2016.
 */
public class Category implements Serializable {

    private String title;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}