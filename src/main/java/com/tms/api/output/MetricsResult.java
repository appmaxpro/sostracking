package com.tms.api.output;

import com.tms.api.Point;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

public class MetricsResult {
    public static final MetricsResult EMPTY_METRICS = new MetricsResult(0L, 0L, 0L, Collections.EMPTY_LIST);
    // distance in meter
    private Long distance;
    // duration in secound
    private Long duration;
    private Long trafficDuration;
    private List<Point> directions;
    private String origin;
    private String dest;
    private BigDecimal fareValue;
    private Currency fareCurrency;
    private long timeMillis;

    public MetricsResult(Long distance, Long duration, Long trafficDuration, List<Point> directions){
        this.distance = distance;
        this.duration = duration;
        this.trafficDuration = trafficDuration;
        this.directions = directions;
    }

    public MetricsResult(Long distance, Long duration, Long trafficDuration,
                         List<Point> directions, String origin, String dest,
                         BigDecimal fareValue, Currency fareCurrency) {
        this.distance = distance;
        this.duration = duration;
        this.trafficDuration = trafficDuration;
        this.directions = directions;
        this.origin = origin;
        this.dest = dest;
        this.fareValue = fareValue;
        this.fareCurrency = fareCurrency;
        timeMillis = System.currentTimeMillis();
    }

    public Long getDistance() {
        return distance;
    }

    public Long getDuration() {
        return duration;
    }

    public Long getTrafficDuration() {
        return trafficDuration;
    }

    public List<Point> getDirections() {
        return directions;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDest() {
        return dest;
    }

    public BigDecimal getFareValue() {
        return fareValue;
    }

    public Currency getFareCurrency() {
        return fareCurrency;
    }

    public long getTimeMillis() {
        return timeMillis;
    }
}
