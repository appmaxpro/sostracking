package com.tms.utils.cost;

import com.tms.api.Point;

public class CircleDistanceCalculator implements DistanceCalculator{
    private static final double R = 6372.8; // km

    /**
     * Harversine method.
     * <p>
     * double lon1 = p1.getX();
     * double lon2 = p2.getX();
     * double lat1 = p1.getY();
     * double lat2 = p2.getY();
     *
     * @param p1 - from coord
     * @param p2 - to coord
     * @return great circle distance
     */
    @Override
    public double calculateDistance(Point p1, Point p2) {
        double lon1 = p1.getLat();
        double lon2 = p2.getLat();
        double lat1 = p1.getLng();
        double lat2 = p2.getLng();

        double delta_Lat = Math.toRadians(lat2 - lat1);
        double delta_Lon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(delta_Lat / 2) * Math.sin(delta_Lat / 2) + Math.sin(delta_Lon / 2) * Math.sin(delta_Lon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distance = R * c;

        return distance;
    }
}
