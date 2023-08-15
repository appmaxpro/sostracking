package com.tms.model.fleet;

import com.tms.TmsStore;
import com.tms.model.Model;
import com.tms.model.base.OrderServiceCapability;

public class VehicleProfile extends Model {

    private double minRunDistance;
    private double maxRunDistance;
    private long orderServiceCapability;

    public double getMinRunDistance() {
        return minRunDistance;
    }

    public void setMinRunDistance(double minRunDistance) {
        this.minRunDistance = minRunDistance;
    }

    public double getMaxRunDistance() {
        return maxRunDistance;
    }

    public void setMaxRunDistance(double maxRunDistance) {
        this.maxRunDistance = maxRunDistance;
    }

    public long getOrderServiceCapability() {
        return orderServiceCapability;
    }

    public void setOrderServiceCapability(long orderServiceCapability) {
        this.orderServiceCapability = orderServiceCapability;
    }

    public OrderServiceCapability getOrderServiceCapability(TmsStore store) {
        return store.getObject(OrderServiceCapability.class, orderServiceCapability);
    }

}
