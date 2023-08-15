package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

public class Company extends Model {

    private String name;
    private String code;
    private long currency;



    public Currency getCurrency(TmsStore store) {
        return store.getObject(Currency.class, currency);
    }

}
