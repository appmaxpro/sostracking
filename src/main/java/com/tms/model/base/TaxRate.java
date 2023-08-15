package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

import java.math.BigDecimal;

public class TaxRate extends Model {

    private String name;
    private BigDecimal value = BigDecimal.ZERO;
    private long tax;

    public Tax getTax(TmsStore store) {
        return store.getObject(Tax.class, tax);
    }

}
