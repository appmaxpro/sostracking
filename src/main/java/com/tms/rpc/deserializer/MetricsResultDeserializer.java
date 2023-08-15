package com.tms.rpc.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeBindings;

import com.tms.api.Point;
import com.tms.api.output.MetricsResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

class MetricsResultDeserializer extends StdDeserializer<MetricsResult> {
    TypeReference<MetricsResult> ref;
    MetricsResultDeserializer() {
        super(MetricsResult.class);
    }

    @Override
    public MetricsResult deserialize(JsonParser p,
                                     DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        return Utils.readMetricsResult(node);
    }

}
