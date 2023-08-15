package com.tms.model;

import java.math.BigDecimal;

public class TransTrafficRateRule extends Model{
    private int sequence = 0;
    private long transPricing;
    private double trafficValue;
    private BigDecimal multiple;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getTransPricing() {
        return transPricing;
    }

    public void setTransPricing(long transPricing) {
        this.transPricing = transPricing;
    }

    public double getTrafficValue() {
        return trafficValue;
    }

    public void setTrafficValue(double trafficValue) {
        this.trafficValue = trafficValue;
    }

    public BigDecimal getMultiple() {
        return multiple;
    }

    public void setMultiple(BigDecimal multiple) {
        this.multiple = multiple;
    }
}
