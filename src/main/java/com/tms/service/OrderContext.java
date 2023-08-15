package com.tms.service;

import com.tms.TmsStore;
import com.tms.api.input.OrderRequest;
import com.tms.api.output.MetricsResult;
import com.tms.model.base.OrderServiceItemSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderContext {

    OrderRequest request;
    MetricsResult metrics;
    TmsStore store;

    int partnerType;



    private List<Map> pricingResultLog;
    private Map<String, Object> context = new HashMap<>();

    OrderServiceItemSet requestServiceItemSet;
    OrderServiceItemSet requestServiceRequirements;

    public OrderContext(OrderRequest request,
                        TmsStore store,
                        Map<String, Object> context) {
        this.request = request;
        this.store = store;
        if (context != null) {
            this.context.putAll(context);
        }
    }



    public Object get(String key) {
        return context.get(key);
    }

    public Object set(String key, Object value) {
        return context.put(key, value);
    }

    public Object eval(String expr) {
        return null;
    }

    void setMetrics(MetricsResult metrics) {
        context.put("distance", metrics.getDistance().doubleValue());
        context.put("duration", metrics.getDuration().doubleValue() / 60.0);

        context.put("traffic",
                Math.min(1.0,
                    metrics.getDuration() > 0
                    ? metrics.getTrafficDuration().doubleValue() /
                            metrics.getDuration().doubleValue()
                    : 0.0
                ));
        this.metrics = metrics;

    }

    public OrderRequest getRequest() {
        return request;
    }

    public MetricsResult getMetrics() {
        return metrics;
    }

    public TmsStore getStore() {
        return store;
    }

    public void addPricingResultLog(Map pricingLog){
        if (pricingResultLog == null)
            pricingResultLog = new ArrayList<>();
        pricingResultLog.add(pricingLog);

    }
}
