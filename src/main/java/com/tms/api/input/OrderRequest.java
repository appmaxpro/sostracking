package com.tms.api.input;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderRequest implements Serializable {
    private MetricRequest metrics;
    private Long partner;
    private Long service;
    private long[] serviceItems;
    private long[] serviceRequirements;

    private LocalDateTime dateTime;
    private Boolean useRateAttributes;

    public MetricRequest getMetrics() {
        return metrics;
    }

    public void setMetrics(MetricRequest metrics) {
        this.metrics = metrics;
    }

    public Long getPartner() {
        return partner;
    }

    public void setPartner(Long partner) {
        this.partner = partner;
    }

    public long getService() {
        return service;
    }

    public void setService(Long service) {
        this.service = service;
    }

    public long[] getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(long[] serviceItems) {
        this.serviceItems = serviceItems;
    }

    public long[] getServiceRequirements() {
        return serviceRequirements;
    }

    public void setServiceRequirements(long[] serviceRequirements) {
        this.serviceRequirements = serviceRequirements;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean getUseRateAttributes() {
        return useRateAttributes;
    }

    public void setUseRateAttributes(Boolean useRateAttributes) {
        this.useRateAttributes = useRateAttributes;
    }
}
