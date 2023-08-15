package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

import java.util.Collection;

public class OrderServiceCapability extends Model {

    public Collection<OrderServiceCapabilityItem> getCapabilityItems(TmsStore store) {
        return store.getRelatedList(OrderServiceCapabilityItem.class, "orderServiceCapability", getId());
    }

}
