package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

import java.util.Collection;

public class Tax extends Model {
    private String name;
    private String code;
    private long activeTaxLine;

    public TaxRate getActiveTaxLine(TmsStore store) {
        return store.getObject(TaxRate.class, activeTaxLine);
    }

    public Collection<TaxRate> getTaxEquivList(TmsStore store) {
        return store.getRelatedList(TaxRate.class, "taxEquivList", getId());
    }
}
