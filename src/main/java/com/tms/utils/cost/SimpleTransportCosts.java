package com.tms.utils.cost;

import com.tms.api.Point;

/**
 * @author stefan schroeder
 */
public class SimpleTransportCosts extends AbstractTransportCosts {

    public int speed = 1;

    public double detourFactor = 1.0;

    private DistanceCalculator distanceCalculator;

    public SimpleTransportCosts(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public String toString() {
        return "[name=crowFlyCosts]";
    }

    @Override
    public double getTransportCost(Point from, Point to, double time, double perDistanceUnit) {
        double distance = calculateDistance(from, to);

        return distance * perDistanceUnit;
    }

    double calculateDistance(Point from, Point to) {
        try {
            return distanceCalculator.calculateDistance(from, to) * detourFactor;
        } catch (NullPointerException e) {
            throw new NullPointerException("cannot calculate euclidean distance. coordinates are missing. either add coordinates or use another transport-cost-calculator.");
        }
    }

    @Override
    public double getTransportTime(Point from, Point to, double time, double speed) {
        return calculateDistance(from, to) / speed;
    }

    @Override
    public double getDistance(Point from, Point to, double departureTime, double perDistanceUnit) {
        return calculateDistance(from, to);
    }
}