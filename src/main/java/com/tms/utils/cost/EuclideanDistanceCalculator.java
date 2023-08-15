package com.tms.utils.cost;

import com.tms.api.Point;

public class EuclideanDistanceCalculator implements DistanceCalculator{
    private static final double R = 6372.8; // km
    @Override
    public double calculateDistance(Point p1, Point p2) {
        double xDiff = p1.getLat() - p2.getLat();
        double yDiff = p1.getLng() - p2.getLng();
        return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff)) * R;
    }

}
