package com.tms.utils;

import java.util.Objects;

class RelationKey<T> {
    private final Class<T> entity;
    private final String key;
    private final long reference;
    private final int _hashCode;
    public RelationKey(Class<T> entity, String key, long reference) {
        this.entity = entity;
        this.key = key;
        this.reference = reference;
        _hashCode = Objects.hash(reference, entity, key);
    }

    public String getKey() {
        return key;
    }

    public Class<T> getEntity() {
        return entity;
    }

    @Override
    public int hashCode() {
        return _hashCode;
    }

    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other instanceof RelationKey) {
            RelationKey myOther = (RelationKey) other;
            return reference == myOther.reference
                    && myOther.entity.equals(entity)
                    && myOther.key.equals(key);
        }
        return false;
    }
}
