package com.binhnv.hdsplash.models;

import java.io.Serializable;

/**
 * Created by sev_user on 1/27/2016.
 */
public class Link implements Serializable  {
    // private String self;
    public String full;
    private String regular;
    private String small;

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }
}

