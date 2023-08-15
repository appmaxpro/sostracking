/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2023 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.tms.mapper;


import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tms.inject.Beans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tms.TmsStore;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * This class can be used to map params to Java bean using reflection. It also provides convenient
 * methods to get/set values to a bean instance.
 */
public class ModelMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModelMapper.class);

  private static final LoadingCache<Class<?>, ModelMapper> MAPPER_CACHE =
      CacheBuilder.newBuilder()
              .maximumSize(100)
              .weakKeys()
              .build(CacheLoader.from(ModelMapper::new));

  private static final Cache<Method, Annotation[]> ANNOTATION_CACHE =
      CacheBuilder.newBuilder().maximumSize(1000).weakKeys().build();

  private static final Object[] NULL_ARGUMENTS = {};

  private static final String PREFIX_COMPUTE = "compute";
  private static final String PREFIX_SET = "set";

  private Map<String, Method> getters = new HashMap<>(); // field -> getter
  private Map<String, Method> setters = new HashMap<>(); // field -> setter
  private Map<String, String> methods = new HashMap<>(); // getter/setter/compute -> field

  private Map<String, Class<?>> types = new HashMap<>();
  private Map<String, Property> fields = new HashMap<>();

  private List<String> fieldDepends;

  private Map<String, Set<String>> computeDependencies;

  private Set<Property> sequenceFields = new HashSet<>();

  private Property nameField;

  private Class<?> beanClass;
  private transient boolean inheritChecked;

  private transient boolean hasExtendedFields;
  private transient TmsStore store;

  private ModelMapper(Class<?> beanClass) {
    Preconditions.checkNotNull(beanClass);
    this.beanClass = beanClass;
    try {

      Map<String, List<String>> dependsFields = null;
      BeanInfo info = Introspector.getBeanInfo(beanClass, Object.class);
      for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
        String name = descriptor.getName();
        Method getter = descriptor.getReadMethod();
        Method setter = descriptor.getWriteMethod();
        Class<?> type = descriptor.getPropertyType();

        if (getter != null) {
          getters.put(name, getter);
          methods.put(getter.getName(), name);
          try {
            if (setter == null) {
              setter =
                      getMethod(
                              beanClass,
                              PREFIX_SET + name.substring(0, 1).toUpperCase() + name.substring(1),
                              type);
            }
            if (setter != null) {
              setter.setAccessible(true);
              setters.put(name, setter);
              methods.put(setter.getName(), name);
            }


            AnnotationFieldInfo fieldInfo = getAnnotationFieldInfo(name, getter);

            Property property =
                new Property(
                    this, name, getter, setter, type, fieldInfo);

            if (fieldInfo.field == null)
              property.transient_ = true;

            fields.put(name, property);
            if (property.isSequence()) {
              sequenceFields.add(property);
            }
            if (nameField == null && property.isNameColumn()) {
              nameField = property;
            }

            if (property.isVirtual()) {
              final Method compute =
                  getMethod(
                      beanClass,
                      PREFIX_COMPUTE + name.substring(0, 1).toUpperCase() + name.substring(1));
              if (compute != null) {
                methods.put(compute.getName(), name);
              }
            }
          } catch (Exception e) {

            continue;
          }
        }

        types.put(name, type);
      }

      if (dependsFields != null) {
        // fieldDepends = Collections.unmodifiableList(topSort(dependsFields));
      }
      store = Beans.get(TmsStore.class);

    } catch (IntrospectionException e) {
    }
  }

  private void initInherits() {
    synchronized (ModelMapper.class) {
      if (inheritChecked) {
        return;
      }
      inheritChecked = true;
      /*
      if (attrs != null) {

        for (Attr attr : beanClass.getAnnotation(Attrs.class).value()) {
          if ("extend".equals(attr.name()) && attr.type() != null && attr.type() != beanClass) {

            Property referenceProperty = getProperty(attr.value());
            if (referenceProperty != null) {
              hasExtendedFields = true;
              Mapper mapperToExtend = Mapper.of(referenceProperty.getTarget());

              for (Property property : mapperToExtend.fields.values()) {
                if (!property.isPrimary()) {

                  Property existsProperty = fields.get(property.getName());
                  if (existsProperty == null || existsProperty.isTransient()) {
                    if (referenceProperty.mappedByProperty == null
                            && referenceProperty.getName().equals(property.getMappedBy())
                            && referenceProperty.getTarget().isInstance(property.getTarget())) {
                      referenceProperty.mappedByProperty = property;
                    }
                    try {
                      Property relatedChild = referenceProperty.addChildProperty(property);
                      fields.put(relatedChild.getName(), relatedChild);
                    } catch (CloneNotSupportedException e) {
                      LOGGER.warn("Clone property: " + property.getName() + " cause error", e);
                    }
                  }
                }
              }
            }
          }
        }
      }

       */
    }

  }

  private AnnotationFieldInfo getAnnotationFieldInfo(String name, Method method) {
    Field field = null;
    Annotation[] fieldAnnotation;
    try {
      field = getField(beanClass, name);
      fieldAnnotation = field.getAnnotations();
    } catch (Exception e) {
      fieldAnnotation = new Annotation[0];
    }

    Annotation[] methodAnnotation = method.getAnnotations();
    if (methodAnnotation.length == 0) {
      return new AnnotationFieldInfo(field, fieldAnnotation);
    }

    if (fieldAnnotation.length == 0) {
      return new AnnotationFieldInfo(field, methodAnnotation);
    }

    Annotation[] all = new Annotation[methodAnnotation.length + fieldAnnotation.length];
    System.arraycopy(fieldAnnotation, 0, all, 0, fieldAnnotation.length);
    System.arraycopy(methodAnnotation, 0, all, fieldAnnotation.length, methodAnnotation.length);

    return new AnnotationFieldInfo(field, all);
  }

  private Field getField(Class<?> klass, String name) {
    if (klass == null) return null;
    try {
      return klass.getDeclaredField(name);
    } catch (NoSuchFieldException e) {
      return getField(klass.getSuperclass(), name);
    }
  }

  private Method getMethod(Class<?> klass, String name, Class<?>... parameterTypes) {
    if (klass == null) return null;
    try {
      return klass.getDeclaredMethod(name, parameterTypes);
    } catch (NoSuchMethodException e) {
      return getMethod(klass.getSuperclass(), name, parameterTypes);
    }
  }

  /**
   * Create a {@link ModelMapper} for the given Java Bean class by introspecting all it's properties.
   *
   * <p>If the {@link ModelMapper} class has been previously created for the given class, then the {@link
   * ModelMapper} class is retrieved from the cache.
   *
   * @param klass the bean class
   * @return an instance of {@link ModelMapper} for the given class.
   */
  public static ModelMapper of(Class<?> klass) {
    try {
      ModelMapper result = MAPPER_CACHE.get(klass);
      if (!result.inheritChecked) result.initInherits();
      return result;
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get all the properties.
   *
   * @return an array of {@link Property}
   */
  public Property[] getProperties() {
    return fields.values().toArray(new Property[] {});
  }

  /**
   * Get the {@link Property} of the given name.
   *
   * @param name name of the property
   * @return a Property or null if property doesn't exist.
   */
  public Property getProperty(String name) {
    return fields.get(name);
  }

  /**
   * Get the {@link Property}  of the given name if it exists.
   *
   * @param bean the bean
   * @param name name of the property
   * @return a Property or null if property doesn't exist.
   */
  public Property getProperty(Object bean, String name) {
    return getProperty(name);
  }

  /**
   * Get {@link Property} by it's getter, setter or compute method.
   *
   * @param method the getter, setter or compute method
   * @return the property associated with the method
   */
  public Property getProperty(Method method) {
    Preconditions.checkNotNull(method);
    return getProperty(methods.get(method.getName()));
  }

  /**
   * Get the property of the name field.
   *
   * <p>A name field annotated with {@ NameColumn} or a field with name <code>name</code> is
   * considered name field.
   *
   * @return a property
   */
  public Property getNameField() {
    if (nameField != null) {
      return nameField;
    }
    for (Property property : fields.values()) {
      if (property.isNameColumn()) {
        return nameField = property;
      }
    }
    return nameField = getProperty("name");
  }

  /**
   * Get all the {@ Sequence} fields.
   *
   * @return copy of the original set of fields.
   */
  public Property[] getSequenceFields() {
    return sequenceFields.toArray(new Property[] {});
  }

  /**
   * Find the fields directly accessed by the compute method of the given computed property.
   *
   * @param property the computed property
   * @return set of fields accessed by computed property
   */
  /*
  public Set<String> getComputeDependencies(Property property) {
    Preconditions.checkNotNull(property);
    if (computeDependencies == null) {
      computeDependencies = findComputeDependencies();
    }
    return computeDependencies.computeIfAbsent(
        property.getName(),
        key ->
            Optional.ofNullable(beanClass.getSuperclass())
                .map(Mapper::of)
                .map(mapper -> mapper.getComputeDependencies(property))
                .orElse(Collections.emptySet()));
  }

  private Map<String, Set<String>> findComputeDependencies() {
    final String className = beanClass.getName().replace('.', '/');
    final ClassReader reader;
    try {
      reader = new ClassReader(ResourceUtils.getResourceStream(className + ".class"));
    } catch (IOException e) {
      return new HashMap<>();
    }

    final ClassNode node = new ClassNode();
    reader.accept(node, 0);

    return ((List<?>) node.methods)
        .stream()
            .map(m -> (MethodNode) m)
            .filter(m -> Modifier.isProtected(m.access))
            .filter(m -> m.name.startsWith(PREFIX_COMPUTE))
            .filter(m -> methods.containsKey(m.name))
            .collect(
                Collectors.toMap(
                    m -> methods.get(m.name),
                    m -> {
                      return Arrays.stream(m.instructions.toArray())
                          .filter(n -> n.getOpcode() == Opcodes.GETFIELD)
                          .filter(n -> n instanceof FieldInsnNode)
                          .map(n -> (FieldInsnNode) n)
                          .filter(n -> !n.name.equals(methods.get(m.name)))
                          .map(n -> n.name)
                          .collect(Collectors.toSet());
                    }));
  }

   */

  /**
   * Get the bean class this mapper operates on.
   *
   * @return the bean class
   */
  public Class<?> getBeanClass() {
    return beanClass;
  }

  /**
   * Get the getter method of the given property.
   *
   * @param name name of the property
   * @return getter method or null if property is write-only
   */
  public Function getGetter(String name) {
    Property property = getProperty(name);
    return property != null ? property.getGetter() : null;
  }

  /**
   * Get the setter method of the given property.
   *
   * @param name name of the property
   * @return setter method or null if property is read-only
   */
  public Setter getSetter(String name) {
    Property property = getProperty(name);
    return property != null ? property.getSetter() : null;
  }

  /**
   * Get the value of given property from the given bean. It returns <code>null</code> if property
   * doesn't exist.
   *
   * @param bean the bean
   * @param name name of the property
   * @return property value
   */
  public Object get(Object bean, String name) {
    Preconditions.checkNotNull(bean);
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.trim().equals(""));

    try {
      Property property = getProperty(name);
      if (property.getParent() != null) {
        bean = getValue(bean, property.getParent());
        return property.get(bean);
      }
      checkInstance(bean, name);
      return getValue(bean, property);
    } catch (Exception e) {
      return null;
    }
  }

  Object getValue(Object bean, Property property){
    try{
      return property.getGetter().apply(bean);
    } catch (Exception e) {
      LOGGER.warn("get {} of bean {} cause error: {}", property.getName(), bean, e.getMessage());
    }
    return null;
  }

  Object getRelatedBean(Object bean, Property property, boolean createIfNull) {
    try {
      Object value = getValue(bean, property);
      if (value == null && createIfNull) {
        value = property.getTarget().getDeclaredConstructor().newInstance();
        setValue(bean, property, property.getName(), value);
      }
      return value;
    } catch (Exception ex) {
      Throwable e = ex;
      while (e.getCause() != null) {
        e = e.getCause();
      }
      LOGGER.warn("getRelatedBean for property: {} of bean {} cause error: {}", property.getName(), bean, e.getMessage());
    }
    return null;
  }

  /**
   * Set the property of the given bean with the provided value.
   *
   * @param bean the bean
   * @param name name of the property
   * @param value value for the property
   * @return old value of the property
   */
  public Object set(Object bean, String name, Object value) {
    Preconditions.checkNotNull(bean);
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.trim().equals(""));
    Property property = getProperty(name);

    if (property != null && property.getParent() != null) {
      try {
        Object parentValue = getRelatedBean(bean, property.getParent(), true);
        return property.set(parentValue, value);
      } catch (Exception e) {
        LOGGER.error(String.format("set value: %s for %s cause error on set field: %s of related property: %s",
                value, bean.getClass(), name, property.getParent().getName()+"."+property.getName()));
        throw new IllegalArgumentException(e);
      }
    }
    checkInstance(bean, name);

    return setValue(bean, property, name, value);
  }

  Object setValue(Object bean, Property property, String name, Object value) {
    if (property.getSetter() == null) {
      throw new IllegalArgumentException(
              "The bean of type: " + beanClass.getName() + " has no property called: " + property.getName());
    }

    final Object oldValue = getValue(bean, property);
    try {
      property.getSetter().apply(bean, Adapter.adapt(value, property.getJavaType(), property.getGenericType(), property));
    } catch (Exception e) {
      LOGGER.error(String.format("set value: %s for %s cause error on set value of property: %s",
              value, bean.getClass(), property.getName()));
      throw new IllegalArgumentException(e.getCause());
    }
    return oldValue;
  }
  /*
  public void createRelation(Map<String, Object> value) {
    if (hasExtendedFields) {
      Object bean = null;
      for(Map.Entry<String, Object> entry : new ArrayList<>(value.entrySet())) {
        Property property = getProperty(entry.getKey());
        if (property != null && property.getParent() != null) {
          value.remove(entry.getKey());
          if (property.getParent().isTransient())
            continue;

          try {
            Map<String, Object> parentValue =
                    (Map<String, Object>)value.get(property.getParent().getName());
            if (parentValue == null) {
              parentValue = new HashMap<>();
              if (bean == null && value.get("id") != null ) {

                bean = JpaRepository.of((Class<Model>)beanClass)
                        .find(Long.valueOf(value.get("id").toString()));
              }
              if (bean != null) {
                IdModel relatedModel =
                        (IdModel) get(bean, property.getParent().getName());

                try {
                  if (relatedModel != null) {
                    parentValue.put("id", relatedModel.getId());
                    if (relatedModel instanceof VersionModel) {
                      parentValue.put("version", ((VersionModel)relatedModel).getVersion());
                    }
                  }
                } catch (Exception ex){
                    LOGGER.warn(ex.toString());
                }
              }
              value.put(property.getParent().getName(), parentValue);
            }
            parentValue.put(property.getName(), entry.getValue());
            parentValue.put("$edited", Boolean.TRUE);
            LOGGER.debug("Create relation of {} with values: {}", property.getParent().getTarget().getName(), parentValue);
          } catch (Exception e) {
            throw new IllegalArgumentException(e);
          }
        }
      }
    }
  }

   */

  /**
   * Create an object of the given class mapping the given value map to it's properties.
   *
   * @param <T> type of the bean
   * @param klass class of the bean
   * @param values value map
   * @return an instance of the given class
   */
  public static <T> T toBean(Class<T> klass, Map<String, Object> values) {
    final T bean;
    try {
      bean = klass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
    if (values == null || values.isEmpty()) {
      return bean;
    }
    final ModelMapper modelMapper = ModelMapper.of(klass);
    values.entrySet().stream()
        .forEach(e -> {
           var p = modelMapper.getProperty(e.getKey());
           if (p != null && p.getSetter() != null) {
             modelMapper.set(bean, e.getKey(), e.getValue());
           }
        } );
    return bean;
  }

  /**
   * Create a map from the given bean instance with property names are keys and their respective
   * values are map values.
   *
   * @param bean a bean instance
   * @return a map
   */
  public static Map<String, Object> toMap(Object bean) {
    if (bean == null) {
      return null;
    }
    final Map<String, Object> map = new HashMap<>();
    final ModelMapper modelMapper = ModelMapper.of(bean.getClass());
    for (Property p : modelMapper.getProperties()) {
      map.put(p.getName(), p.getParent() == null
              ? p.get(bean) : modelMapper.get(bean, p.getName()));
    }
    return map;
  }



  private void checkInstance(Object bean, String fieldName) {
    if (!beanClass.isInstance(bean)){
      throw new IllegalArgumentException(
              String.format("Bean object: %s must be instance of: %s for field: %s",
                      bean.getClass(), beanClass, fieldName));
    }
  }

  /***
   * https://github.com/marcelklehr/toposort
   * depends = {
   *         A: [],
   *         B: ['A'],
   *         C: ['A', 'B'],
   *         D: ['F'],
   *         E: ['D', 'C'],
   *         F: []
   *    }
   * return = ['F', 'D', 'A', 'B', 'C', 'E']
   *
   * @param depends
   */
  private static List<String> topSort(Map<String, List<String>> depends) {
    List<String> result = new ArrayList<>();
    List<String> keys = new ArrayList<>(depends.keySet());
    while (!keys.isEmpty()) {
      for (int i = 0; i < keys.size(); i++) {
        String key = keys.get(i);
        List<String> dependencies = depends.get(key);

        if (dependencies == null
            || dependencies.isEmpty()
            || dependencies.stream().allMatch(result::contains)) {
          keys.remove(i);
          result.add(key);
        }
      }
    }

    return result;
  }

  private static class Node {
    final String id;
    final List<String> afters = new ArrayList<>();

    Node(String id) {
      this.id = id;
    }
  }

}
