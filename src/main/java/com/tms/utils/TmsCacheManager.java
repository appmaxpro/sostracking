package com.tms.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tms.mapper.ModelMapper;
import com.tms.mapper.PropertyType;
import com.tms.model.Model;
import com.tms.utils.primitive.Primitive;
import com.tms.utils.primitive.PrimitiveLongObjectMap;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class TmsCacheManager<T extends Model> {

    private static Cache<RelationKey, List<? extends Model>> relationCache =
            Caffeine.newBuilder()
                    .maximumSize(300)
                    .weakKeys()
                    .build();

    private static Map<Class<? extends Model>, PrimitiveLongObjectMap<? extends Model>>
            mapObjects = new HashMap<>();


    public static <T extends Model> List<T> getRelatedList(final Class<T> entity,
                                                           String mappedBy,
                                                           Long reference){

        List related =  relationCache.get(new RelationKey<T>(entity, mappedBy, reference), key -> {

            var mapper = ModelMapper.of(entity);
            var relatedProperty = mapper.getGetter(mappedBy);
            PrimitiveLongObjectMap<T> values = getObjectMap(entity);
            List<T> result = new ArrayList<>();
            for (var object : values.values()) {
                if (reference.equals(relatedProperty.apply(object))) {
                    result.add(object);
                }
            }
            return Collections.unmodifiableList(result);
        });
        return related;
    }

    public static <T extends Model> Collection<T> getObjects(Class<T> owner, long[] ids){
        if (ids == null)
            return Collections.EMPTY_LIST;

        List<T> result = new ArrayList<>() ;
        PrimitiveLongObjectMap<? extends Model> models = getObjectMap(owner);

        for (int index = 0; index < ids.length; index++) {
            T model = (T) models.get(ids[index]);
            result.add(model);
        }
        return result;
    }

    public static <T extends Model> T getObject(Class<T> owner, long id) {
        return (T)getObjectMap(owner).get(id);
    }

    public static <T extends Model>
            PrimitiveLongObjectMap<T> getObjectMap(Class<T> owner) {
        return (PrimitiveLongObjectMap<T>)mapObjects.computeIfAbsent(owner, key -> Primitive.longObjectMap());
    }


    void onChange(Object model, List<String> fields) {
        ConcurrentMap map = relationCache.asMap();
        var mapper = ModelMapper.of(model.getClass());
        var id = (Long)mapper.get(model, "id");
        for(String field : fields) {
            var property = mapper.getProperty(field);
            if (property.getType() == PropertyType.ONE_TO_MANY) {
                relationCache.invalidate(new RelationKey(property.getTarget(), property.getMappedBy(), id));
            }
        }
    }


}
