package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

import java.util.Collection;

public class OrderService extends Model {

    public Collection<OrderServiceItem> getOrderServiceItems(TmsStore store) {
        return store.getRelatedList(OrderServiceItem.class, "orderService", getId());
    }

}
