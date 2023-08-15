package com.tms.model.base;

import com.tms.model.Model;
import com.tms.model.TransCancelPricing;
import com.tms.model.TransPricing;
import com.tms.model.TransZonePricing;
import com.tms.model.types.TransRoundingMode;
import com.tms.TmsStore;

import java.util.Collection;

public class PartnerPriceList extends Model {

    private TransRoundingMode roundingMode;

    public TransRoundingMode getRoundingMode() {
        return roundingMode;
    }

    public void setRoundingMode(TransRoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }

    public Collection<TransPricing> getTransPricingList(TmsStore store) {
        return store.getRelatedList(TransPricing.class, "priceList", getId());
    }

    public Collection<TransZonePricing> getTransZonePricingList(TmsStore store) {
        return store.getRelatedList(TransZonePricing.class, "priceList", getId());
    }

    public Collection<TransCancelPricing> getTransCancelPricingList(TmsStore store) {
        return store.getRelatedList(TransCancelPricing.class, "priceList", getId());
    }

}
