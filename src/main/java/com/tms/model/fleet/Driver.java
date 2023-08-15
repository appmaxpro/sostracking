package com.tms.model.fleet;

import com.tms.TmsStore;
import com.tms.annotations.RefType;
import com.tms.model.base.GeoZoneGroup;
import com.tms.model.RefModel;
import com.tms.model.base.OrderServiceItemSet;

@RefType(target = org.traccar.model.Driver.class)
public class Driver extends RefModel {

    private static final int ALL_ZONES = 0;
    private static final int RADIUS = 1;
    private static final int CITY_PLACE = 2;
    private static final int SELECT = 3;

    private static final int PUBLISH_STATUS_PENDING = 1;
    private static final int PUBLISH_STATUS_PUBLISHED = 2;
    private static final int PUBLISH_STATUS_DOC_EXPIRED = 3;
    private static final int PUBLISH_STATUS_BLOCKED = 4;

    private String name;
    private int publishStatus;
    private String externalRef;
    private boolean freelancer;
    private boolean relationBlocked;

    private int driverZoneServiceType;

    private double zoneServiceRadius;
    private long includeZoneGroup;
    private long excludeZoneGroup;
    private long serviceItemSet;

    private long vehicle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(int publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public boolean isFreelancer() {
        return freelancer;
    }

    public void setFreelancer(boolean freelancer) {
        this.freelancer = freelancer;
    }

    public boolean isRelationBlocked() {
        return relationBlocked;
    }

    public void setRelationBlocked(boolean relationBlocked) {
        this.relationBlocked = relationBlocked;
    }

    public int getDriverZoneServiceType() {
        return driverZoneServiceType;
    }

    public void setDriverZoneServiceType(int driverZoneServiceType) {
        this.driverZoneServiceType = driverZoneServiceType;
    }

    public double getZoneServiceRadius() {
        return zoneServiceRadius;
    }

    public void setZoneServiceRadius(double zoneServiceRadius) {
        this.zoneServiceRadius = zoneServiceRadius;
    }

    public long getIncludeZoneGroup() {
        return includeZoneGroup;
    }

    public void setIncludeZoneGroup(long includeZoneGroup) {
        this.includeZoneGroup = includeZoneGroup;
    }

    public long getExcludeZoneGroup() {
        return excludeZoneGroup;
    }

    public void setExcludeZoneGroup(long excludeZoneGroup) {
        this.excludeZoneGroup = excludeZoneGroup;
    }

    public long getServiceItemSet() {
        return serviceItemSet;
    }

    public void setServiceItemSet(long serviceItemSet) {
        this.serviceItemSet = serviceItemSet;
    }

    public long getVehicle() {
        return vehicle;
    }

    public void setVehicle(long vehicle) {
        this.vehicle = vehicle;
    }

    public Vehicle getVehicle(TmsStore store){
        return store.getObject(Vehicle.class, getVehicle());
    }

    public GeoZoneGroup getIncludeZoneGroup(TmsStore store){
        return store.getObject(GeoZoneGroup.class, includeZoneGroup);
    }

    public GeoZoneGroup getExcludeZoneGroup(TmsStore store){
        return store.getObject(GeoZoneGroup.class, excludeZoneGroup);
    }

    public OrderServiceItemSet getServiceItemSet(TmsStore store){
        return store.getObject(OrderServiceItemSet.class, getServiceItemSet());
    }

}
