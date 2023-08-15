package com.tms.model.types;

import com.tms.annotations.Widget;

import java.util.Objects;

public enum TransRateRuleFactor implements ValueEnum<String> {
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
            title = "Deference"
    )
    DEF("def");

    private final String value;

    private TransRateRuleFactor(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String getValue() {
        return value;
    }
}
