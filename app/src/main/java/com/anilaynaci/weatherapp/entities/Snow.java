package com.anilaynaci.weatherapp.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by anila on 29.10.2017.
 */

public class Snow implements Serializable{

    @JsonProperty("3h")
    private Double threeHour;

    public Double getThreeHour() {
        return threeHour;
    }

    public void setThreeHour(Double threeHour) {
        this.threeHour = threeHour;
    }
}
