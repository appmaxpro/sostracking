package com.tms.model.base;

import com.tms.annotations.RefType;
import com.tms.model.RefModel;
import org.traccar.geofence.GeofenceGeometry;

import javax.persistence.Transient;

@RefType(target = org.traccar.model.Geofence.class)
public class GeoZone extends RefModel {

    @Transient
    private GeofenceGeometry geometry;

    public GeofenceGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GeofenceGeometry geometry) {
        this.geometry = geometry;
    }

    public boolean contains(double lat, double lng){
        return geometry != null && geometry.containsPoint(lat, lng, 0.0);
    }
}
