package com.tms.model;

import com.tms.TmsStore;

import java.util.Collection;

public class TransCancelPricing extends Model{
    private int sequence = 0;
    private long priceList;
    private long transService;
    private long cancelReason;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getPriceList() {
        return priceList;
    }

    public void setPriceList(long priceList) {
        this.priceList = priceList;
    }

    public long getTransService() {
        return transService;
    }

    public void setTransService(long transService) {
        this.transService = transService;
    }

    public long getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(long cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Collection<TransCancelPricingRule> getCancelPricingRule(TmsStore store) {
        return store.getRelatedList(TransCancelPricingRule.class, "cancelPricing", getId());
    }
}
