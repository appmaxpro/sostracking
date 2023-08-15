package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

import java.util.Collection;

public class OrderServiceItem extends Model {

    private long orderService;
    private long product;

    public OrderService getOrderService(TmsStore store) {
        return store.getObject(OrderService.class, orderService);
    }

    public Product getProduct(TmsStore store) {
        return store.getObject(Product.class, product);
    }

}
