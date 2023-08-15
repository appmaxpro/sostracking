package com.tms.model;

import com.tms.TmsStore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

public class TransPricing extends Model implements PricingRuleContainer{
    private int sequence = 0;
    private long priceList;
    private long transService;
    private String name;
    private BigDecimal baseRate = BigDecimal.ZERO;
    private BigDecimal ratePer100m = BigDecimal.ZERO;
    private BigDecimal ratePerMinute = BigDecimal.ZERO;
    private BigDecimal ratePerMinuteWait = BigDecimal.ZERO;
    private BigDecimal perPayPercent = BigDecimal.ZERO;
    private int minDistance;
    private int maxDistance;
    private LocalDate startDate;
    private LocalDate endDate;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    public BigDecimal getRatePer100m() {
        return ratePer100m;
    }

    public void setRatePer100m(BigDecimal ratePer100m) {
        this.ratePer100m = ratePer100m;
    }

    public BigDecimal getRatePerMinute() {
        return ratePerMinute;
    }

    public void setRatePerMinute(BigDecimal ratePerMinute) {
        this.ratePerMinute = ratePerMinute;
    }

    public BigDecimal getRatePerMinuteWait() {
        return ratePerMinuteWait;
    }

    public void setRatePerMinuteWait(BigDecimal ratePerMinuteWait) {
        this.ratePerMinuteWait = ratePerMinuteWait;
    }

    public BigDecimal getPerPayPercent() {
        return perPayPercent;
    }

    public void setPerPayPercent(BigDecimal perPayPercent) {
        this.perPayPercent = perPayPercent;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Collection<TransRateRule> getRateRuleList(TmsStore store) {
        return store.getRelatedList(TransRateRule.class, "transPricing", getId());
    }

    public Collection<TransTimeRateRule> getTimeRateRuleList(TmsStore store) {
        return store.getRelatedList(TransTimeRateRule.class, "transPricing", getId());
    }

    public Collection<TransTrafficRateRule> getTrafficRateRuleList(TmsStore store) {
        return store.getRelatedList(TransTrafficRateRule.class, "transPricing", getId());
    }

}
