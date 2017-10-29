package com.anilaynaci.weatherapp.entities;

import java.io.Serializable;

/**
 * Created by anila on 29.10.2017.
 */

public class Sys implements Serializable {
    private String pod;

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }
}
