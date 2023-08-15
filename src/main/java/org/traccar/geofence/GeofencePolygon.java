/*
 * Copyright 2016 - 2020 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.geofence;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.context.jts.JtsSpatialContextFactory;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.jts.JtsShapeFactory;
import org.traccar.config.Config;
import org.traccar.model.Geofence;

import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.locationtech.spatial4j.distance.DistanceUtils.DEG_TO_KM;

public class GeofencePolygon extends GeofenceGeometry {
    /*
     * Default length for xpoints and ypoints.
     */
    private static final int MIN_LENGTH = 4;
    private static final Rectangle EMPTY_RECT = new Rectangle();

    public GeofencePolygon() {
    }

    public GeofencePolygon(String wkt) throws ParseException {
        fromWkt(wkt);
    }

    //private ArrayList<Coordinate> coordinates;

    //private float[] constant;
    //private float[] multiple;

    /**
     * The total number of points.  The value of {@code npoints}
     * represents the number of valid points in this {@code Polygon}
     * and might be less than the number of elements in
     * {@link #xpoints xpoints} or {@link #ypoints ypoints}.
     * This value can be 0.
     *
     * @serial
     * @since 1.0
     */
    public int npoints;

    /**
     * The array of X coordinates.  The number of elements in
     * this array might be more than the number of X coordinates
     * in this {@code Polygon}.  The extra elements allow new points
     * to be added to this {@code Polygon} without re-creating this
     * array.  The value of {@link #npoints npoints} is equal to the
     * number of valid points in this {@code Polygon}.
     *
     * @serial
     * @since 1.0
     */
    public float xpoints[];

    /**
     * The array of Y coordinates.  The number of elements in
     * this array might be more than the number of Y coordinates
     * in this {@code Polygon}.  The extra elements allow new points
     * to be added to this {@code Polygon} without re-creating this
     * array.  The value of {@code npoints} is equal to the
     * number of valid points in this {@code Polygon}.
     *
     * @serial
     * @since 1.0
     */
    public float ypoints[];

    private Rectangle bounds;

    /*

    private boolean needNormalize = false;



    private float normalizeLon(float lon) {
        if (needNormalize && lon < -90) {
            return lon + 360;
        }
        return lon;
    }

     */

    @Override
    public boolean containsPoint(double latitude, double longitude, double distance){
        /*
        int polyCorners = coordinates.size();
        int i;
        int j = polyCorners - 1;
        float longitudeNorm = normalizeLon(longitude);
        boolean oddNodes = false;

        for (i = 0; i < polyCorners; j = i++) {
            if (normalizeLon(coordinates.get(i).getLon()) < longitudeNorm
                    && normalizeLon(coordinates.get(j).getLon()) >= longitudeNorm
                    || normalizeLon(coordinates.get(j).getLon()) < longitudeNorm
                    && normalizeLon(coordinates.get(i).getLon()) >= longitudeNorm) {
                oddNodes ^= longitudeNorm * multiple[i] + constant[i] < latitude;
            }
        }

         */
        return contains((float)latitude, (float)longitude);
    }

    public boolean contains(float x, float y) {
        if (npoints <= 2 || !getBounds().contains(x, y)) {
            return false;
        }
        int hits = 0;

        float lastx = xpoints[npoints - 1];
        float lasty = ypoints[npoints - 1];
        float curx, cury;

        // Walk the edges of the polygon
        for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
            curx = xpoints[i];
            cury = ypoints[i];

            if (cury == lasty) {
                continue;
            }

            float leftx;
            if (curx < lastx) {
                if (x >= lastx) {
                    continue;
                }
                leftx = curx;
            } else {
                if (x >= curx) {
                    continue;
                }
                leftx = lastx;
            }

            double test1, test2;
            if (cury < lasty) {
                if (y < cury || y >= lasty) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - curx;
                test2 = y - cury;
            } else {
                if (y < lasty || y >= cury) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - lastx;
                test2 = y - lasty;
            }

            if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
                hits++;
            }
        }

        return ((hits & 1) != 0);
    }

    @Override
    public double calculateArea() {
        JtsShapeFactory jtsShapeFactory = new JtsSpatialContextFactory().newSpatialContext().getShapeFactory();
        ShapeFactory.PolygonBuilder polygonBuilder = jtsShapeFactory.polygon();
        for (int i = 0; i < npoints; i++) {
            polygonBuilder.pointXY((double) ypoints[i], (double) xpoints[i]);
        }
        return polygonBuilder.build().getArea(SpatialContext.GEO) * DEG_TO_KM * DEG_TO_KM;
    }

    @Override
    public String toWkt() {
        StringBuilder buf = new StringBuilder();
        buf.append("POLYGON ((");
        for (int i = 0; i < npoints; i++) {
            buf.append((double) xpoints[i])
                .append(" ")
                .append((double) ypoints[i])
                .append(", ");
        }

        return buf.substring(0, buf.length() - 2) + "))";
    }

    @Override
    public void fromWkt(String wkt) throws ParseException {

        if (!wkt.startsWith("POLYGON")) {
            throw new ParseException("Mismatch geometry type", 0);
        }
        String content = wkt.substring(wkt.indexOf("((") + 2, wkt.indexOf("))"));
        if (content.isEmpty()) {
            throw new ParseException("No content", 0);
        }
        String[] commaTokens = content.split(",");
        if (commaTokens.length < 3) {
            throw new ParseException("Not valid content", 0);
        }

        reset(commaTokens.length);

        for (String commaToken : commaTokens) {
            String[] tokens = commaToken.trim().split("\\s");
            if (tokens.length != 2) {
                throw new ParseException("Here must be two coordinates: " + commaToken, 0);
            }
            //Coordinate coordinate = new Coordinate();

            try {
                //coordinate.setLat(Double.parseDouble(tokens[0]));
                xpoints[npoints] = Float.parseFloat(tokens[0]);
            } catch (NumberFormatException e) {
                throw new ParseException(tokens[0] + " is not a float", 0);
            }
            try {
                //coordinate.setLon(Double.parseDouble(tokens[1]));
                ypoints[npoints] = Float.parseFloat(tokens[1]);
            } catch (NumberFormatException e) {
                throw new ParseException(tokens[1] + " is not a float", 0);
            }
            npoints++;
        }

        //preCalculate();
    }
    /*
    private void preCalculate() {
        if (npoints == 0) {
            return;
        }


        int i;
        int j = npoints - 1;

        if (constant != null) {
            constant = null;
        }
        if (multiple != null) {
            multiple = null;
        }

        constant = new float[npoints];
        multiple = new float[npoints];


        boolean hasNegative = false;
        boolean hasPositive = false;
        for (i = 0; i < npoints; i++) {
            if (ypoints[i] > 90) {
                hasPositive = true;
            } else if (ypoints[i] < -90) {
                hasNegative = true;
            }
        }
        needNormalize = hasPositive && hasNegative;

        for (i = 0; i < npoints; j = i++) {
            if (normalizeLon(ypoints[j]) == normalizeLon(ypoints[i])) {
                constant[i] = xpoints[i];
                multiple[i] = 0;
            } else {
                constant[i] = xpoints[i]
                        - (normalizeLon(ypoints[i]) * ypoints[j])
                        / (normalizeLon(ypoints[j]) - normalizeLon(ypoints[i]))
                        + (normalizeLon(ypoints[i]) * xpoints[i])
                        / (normalizeLon(ypoints[j]) - normalizeLon(ypoints[i]));
                multiple[i] = (xpoints[j] - xpoints[i])
                        / (normalizeLon(ypoints[j]) - normalizeLon(ypoints[i]));
            }
        }
    }*/

    private void reset(int newSize) {
        bounds = null;
        npoints = 0;
        xpoints = new float[Math.max(newSize, MIN_LENGTH)];
        ypoints = new float[Math.max(newSize, MIN_LENGTH)];
    }

    private Rectangle getBounds(){
        if (npoints == 0) {
            return EMPTY_RECT;
        }
        if (bounds == null) {
            calculateBounds(xpoints, ypoints, npoints);
        }

        return bounds;
    }

    void calculateBounds(float xpoints[], float ypoints[], int npoints) {
        float boundsMinX = Integer.MAX_VALUE;
        float boundsMinY = Integer.MAX_VALUE;
        float boundsMaxX = Integer.MIN_VALUE;
        float boundsMaxY = Integer.MIN_VALUE;

        for (int i = 0; i < npoints; i++) {
            float x = xpoints[i];
            boundsMinX = Math.min(boundsMinX, x);
            boundsMaxX = Math.max(boundsMaxX, x);
            float y = ypoints[i];
            boundsMinY = Math.min(boundsMinY, y);
            boundsMaxY = Math.max(boundsMaxY, y);
        }
    }

    private static class Rectangle {
        /**
         * The X coordinate of the upper-left corner of the {@code Rectangle}.
         *
         * @serial
         * @since 1.0
         */
        public float x;

        /**
         * The Y coordinate of the upper-left corner of the {@code Rectangle}.
         *
         * @serial
         * @since 1.0
         */
        public float y;

        /**
         * The width of the {@code Rectangle}.
         * @serial
         * @since 1.0
         */
        public float width;


        /**
         * The height of the {@code Rectangle}.
         *
         * @serial
         * @since 1.0
         */
        public float height;

        Rectangle(){
            this(0.0f, 0.0f, 0.0f, 0.0f);
        }

         Rectangle(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean contains(float X, float Y, float W, float H) {
            float w = this.width;
            float h = this.height;
            /*
            if ((w | h | W | H) < 0.0) {
                // At least one of the dimensions is negative...
                return false;
            }

             */
            // Note: if any dimension is zero, tests below must return false...
            float x = this.x;
            float y = this.y;
            if (X < x || Y < y) {
                return false;
            }
            w += x;
            W += X;
            if (W <= X) {
                // X+W overflowed or W was zero, return false if...
                // either original w or W was zero or
                // x+w did not overflow or
                // the overflowed x+w is smaller than the overflowed X+W
                if (w >= x || W > w) return false;
            } else {
                // X+W did not overflow and W was not zero, return false if...
                // original w was zero or
                // x+w did not overflow and x+w is smaller than X+W
                if (w >= x && W > w) return false;
            }
            h += y;
            H += Y;
            if (H <= Y) {
                if (h >= y || H > h) return false;
            } else {
                if (h >= y && H > h) return false;
            }
            return true;
        }

        public boolean contains(float X, float Y) {
            float w = this.width;
            float h = this.height;
            if (((int)w | (int)h) < 0) {
                // At least one of the dimensions is negative...
                return false;
            }
            // Note: if either dimension is zero, tests below must return false...
            float x = this.x;
            float y = this.y;
            if (X < x || Y < y) {
                return false;
            }
            w += x;
            h += y;
            //    overflow || intersect
            return ((w < x || w > X) &&
                    (h < y || h > Y));
        }
    }

}
