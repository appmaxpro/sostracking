package com.tms.rpc;


import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tms.api.Point;
import com.tms.model.Model;
/*
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;


 */
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;
import javax.inject.Singleton;


@Singleton
public class ObjectMapperProvider implements Provider<ObjectMapper> {

    private final ObjectMapper mapper;



    static class ModelSerializer extends JsonSerializer<Model> {

        @Override
        public void serialize(Model value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value != null) {
                final JsonSerializer<Object> serializer = provider.findValueSerializer(Map.class, null);
                final Map<String, Object> map = Resource.toMap(value);
                serializer.serialize(map, jgen, provider);
            }
        }
    }

    static class ZonedDateTimeSerializer extends JsonSerializer<java.time.ZonedDateTime> {

        @Override
        public void serialize(
                java.time.ZonedDateTime value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value != null) {
                jgen.writeString(value.withZoneSameInstant(ZoneOffset.UTC).toString());
            }
        }
    }

    static class LocalDateTimeSerializer extends JsonSerializer<java.time.LocalDateTime> {

        @Override
        public void serialize(
                java.time.LocalDateTime value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value != null) {
                jgen.writeString(
                        value
                                .atZone(ZoneOffset.systemDefault())
                                .withZoneSameInstant(ZoneOffset.UTC)
                                .toString());
            }
        }
    }

    static class LocalTimeSerializer extends JsonSerializer<java.time.LocalTime> {

        @Override
        public void serialize(
                java.time.LocalTime value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value != null) {
                jgen.writeString(value.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        }
    }

    static class DecimalSerializer extends JsonSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value != null) {
                jgen.writeString(value.toPlainString());
            }
        }
    }

    static class DecimalDeserializer extends JsonDeserializer<BigDecimal> {

        @Override
        public BigDecimal deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
            switch (parser.currentTokenId())
            {
                case JsonTokenId.ID_NUMBER_FLOAT:
                    return parser.getDecimalValue();
                case JsonTokenId.ID_NUMBER_INT:
                    return new BigDecimal(parser.getLongValue());
                case JsonTokenId.ID_STRING:
                    return new BigDecimal(parser.getText());

            }
            return null;
        }
    }


    static ObjectMapper createObjectMapper() {
        return createObjectMapper(new ModelSerializer());
    }

    static ObjectMapper createObjectMapper(JsonSerializer<Model> modelSerializer) {

        final ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        SimpleModule module = new SimpleModule("MyModule");
        module.addSerializer(Model.class, modelSerializer);
        //module.addSerializer(GString.class, new GStringSerializer());
        module.addSerializer(BigDecimal.class, new DecimalSerializer());
        com.tms.rpc.deserializer.Utils.addSerializers(module);
        module.addDeserializer(BigDecimal.class, new DecimalDeserializer());

        //module.setSerializerModifier(new ListSerializerModifier());

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(java.time.ZonedDateTime.class, new ZonedDateTimeSerializer());
        javaTimeModule.addSerializer(java.time.LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addSerializer(java.time.LocalTime.class, new LocalTimeSerializer());

        mapper.registerModule(module);
        //mapper.registerModule(javaTimeModule);
        mapper.registerModule(new Jdk8Module());
        //mapper.registerModule(new ParameterNamesModule());
        //mapper.registerModule(new GuavaModule());


        //GeolatteGeomModule geolatteGeomModule = new GeolatteGeomModule();
        //geolatteGeomModule.set(Setting.IGNORE_CRS, true);
        //mapper.registerModule(geolatteGeomModule);

        return mapper;
    }

    public ObjectMapperProvider() {
        this.mapper = createObjectMapper();
    }

    @Override
    public ObjectMapper get() {
        return mapper;
    }
}