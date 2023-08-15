package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

import java.util.Collection;

public class OrderServiceCapabilityItem extends Model {

    private int sequence;

    private long orderServiceCapability;
    private long orderService;
    private long[] orderServiceItems;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getOrderServiceCapability() {
        return orderServiceCapability;
    }

    public void setOrderServiceCapability(long orderServiceCapability) {
        this.orderServiceCapability = orderServiceCapability;
    }

    public long getOrderService() {
        return orderService;
    }

    public void setOrderService(long orderService) {
        this.orderService = orderService;
    }

    public long[] getOrderServiceItems() {
        return orderServiceItems;
    }

    public void setOrderServiceItems(long[] orderServiceItems) {
        this.orderServiceItems = orderServiceItems;
    }

    public Collection<OrderServiceItem> getOrderServiceItems(TmsStore store) {
        return store.getObjects(OrderServiceItem.class, orderServiceItems);
    }


}
