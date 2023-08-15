package com.tms.model.fleet;

import com.tms.TmsStore;
import com.tms.annotations.RefType;
import com.tms.model.RefModel;
import com.tms.model.base.OrderServiceItemSet;

@RefType(target = org.traccar.model.Device.class)
public class Vehicle extends RefModel {

    private long requirements;

    private long profile;

    public OrderServiceItemSet getRequirements(TmsStore store) {
        return store.getObject(OrderServiceItemSet.class, requirements);
    }

    public VehicleProfile getProfile(TmsStore store) {
        return store.getObject(VehicleProfile.class, profile);
    }

}
