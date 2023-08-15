package com.tms.api.output;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ServiceRateResult {

    private long id;
    private BigDecimal rate;
    private String name;
    private int type;

    private Map<String, Object> attributes;

    public ServiceRateResult(long id, BigDecimal rate, String name, int type) {
        this(id, rate, name, type, null);
    }

    public ServiceRateResult(long id, BigDecimal rate, String name, int type,
                             Map<String, Object> attributes) {
        this.id = id;
        this.rate = rate;
        this.name = name;
        this.type = type;
        this.attributes = attributes;
    }


    public long getId() {
        return id;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttr(String key, Object value){
        if (attributes == null)
            attributes = new HashMap<>();
        attributes.put(key, value);
    }

}
