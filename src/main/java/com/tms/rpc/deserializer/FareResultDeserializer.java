package com.tms.rpc.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tms.api.Point;

import java.io.IOException;
import com.tms.api.Point;
import com.tms.api.output.FareResult;

class FareResultDeserializer extends StdDeserializer<FareResult> {

    protected FareResultDeserializer() {
        super(FareResult.class);
    }

    @Override
    public FareResult deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JacksonException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        return Utils.readFareResult(node);
    }
}