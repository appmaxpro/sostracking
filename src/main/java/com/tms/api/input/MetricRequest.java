package com.tms.api.input;

import com.tms.api.Point;

import java.io.Serializable;
import java.util.List;

public class MetricRequest implements Serializable {


    private List<Point> points;
    private Boolean directions;
    private Boolean traffic;
    private Boolean addresses;
    private String mode; //DRIVING|WALKING|BICYCLING|TRANSIT

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Boolean getDirections() {
        return directions;
    }

    public void setDirections(Boolean directions) {
        this.directions = directions;
    }

    public Boolean getTraffic() {
        return traffic;
    }

    public void setTraffic(Boolean traffic) {
        this.traffic = traffic;
    }

    public Boolean getAddresses() {
        return addresses;
    }

    public void setAddresses(Boolean addresses) {
        this.addresses = addresses;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
