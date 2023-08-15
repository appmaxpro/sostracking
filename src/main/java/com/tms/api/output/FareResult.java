package com.tms.api.output;

import java.util.List;

public class FareResult {
    private MetricsResult metrics;
    private String currency;
    private List<ServiceRateResult> services;

    public FareResult(MetricsResult metrics, String currency, List<ServiceRateResult> services) {
        this.metrics = metrics;
        this.currency = currency;
        this.services = services;
    }

    public MetricsResult getMetrics() {
        return metrics;
    }

    public String getCurrency() {
        return currency;
    }

    public List<ServiceRateResult> getServices() {
        return services;
    }
}
