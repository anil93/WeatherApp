package com.anilaynaci.weatherapp.entities;

import java.io.Serializable;

/**
 * Created by anila on 29.10.2017.
 */

public class Wind implements Serializable {
    private double speed;
    private double deg;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDeg() {
        return deg;
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }
}
