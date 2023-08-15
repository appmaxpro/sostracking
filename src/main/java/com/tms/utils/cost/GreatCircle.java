package com.tms.utils.cost;

import com.tms.api.Point;

import java.util.Objects;

/**
 * A great circle, also known as an orthodrome, of a sphere is the intersection of the sphere and
 * a plane that passes through the center point of the sphere. A great circle is the largest circle
 * that can be drawn on any given sphere. (Wikipedia)
 * This library contains functions about great circle for calculation, bearings, distances or midpoints.
 * All these functions are taken from Chris Veness(https://github.com/chrisveness)'s amazing work
 * Geodesy Functions(https://github.com/chrisveness/geodesy) and ported to Kotlin.
 * Some of comments are copied from original library
 */
class GreatCircle {
    private static final double R = 6372.8;
    
    /**
     * Haversine formula. Giving great-circle distances between two points on a sphere from their longitudes and latitudes.
     * It is a special case of a more general formula in spherical trigonometry, the law of haversines, relating the
     * sides and angles of spherical "triangles".
     *
     * https://rosettacode.org/wiki/Haversine_formula#Java
     * Based on https://gist.github.com/jferrao/cb44d09da234698a7feee68ca895f491
     * @param startPoint Initial coordinates
     * @param endPoint Final coordinates
     * @return Distance in kilometers
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var jfkPoints = Point(40.63980103, -73.77890015) // The coordinates of New York JFK Airport
     * var greatCircle = GreatCircle()
     * var distance = greatCircle.distance(istPoints, jfkPoints)
     */
    public static double distance(Point startPoint, Point endPoint) {
        double dLat = Math.toRadians(endPoint.getLat() - startPoint.getLat());
        double dLon = Math.toRadians(endPoint.getLng() - startPoint.getLng());
        double originLat = Math.toRadians(startPoint.getLat());
        double destinationLat = Math.toRadians(endPoint.getLat());
        double a = Math.pow(Math.sin(dLat / 2.0), 2.0) + Math.pow(Math.sin(dLon / 2.0), 2.0) * Math.cos(originLat) * Math.cos(destinationLat);
        double c = 2.0 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    /**
     * Haversine formula. Giving great-circle distances between two points on a sphere from their longitudes and latitudes.
     * It is a special case of a more general formula in spherical trigonometry, the law of haversines, relating the
     * sides and angles of spherical "triangles".
     *
     * https://rosettacode.org/wiki/Haversine_formula#Java
     *
     * @param startPoint Initial coordinates
     * @param endPoint Final coordinates
     * @return Distance in nautical miles
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var jfkPoints = Point(40.63980103, -73.77890015) // The coordinates of New York JFK Airport
     * var greatCircle = GreatCircle()
     * var distance = greatCircle.distanceInNm(istPoints, jfkPoints)
     */
    public static double distanceInNm(Point startPoint, Point endPoint) {
        return distance(startPoint, endPoint) * 0.539956803;
    }

    /**
     * This function is for the initial bearing (sometimes referred to as forward azimuth) which if
     * followed in a straight line along a great-circle arc will take you from the start point to the end point
     *
     *
     * @param startPoint Initial coordinates
     * @param endPoint Final coordinates
     * @return Bearing in degrees from North, 0° ... 360°
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var jfkPoints = Point(40.63980103, -73.77890015) // The coordinates of New York JFK Airport
     * var greatCircle = GreatCircle()
     * var bearing = greatCircle.bearing(istPoints, jfkPoints)
     */
    public static double bearing(Point startPoint, Point endPoint) {
        if (Objects.equals(startPoint, endPoint)) {
            return 0.0;
        } else {
            double deltaLon = endPoint.getLngR() - startPoint.getLngR();
            double y = Math.sin(deltaLon) * Math.cos(endPoint.getLatR());
            double x = Math.cos(startPoint.getLatR()) * Math.sin(endPoint.getLatR())
                    - Math.sin(startPoint.getLatR()) * Math.cos(endPoint.getLatR()) * Math.cos(deltaLon);
            double phi = Math.atan2(y, x);
            return wrap360(Math.toDegrees(phi));
        }
    }

    /**
     * This function returns final bearing arriving at destination point from startPoint; the final
     * bearing will differ from the initial bearing by varying degrees according to distance and latitude
     *
     *
     * @param startPoint Initial coordinates
     * @param endPoint Final coordinates
     * @return Bearing in degrees from North, 0° ... 360°
     */
    public static double finalBearing(Point startPoint, Point endPoint) {
        double bearing = bearing(startPoint, endPoint) + 180.0;
        return wrap360(bearing);
    }

    /**
     * This function calculates the midpoint between startPoint and endPoint
     *
     * @param startPoint Initial coordinates
     * @param endPoint Final coordinates
     * @return Midpoint coordinates
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var jfkPoints = Point(40.63980103, -73.77890015) // The coordinates of New York JFK Airport
     * var greatCircle = GreatCircle()
     * var midpoint = greatCircle.midpoint(istPoints, jfkPoints)
     */
    public static Point midpoint(Point startPoint, Point endPoint) {
        double deltaLon = endPoint.getLng() - startPoint.getLng();
        double bx = Math.cos(endPoint.getLat()) * Math.cos(deltaLon);
        double by = Math.cos(endPoint.getLat()) * Math.sin(deltaLon);
        double lat = Math.atan2(Math.sin(startPoint.getLat()) + Math.sin(endPoint.getLat()), 
                Math.sqrt((Math.cos(startPoint.getLat()) + bx) * (Math.cos(startPoint.getLat()) 
                        + bx) + by * by));
        double lon = startPoint.getLng() + Math.atan2(by, Math.cos(startPoint.getLat()) + bx);
        return new Point(lat, lon);
    }

    /**
     * This function returns the point at given fraction between startPoint and endPoint
     *
     * @param startPoint Initial coordinates
     * @param endPoint Final coordinates
     * @param fraction Fraction between coordinates
     * @return Intermediate coordinates between startPoint and endPoint
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var jfkPoints = Point(40.63980103, -73.77890015) // The coordinates of New York JFK Airport
     * var fraction = 0.25
     * var intermediate = greatCircle.intermediate(istPoints, jfkPoints, fraction)
     */
    public static Point intermediate(Point startPoint, Point endPoint, double fraction) {
        if (Objects.equals(startPoint, endPoint)) {
            return startPoint;
        } else {
            double deltaLat = endPoint.getLatR() - startPoint.getLatR();
            double deltaLon = endPoint.getLngR() - startPoint.getLngR();
            double a = Math.sin(deltaLat / 2.0) * Math.sin(deltaLat / 2.0) + Math.cos(startPoint.getLatR()) * Math.cos(endPoint.getLatR()) * Math.sin(deltaLon / 2.0) * Math.sin(deltaLon / 2.0);
            double δ = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
            double A = Math.sin((1.0 - fraction) * δ) / Math.sin(δ);
            double B = Math.sin(fraction * δ) / Math.sin(δ);
            double x = A * Math.cos(startPoint.getLatR()) * Math.cos(startPoint.getLngR()) + B * Math.cos(endPoint.getLatR()) * Math.cos(endPoint.getLngR());
            double y = A * Math.cos(startPoint.getLatR()) * Math.sin(startPoint.getLngR()) + B * Math.cos(endPoint.getLatR()) * Math.sin(endPoint.getLngR());
            double z = A * Math.sin(startPoint.getLatR()) + B * Math.sin(endPoint.getLatR());
            double lat = Math.atan2(z, Math.sqrt(x * x + y * y));
            double lon = Math.atan2(y, x);
            return new Point(lat, lon);
        }
    }

    /**
     * This function returns the point of intersection of two paths which one starts from firstPoint
     * with firstBearing and the other one starts from secondPoint with secondBearing
     *
     * @param firstPoint First point coordinates
     * @param firstBearing First path's bearing
     * @param secondPoint Second point coordinates
     * @param secondBearing Second path's bearing
     * @return Intersections coordinates of two paths or return null if there is no intersection
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var fcoPoints = Point(41.8002778,12.2388889) // The coordinates of Roma Fiumicino Airport
     * var bearingFromIstanbulToWest : Double = 270.0
     * var bearingFromRomeToNorthEast : Double = 45.0
     * var intersection = greatCircle.intersection(istPoints, bearingFromIstanbulToWest, fcoPoints, bearingFromRomeToNorthEast)
     *  */
    public static Point intersection(Point firstPoint, double firstBearing, Point secondPoint, double secondBearing) {
        double deltaLat = secondPoint.getLatR() - firstPoint.getLatR();
        double deltaLon = secondPoint.getLngR() - firstPoint.getLngR();
        double delta12 = 2.0 * Math.asin(Math.sqrt(Math.sin(deltaLat / 2.0) * Math.sin(deltaLat / 2.0) + Math.cos(firstPoint.getLatR()) * Math.cos(secondPoint.getLatR()) * Math.sin(deltaLon / 2.0) * Math.sin(deltaLon / 2.0)));
        if (Math.abs(delta12) < Math.E) {
            return firstPoint;
        } else {
            double cosTetaa = (Math.sin(secondPoint.getLatR()) - Math.sin(firstPoint.getLatR()) * Math.cos(delta12)) / (Math.sin(delta12) * Math.cos(firstPoint.getLatR()));
            double cosTetab = (Math.sin(firstPoint.getLatR()) - Math.sin(secondPoint.getLatR()) * Math.cos(delta12)) / (Math.sin(delta12) * Math.cos(secondPoint.getLatR()));
            double tetaa = Math.acos((double)Math.min(Math.max((int)cosTetaa, -1), 1));
            double tetab = Math.acos((double)Math.min(Math.max((int)cosTetab, -1), 1));
            double teta12 = Math.sin(secondPoint.getLngR() - firstPoint.getLngR()) > 0.0 ? tetaa : 6.283185307179586 - tetaa;
            double teta21 = Math.sin(secondPoint.getLngR() - firstPoint.getLngR()) > 0.0 ? 6.283185307179586 - tetab : tetab;
            double alpha1 = Math.toRadians(firstBearing) - teta12;
            double alpha2 = teta21 - Math.toRadians(secondBearing);
            if (Math.sin(alpha1) == 0.0 && Math.sin(alpha2) == 0.0) {
                return null;
            } else if (Math.sin(alpha1) * Math.sin(alpha2) < 0.0) {
                return null;
            } else {
                double cosalpha3 = -Math.cos(alpha1) * Math.cos(alpha2) + Math.sin(alpha1) * Math.sin(alpha2) * Math.cos(delta12);
                double delta13 = Math.atan2(Math.sin(delta12) * Math.sin(alpha1) * Math.sin(alpha2), Math.cos(alpha2) + Math.cos(alpha1) * cosalpha3);
                double lat = Math.asin(Math.min(Math.max(Math.sin(firstPoint.getLatR()) * Math.cos(delta13) + Math.cos(firstPoint.getLatR()) * Math.sin(delta13) * Math.cos(Math.toRadians(firstBearing)), -1.0), 1.0));
                double deltaLon13 = Math.atan2(Math.sin(Math.toRadians(firstBearing)) * Math.sin(delta13) * Math.cos(firstPoint.getLatR()), Math.cos(delta13) - Math.sin(firstPoint.getLatR()) * Math.sin(lat));
                double lon = firstPoint.getLngR() + deltaLon13;
                return new Point(lat, lon);
            }
        }
    }

    /**
     * This function calculates the destination point and final bearing travelling along a
     * (shortest distance) great circle arc for a given start point, initial bearing and distance
     *
     *
     * @param startPoint Initial coordinates
     * @param distance Distance on great circle
     * @param bearing Bearing
     * @return Destination coordinates
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var jfkPoints = Point(40.63980103, -73.77890015) // The coordinates of New York JFK Airport
     * var greatCircle = GreatCircle()
     * var bearing = greatCircle.bearing(istPoints, jfkPoints)
     *
     * var distance = 1000 // km
     * var destination = greatCircle.destination(istPoints, distance, bearing) // The coordinates of point which is at 1000th km great circle between Istanbul Airport and JFK Airport
     *
     */
    public static Point destination(Point startPoint, double distance, double bearing) {
        
        double sinLat = Math.sin(startPoint.getLatR()) * Math.cos(distance / R) + Math.cos(startPoint.getLatR()) * Math.sin(distance / R) * Math.cos(Math.toDegrees(bearing));
        double lat = Math.asin(sinLat);
        double y = Math.sin(Math.toRadians(bearing)) * Math.sin(distance / R) * Math.cos(startPoint.getLatR());
        double x = Math.cos(distance / R) - Math.sin(startPoint.getLatR()) * sinLat;
        double lon = startPoint.getLngR() + Math.atan2(y, x);
        return new Point(Math.toDegrees(lat), Math.toDegrees(lon));
    }

    /**
     * This function returns distance from currentPoint to great circle between startPoint and endPoint
     *
     * @param currentPoint The point whose distance is wondering to great circle
     * @param startPoint Start of the great circle
     * @param endPoint End of the great circle
     * @return Distance to the great circle. If returns positive this means right of the path, otherwise it means left of the path.
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var jfkPoints = Point(40.63980103, -73.77890015) // The coordinates of New York JFK Airport
     * var fcoPoints = Point(41.8002778,12.2388889) // The coordinates of Roma Fiumicino Airport
     * var crossTrackDistanceInKm = greatCircle.crossTrackDistance(fcoPoints, istPoints, jfkPoints)
     */
    public static double crossTrackDistance(Point currentPoint, Point startPoint, Point endPoint) {
        if (Objects.equals(currentPoint, startPoint)) {
            return 0.0;
        } else {
            double delta13 = distance(startPoint, currentPoint) / R;
            double teta13 = Math.toRadians(bearing(startPoint, currentPoint));
            double teta12 = Math.toRadians(bearing(startPoint, endPoint));
            double deltaCrossTrack = Math.asin(Math.sin(delta13) * Math.sin(teta13 - teta12));
            return deltaCrossTrack * R;
        }
    }

    /**
     * This function returns how far currentPoint is along a path from from startPoint, heading towards endPoint.
     * That is, if a perpendicular is drawn from currentPoint point to the (great circle) path, the
     * along-track distance is the distance from the start point to where the perpendicular crosses the path.
     *
     *
     * @param currentPoint The point whose distance is wondering to great circle
     * @param startPoint Start of the great circle
     * @param endPoint End of the great circle
     * @return Distance along great circle to point nearest currentPoint point.
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var jfkPoints = Point(40.63980103, -73.77890015) // The coordinates of New York JFK Airport
     * var fcoPoints = Point(41.8002778,12.2388889) // The coordinates of Roma Fiumicino Airport
     * var alongTrackDistanceTo = greatCircle.alongTrackDistanceTo(fcoPoints, istPoints, jfkPoints)
     *
     */
    public static double alongTrackDistanceTo(Point currentPoint, Point startPoint, Point endPoint) {
        if (Objects.equals(currentPoint, startPoint)) {
            return 0.0;
        } else {
            double delta13 = distance(startPoint, currentPoint) / R;
            double teta13 = Math.toRadians(bearing(startPoint, currentPoint));
            double teta12 = Math.toRadians(bearing(startPoint, endPoint));
            double deltaCrossTrack = Math.asin(Math.sin(delta13) * Math.sin(teta13 - teta12));
            double deltaAlongTrack = Math.acos(Math.cos(delta13) / Math.abs(Math.cos(deltaCrossTrack)));
            return deltaAlongTrack * Math.signum(Math.cos(teta12 - teta13)) * R;
        }
    }

    /**
     * This function returns maximum latitude reached when travelling on a great circle on given bearing from
     * startPoint point (‘Clairaut’s formula’). Negate the result for the minimum latitude (in the
     * southern hemisphere).
     *
     * The maximum latitude is independent of longitude; it will be the same for all points on a
     * given latitude.
     *
     * @param startPoint Initial coordinates
     * @param bearing Bearing
     * @return Destination coordinates
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var bearingFromIstanbulToWest : Double = 270.0
     * var maxLatitude = greatCircle.maxLatitude(istPoints, bearingFromIstanbulToWest)
     */
    public static double maxLatitude(Point startPoint, double bearing) {
        double bearingInRad = Math.toRadians(bearing);
        double maxLat = Math.acos(Math.abs(Math.sin(bearingInRad) * Math.cos(startPoint.getLatR())));
        return Math.toDegrees(maxLat);
    }

    /**
     * This function returns the pair of meridians at which a great circle defined by two points crosses the given
     * latitude. If the great circle doesn't reach the given latitude, null is returned.
     *
     * The maximum latitude is independent of longitude; it will be the same for all points on a
     * given latitude.
     *
     * @param startPoint Initial coordinates
     * @param endPoint Final coordinates
     * @param latitude Latitude crossings are to be determined for.
     * @return Array containing { lon1, lon2 } or null if given latitude not reached.
     *
     * @sample
     * var istPoints = Point(41.28111111, 28.75333333) // The coordinates of Istanbul Airport
     * var jfkPoints = Point(40.63980103, -73.77890015) // The coordinates of New York JFK Airport
     * var latitude : Double = 60.0 // means 60 degrees north
     * var crossingParallels = greatCircle.crossingParallels(istPoints, jfkPoints, latitude)
     */
    public static double[] crossingParallels(Point startPoint, Point endPoint, double latitude) {
        double latR = Math.toRadians(latitude);
        double deltaLon = endPoint.getLngR() - startPoint.getLngR();
        double x = Math.sin(startPoint.getLatR()) * Math.cos(endPoint.getLatR()) * Math.cos(latR) * Math.sin(deltaLon);
        double y = Math.sin(startPoint.getLatR()) * Math.cos(endPoint.getLatR()) * Math.cos(latR) * Math.cos(deltaLon) - Math.cos(startPoint.getLatR()) * Math.sin(endPoint.getLatR()) * Math.cos(latR);
        double z = Math.cos(startPoint.getLatR()) * Math.cos(endPoint.getLatR()) * Math.sin(latR) * Math.sin(deltaLon);
        if (z * z > x * x + y * y) {
            return null;
        } else {
            double deltaM = Math.atan2(-y, x);
            double deltaLoni = Math.acos(z / Math.sqrt(x * x + y * y));
            double deltaI1 = startPoint.getLngR() + deltaM - deltaLoni;
            double deltaI2 = startPoint.getLngR() + deltaM + deltaLoni;
            double lon1 = Math.toDegrees(deltaI1);
            double lon2 = Math.toDegrees(deltaI2);
            return new double[]{wrap180(lon1), wrap180(lon2)};
        }
    }

    public static double wrap360(double input) {
        return 0.0 <= input && input < 360.0? input : (input % 360.0 + 360.0) % 360.0;
    }

    public static double wrap180(double input) {
        return -180.0 < input && input <= 180.0 ? input : (input + 540.0) % 360.0 - 180.0;
    }

}