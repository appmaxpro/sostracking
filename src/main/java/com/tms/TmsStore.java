package com.tms;

import com.tms.model.Model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TmsStore {

    <T extends Model> List<T> getRelatedList(final Class<T> entity,
                                             String mappedBy, Long reference);

    <T extends Model> Collection<T> getObjects(Class<T> entity, long[] ids);

    <T extends Model> Collection<T> getAll(Class<T> entity);

    <T extends Model> T getObject(Class<T> entity, long id);

    <T extends Model> T getObjectByRef(Class<T> entity, long id);

    <T extends Model> Optional<T> of(Class<T> entity, long id);

    <T extends Model> T save(Object bean);

    void remove(Object bean);

}
