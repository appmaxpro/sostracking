package com.tms.model.types;

import com.tms.annotations.Widget;

import java.util.Objects;

public enum TransRateRuleVariable implements ValueEnum<String>{
    @Widget(
            title = "Distance"
    )
    DISTANCE("distance"),

    @Widget(
            title = "Duration"
    )
    DURATION("duration"),

    @Widget(
            title = "Price"
    )
    PRICE("price"),

    @Widget(
            title = "Traffic"
    )
    TRAFFIC("traffic"),

    @Widget(
            title = "Cancel"
    )
    CANCEL("cancel");

    private final String value;

    private TransRateRuleVariable(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String getValue() {
        return value;
    }
}
