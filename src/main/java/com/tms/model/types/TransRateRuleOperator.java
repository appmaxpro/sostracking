package com.tms.model.types;

import com.tms.annotations.Widget;

import java.util.Objects;

public enum TransRateRuleOperator implements ValueEnum<String> {
    @Widget(
            title = "Greater Than"
    )
    GT(">"),

    @Widget(
            title = "Greater than equals"
    )
    GTE(">="),

    @Widget(
            title = "Equals"
    )
    EQ("=="),

    @Widget(
            title = "Less Than"
    )
    LT("<"),

    @Widget(
            title = "Less than equals"
    )
    LTE("<=");

    private final String value;

    private TransRateRuleOperator(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String getValue() {
        return value;
    }
}
