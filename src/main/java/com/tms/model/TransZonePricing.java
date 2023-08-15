package com.tms.model;

import com.tms.TmsStore;
import com.tms.model.base.GeoZone;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransZonePricing extends Model{
    // AMOUNT TYPE SELECT
    public static final int AMOUNT_TYPE_NONE = 0;
    public static final int AMOUNT_TYPE_PERCENT = 1;
    public static final int AMOUNT_TYPE_FIXED = 2;

    // TYPE SELECT
    public static final int TYPE_DISCOUNT = 1;
    public static final int TYPE_ADDITIONNAL = 2;
    public static final int TYPE_REPLACE = 3;


    private int sequence = 0;
    private long priceList;
    private long transService;
    private long fromZone;
    private long toZone;
    private BigDecimal price;
    private int typeSelect;

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

    public long getFromZone() {
        return fromZone;
    }

    public void setFromZone(long fromZone) {
        this.fromZone = fromZone;
    }

    public long getToZone() {
        return toZone;
    }

    public void setToZone(long toZone) {
        this.toZone = toZone;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getTypeSelect() {
        return typeSelect;
    }

    public void setTypeSelect(int typeSelect) {
        this.typeSelect = typeSelect;
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

    public GeoZone getFromZone(TmsStore store) {
        return store.getObject(GeoZone.class, fromZone);
    }

    public GeoZone getToZone(TmsStore store) {
        return store.getObject(GeoZone.class, toZone);
    }

    public TransService getTransService(TmsStore store) {
        return store.getObject(TransService.class, transService);
    }
}
