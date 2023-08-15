package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.annotations.RefManyToMany;
import com.tms.model.Model;
import com.tms.model.base.OrderServiceItem;
import com.tms.utils.Utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceItemSet extends Model {

    @RefManyToMany(target = OrderServiceItem.class)
    private long[] items;
    private String code;

    public long[] getItems() {
        return items;
    }

    public void setItems(long[] items) {
        this.items = items;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Collection<OrderServiceItem> getItems(TmsStore store) {
        return store.getObjects(OrderServiceItem.class, items);
    }

    public static OrderServiceItemSet getOrderServiceItemSet(TmsStore store, long[] items) {
        Arrays.sort(items);
        String code = String.join(",", List.of(items).stream()
                .map(String::valueOf).collect(Collectors.toList()));

        var itemSet = Utils.findOne(store.getAll(OrderServiceItemSet.class),
                set -> code.equals(set.getCode()));
        if (itemSet == null) {
            itemSet = new OrderServiceItemSet();
            itemSet.setItems(items);
            itemSet.setCode(code);
            itemSet = store.save(itemSet);
        }

        return itemSet;

    }


    public boolean containsOrderServiceItem(long orderServiceItem) {
        if (items == null) {
            for (var item : items) {
                if (item == orderServiceItem)
                    return true;
            }
        }
        return false;
    }
}
