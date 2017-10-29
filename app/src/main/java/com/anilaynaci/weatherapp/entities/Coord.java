package com.anilaynaci.weatherapp.entities;

import java.io.Serializable;

/**
 * Created by anila on 29.10.2017.
 */

public class Coord implements Serializable {
    private double lat;
    private double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
