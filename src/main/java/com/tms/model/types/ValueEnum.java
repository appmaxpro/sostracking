package com.tms.model.types;

import java.util.Objects;

public interface ValueEnum<T> {

    /**
     * Get the value.
     *
     * @return custom value associated with this enum constant.
     */
    T getValue();

    /**
     * Get the constant of the specified enum type with the specified value.
     *
     * @param <T> the enum type whose constant is to be returned
     * @param enumType the {@code Class} object of the enum type from which to return a constant
     * @param value the value of the constant to return
     * @return the enum constant of the specified enum type with the specified value
     * @throws NullPointerException if the specified value is null
     * @throws IllegalArgumentException if specified enumType is not an enum or no constant found for
     *     the specified value
     */
    static <T extends Enum<T>> T of(Class<T> enumType, Object value) {
        if (value == null) {
            throw new NullPointerException("Value is null.");
        }
        if (value instanceof Enum) {
            return enumType.cast(value);
        }
        if (ValueEnum.class.isAssignableFrom(enumType)) {
            for (T item : enumType.getEnumConstants()) {
                if (Objects.equals(((ValueEnum<?>) item).getValue(), value)) {
                    return item;
                }
            }
        }
        if (value instanceof String) {
            try {
                return Enum.valueOf(enumType, (String) value);
            } catch (Exception e) {
            }
        }
        throw new IllegalArgumentException(
                "No enum constant found in " + enumType.getCanonicalName() + " for value: " + value);
    }
}
