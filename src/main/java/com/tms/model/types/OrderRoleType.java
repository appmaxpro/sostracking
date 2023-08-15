package com.tms.model.types;

import com.tms.annotations.Widget;

import java.util.Objects;

public enum OrderRoleType implements ValueEnum<Integer> {
    @Widget(
            title = "Customer"
    )
    CUSTOMER(1),

    @Widget(
            title = "Provide"
    )
    PROVIDER(2),

    @Widget(
            title = "Operator"
    )
    OPERATOR(3);

    private final Integer value;

    private OrderRoleType(Integer value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Integer getValue() {
        return value;
    }
}