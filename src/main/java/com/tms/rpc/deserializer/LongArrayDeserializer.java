package com.tms.rpc.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.tms.api.output.FareResult;

import java.io.IOException;

class LongArrayDeserializer extends StdDeserializer<long[]> {


    protected LongArrayDeserializer() {
        super(long[].class);
    }

    @Override
    public long[] deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = parser.readValueAsTree();
        if (!node.isArray())
            return null;
        ArrayNode array = (ArrayNode)node;
        long[] result = new long[array.size()];
        for (var i = 0; i < result.length; i++) {
            result[i] = array.get(i).asLong();
        }

        return result;
    }
}
