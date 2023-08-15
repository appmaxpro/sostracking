package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

public class Partner extends Model {

    public static final int TYPE_BUSINESS = 1;
    public static final int TYPE_PERSONAL = 2;


    private String name;
    private long salesPartnerPriceList;

    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSalesPartnerPriceList() {
        return salesPartnerPriceList;
    }

    public void setSalesPartnerPriceList(long salesPartnerPriceList) {
        this.salesPartnerPriceList = salesPartnerPriceList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PartnerPriceList getSalesPartnerPriceList(TmsStore tmsStore)  {
        return tmsStore.getObject(PartnerPriceList.class, salesPartnerPriceList);
    }


}
