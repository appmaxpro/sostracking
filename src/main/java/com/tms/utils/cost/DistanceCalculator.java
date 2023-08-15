package com.tms.utils.cost;

import com.tms.api.Point;

public interface DistanceCalculator {
    double calculateDistance(Point coord1, Point coord2);
}
