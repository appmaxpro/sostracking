package com.tms.rpc.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tms.api.input.OrderRequest;
import com.tms.api.output.FareResult;

import java.io.IOException;

public class OrderRequestDeserializer extends StdDeserializer<OrderRequest> {

    protected OrderRequestDeserializer() {
        super(OrderRequest.class);
    }

    @Override
    public OrderRequest deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JacksonException {
        return Utils.readOrderRequest(p.readValueAsTree());
    }
}