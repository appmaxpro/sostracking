package com.tms.model.fleet;

import com.tms.TmsStore;
import com.tms.annotations.RefManyToMany;
import com.tms.model.Model;
import com.tms.model.base.OrderServiceItem;

import java.util.Collection;

public class DriverOrderServiceItemSet extends Model {

    @RefManyToMany(target = OrderServiceItem.class)
    private long[] serviceItemSet;


    public Collection<OrderServiceItem> getOrderServiceItemSet(TmsStore store) {
        return store.getObjects(OrderServiceItem.class, serviceItemSet);
    }




}
