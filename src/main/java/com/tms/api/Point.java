package com.tms.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class Point implements Serializable {
    private double lat;
    private double lng;

    public Point(){}

    public Point(double x, double y) {
        this.lat = x;
        this.lng = y;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @JsonIgnore
    public double getLatR() {
        return Math.toRadians(lat);
    }

    @JsonIgnore
    public double getLngR() {
        return Math.toRadians(lng);
    }
}
