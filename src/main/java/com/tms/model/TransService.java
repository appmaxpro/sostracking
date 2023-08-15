package com.tms.model;

import com.tms.TmsStore;
import com.tms.model.base.GeoZoneGroup;
import com.tms.model.base.OrderServiceItem;
import com.tms.model.types.TransRoundingMode;

import java.math.BigDecimal;
import java.util.Collection;


public class TransService extends Model{

    private int sequence = 0;

    private String name;

    private long orderServiceItem;

    private BigDecimal baseRate = BigDecimal.ZERO;

    private BigDecimal ratePer100m = BigDecimal.ZERO;

    private BigDecimal ratePerMinute = BigDecimal.ZERO;

    private BigDecimal ratePerMinuteWait = BigDecimal.ZERO;

    private BigDecimal perPayPercent = BigDecimal.ZERO;

    private int minDistance;

    private int maxDistance;

    private int searchRadius;

    private long zoneGroup;

    private boolean useForPublic;

    private boolean useZonePricing = false;

    private boolean useAdvancedPricing = false;
    private TransRoundingMode roundingMode;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOrderServiceItem() {
        return orderServiceItem;
    }

    public void setOrderServiceItem(long orderServiceItem) {
        this.orderServiceItem = orderServiceItem;
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

    public int getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }

    public long getZoneGroup() {
        return zoneGroup;
    }

    public void setZoneGroup(long zoneGroup) {
        this.zoneGroup = zoneGroup;
    }

    public boolean isUseForPublic() {
        return useForPublic;
    }

    public void setUseForPublic(boolean useForPublic) {
        this.useForPublic = useForPublic;
    }

    public boolean isUseAdvancedPricing() {
        return useAdvancedPricing;
    }

    public boolean isUseZonePricing() {
        return useZonePricing;
    }

    public void setUseZonePricing(boolean useZonePricing) {
        this.useZonePricing = useZonePricing;
    }

    public boolean getUseAdvancedPricing() {
        return useAdvancedPricing;
    }

    public void setUseAdvancedPricing(boolean useAdvancedPricing) {
        this.useAdvancedPricing = useAdvancedPricing;
    }

    public TransRoundingMode getRoundingMode() {
        return roundingMode;
    }

    public void setRoundingMode(TransRoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }

    public Collection<TransPricing> getTransPricingList(TmsStore store) {
        return store.getRelatedList(TransPricing.class, "transService", getId());
    }

    public Collection<TransZonePricing> getTransZonePricingList(TmsStore store) {
        return store.getRelatedList(TransZonePricing.class, "transService", getId());
    }

    public Collection<TransCancelPricing> getTransCancelPricingList(TmsStore store) {
        return store.getRelatedList(TransCancelPricing.class, "transService", getId());
    }

    public GeoZoneGroup getZoneGroup(TmsStore store) {
        return store.getObject(GeoZoneGroup.class, zoneGroup);
    }

    public OrderServiceItem getOrderServiceItem(TmsStore store) {
        return store.getObject(OrderServiceItem.class, orderServiceItem);
    }

}
