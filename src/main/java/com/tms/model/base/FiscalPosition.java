package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

import java.util.Collection;

public class FiscalPosition extends Model {

    private String name;
    private String code;

    public Collection<TaxEquiv> getTaxEquivList(TmsStore store) {
        return store.getRelatedList(TaxEquiv.class, "taxEquivList", getId());
    }
}
