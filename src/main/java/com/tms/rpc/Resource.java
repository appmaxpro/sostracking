package com.tms.rpc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tms.mapper.ModelMapper;
import com.tms.mapper.Property;
import com.tms.mapper.PropertyType;
import com.tms.model.Model;
import com.tms.model.types.ValueEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Resource {
    public static Map<String, Object> toMap(Object bean, String... names) {
        return _toMap(bean, unflatten(null, names), false, 0);
    }

    public static Map<String, Object> toMapCompact(Object bean) {
        return _toMap(bean, null, true, 1);
    }

    @SuppressWarnings("all")
    private static Map<String, Object> _toMap(
            Object bean, Map<String, Object> fields, boolean compact, int level) {

        if (bean == null) {
            return null;
        }



        if (fields == null) {
            fields = Maps.newHashMap();
        }

        Map<String, Object> result = new HashMap<String, Object>();
        ModelMapper modelMapper = ModelMapper.of(bean.getClass());

        boolean isSaved = ((Model) bean).getId() != null;
        boolean isCompact = compact || fields.containsKey("$version");

        final Set<Property> translatables = new HashSet<>();

        if ((isCompact && isSaved) || (isSaved && level >= 1) || (level > 1)) {

            Property pn = modelMapper.getNameField();
            Property pc = modelMapper.getProperty("code");

            result.put("id", modelMapper.get(bean, "id"));
            result.put("$version", modelMapper.get(bean, "version"));

            if (pn != null) {
                result.put(pn.getName(), modelMapper.get(bean, pn.getName()));
            }
            if (pc != null) {
                result.put(pc.getName(), modelMapper.get(bean, pc.getName()));
            }

            if (pn != null && pn.isTranslatable()) {
                //Translator.translate(result, mapper, pn.getName());
            }

            if (pc != null && pc.isTranslatable()) {
                //Translator.translate(result, mapper, pc.getName());
            }

            for (String name : fields.keySet()) {
                Object child = modelMapper.get(bean, name);
                if (child instanceof Model) {
                    child = _toMap(child, (Map) fields.get(name), true, level + 1);
                }
                Property property = modelMapper.getProperty(name);
                if (property.isTranslatable()) {
                    //Translator.translate(result, mapper, property.getName())
                }

                result.put(name, child);

                /*
                Optional.ofNullable(mapper.getProperty(name))
                        .filter(Property::isTranslatable)
                        .ifPresent(property -> Translator.translate(result, mapper, property.getName()));

                 */
            }
            return result;
        }

        for (final Property prop : modelMapper.getProperties()) {

            String name = prop.getName();
            PropertyType type = prop.getType();

            if (type == PropertyType.BINARY || prop.isPassword()) {
                continue;
            }

            if (isSaved
                    && !name.matches("id|version|archived")
                    && !fields.isEmpty()
                    && !fields.containsKey(name)) {
                continue;
            }

            Object value = modelMapper.get(bean, name);

            if (name.equals("archived") && value == null) {
                continue;
            }

            if (prop.isImage() && byte[].class.isInstance(value)) {
                value = new String((byte[]) value);
            }

            // decimal values should be rounded accordingly otherwise the
            // json mapper may use wrong scale.
            if (value instanceof BigDecimal) {
                BigDecimal decimal = (BigDecimal) value;
                int scale = prop.getScale();
                if (decimal.scale() == 0 && scale > 0 && scale != decimal.scale()) {
                    value = decimal.setScale(scale, RoundingMode.HALF_UP);
                }
            }

            if (value instanceof Model) { // m2o
                Map<String, Object> _fields = (Map) fields.get(prop.getName());
                value = _toMap(value, _fields, true, level + 1);
            }

            if (value instanceof Collection) { // o2m | m2m
                List<Object> items = Lists.newArrayList();
                for (Model input : (Collection<Model>) value) {
                    Map<String, Object> item;
                    if (input.getId() != null) {
                        item = _toMap(input, null, true, level + 1);
                    } else {
                        item = _toMap(input, null, false, 1);
                    }
                    if (item != null) {
                        items.add(item);
                    }
                }
                value = items;
            }

            result.put(name, value);

            if (prop.isTranslatable() && value instanceof String) {
                //Translator.translate(result, mapper, prop.getName());
            }

            // include custom enum value
            if (prop.isEnum() && value instanceof ValueEnum<?>) {
                String enumName = ((Enum<?>) value).name();
                Object enumValue = ((ValueEnum<?>) value).getValue();
                if (!com.google.common.base.Objects.equal(enumName, enumValue)) {
                    result.put(name + "$value", ((ValueEnum<?>) value).getValue());
                }
            }

        }

        return result;
    }

    @SuppressWarnings("all")
    private static Map<String, Object> unflatten(Map<String, Object> map, String... names) {
        if (map == null) map = Maps.newHashMap();
        if (names == null) return map;
        for (String name : names) {
            if (map.containsKey(name)) continue;
            if (name.indexOf('.') >= 0) {
                String[] parts = name.split("\\.", 2);
                Map<String, Object> child = (Map) map.get(parts[0]);
                if (child == null) {
                    child = Maps.newHashMap();
                }
                map.put(parts[0], unflatten(child, parts[1]));
            } else {
                map.put(name, Maps.newHashMap());
            }
        }
        return map;
    }
}
