package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;

public class TaxEquiv extends Model {

    private long fiscalPosition;
    private long fromTax;
    private long toTax;

    public FiscalPosition getFiscalPosition(TmsStore store) {
        return store.getObject(FiscalPosition.class, fiscalPosition);
    }

    public Tax getFromTax(TmsStore store) {
        return store.getObject(Tax.class, fromTax);
    }

    public Tax getToTax(TmsStore store) {
        return store.getObject(Tax.class, toTax);
    }


}
