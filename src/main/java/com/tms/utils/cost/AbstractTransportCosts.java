package com.tms.utils.cost;

import com.tms.api.Point;

public abstract class AbstractTransportCosts implements TransportCosts{
    @Override
    public abstract double getDistance(Point from, Point to, double departureTime, double perDistanceUnit);

    @Override
    public abstract double getTransportTime(Point from, Point to, double departureTime, double speed);

    @Override
    public abstract double getTransportCost(Point from, Point to, double departureTime, double perDistanceUnit);

}
