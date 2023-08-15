package com.tms.utils.cost;

import com.tms.api.Point;

public interface TransportCosts {

    double getDistance(Point from, Point to, double departureTime, double perDistanceUnit);

    double getTransportTime(Point from, Point to, double departureTime, double speed);

    double getTransportCost(Point from, Point to, double departureTime, double perDistanceUnit);
}
