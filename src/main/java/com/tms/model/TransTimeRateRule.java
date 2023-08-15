package com.tms.model;

import java.math.BigDecimal;
import java.time.LocalTime;

public class TransTimeRateRule extends Model{
    private int sequence = 0;
    private long transPricing;
    private LocalTime startTime;
    private LocalTime endTime;
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getMultiple() {
        return multiple;
    }

    public void setMultiple(BigDecimal multiple) {
        this.multiple = multiple;
    }
}
