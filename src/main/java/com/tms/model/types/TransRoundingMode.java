package com.tms.model.types;

import com.tms.annotations.Widget;

import java.math.RoundingMode;
import java.util.Objects;

public enum TransRoundingMode implements ValueEnum<Integer> {
    @Widget(
            title = "None"
    )
    NONE(7),

    @Widget(
            title = "Rounding up"
    )
    UP(0),

    @Widget(
            title = "Rounding down"
    )
    DOWN(1),

    @Widget(
            title = "Rounding celling"
    )
    CEILING(2),

    @Widget(
            title = "Rounding floor"
    )
    FLOOR(3),

    @Widget(
            title = "Rounding half up"
    )
    HALF_UP(4),

    @Widget(
            title = "Rounding half down"
    )
    HALF_DOWN(5),

    @Widget(
            title = "Rounding half even"
    )
    HALF_EVEN(6);

    private final Integer value;
    private final RoundingMode roundingMode;

    private TransRoundingMode(Integer value) {
        this.value = Objects.requireNonNull(value);
        roundingMode = RoundingMode.valueOf(value);
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }
}
