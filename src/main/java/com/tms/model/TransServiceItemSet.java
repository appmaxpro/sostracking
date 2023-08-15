package com.tms.model;

import com.tms.TmsStore;
import com.tms.model.base.OrderServiceItem;

import java.util.Collection;

public class TransServiceItemSet extends Model{

    private long[] orderServiceItems;
    private String code;

    public long[] getOrderServiceItems() {
        return orderServiceItems;
    }

    public void setOrderServiceItems(long[] orderServiceItems) {
        this.orderServiceItems = orderServiceItems;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Collection<OrderServiceItem> getOrderServiceItems(TmsStore store) {
        return store.getObjects(OrderServiceItem.class, orderServiceItems);
    }

}
