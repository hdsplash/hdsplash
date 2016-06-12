package com.binhnv.hdsplash.models;

import java.io.Serializable;

/**
 * Created by sev_user on 1/27/2016.
 */
public class User implements Serializable {
    private String name;
    private String id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
