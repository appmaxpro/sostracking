package com.tms.rpc.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.TextNode;

import com.tms.api.Point;
import com.tms.api.input.MetricRequest;
import com.tms.api.input.OrderRequest;
import com.tms.api.output.FareResult;
import com.tms.api.output.MetricsResult;
import com.tms.api.output.ServiceRateResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;

public class Utils {
    private Utils(){}

    public static void addSerializers( SimpleModule module) {
        module.addDeserializer(Point.class, new PointDeserializer());
        module.addDeserializer(FareResult.class, new FareResultDeserializer());
        module.addDeserializer(MetricsResult.class, new MetricsResultDeserializer());
        module.addDeserializer(ServiceRateResult.class, new ServiceRateResultDeserializer());
        module.addDeserializer(long[].class, new LongArrayDeserializer());
    }

    static Long readLong(JsonNode node) {
        try {
            return node.isNull() ? null : node.asLong();
        } catch (Exception ex) {}
        return null;
    }

    static Integer readInteger(JsonNode node) {
        try {
            return node.isNull() ? null : node.asInt();
        } catch (Exception ex) {}
        return null;
    }

    static Double readDouble(JsonNode node) {
        try {
            return node.isNull() ? null : node.asDouble();
        } catch (Exception ex) {}
        return null;
    }

    static BigDecimal readDecimal(JsonNode node) {
        try {
            return node.isNull() ? null : new BigDecimal(node.asText());
        } catch (Exception ex) {}
        return null;

    }

    static Boolean readBoolean(JsonNode node) {
        try {
            return node.isNull() ? null : node.asBoolean();
        } catch (Exception ex) {}
        return null;
    }

    static String readString(JsonNode node) {
        try {
            return node.isNull() ? null : node.asText();
        } catch (Exception ex) {}
        return null;
    }

    static MetricsResult readMetricsResult(JsonNode node) {
        if (node == null || node.isNull())
            return null;
        List<Point> directions = readList(node.get("directions"), Utils::readPoint);
        Long distance = Utils.readLong(node.get("distance")) ;
        Long duration = Utils.readLong(node.get("duration"));
        Long trafficDuration = Utils.readLong(node.get("trafficDuration"));
        var origin = Utils.readString(node.get("origin")) ;
        var dest = Utils.readString(node.get("origin"));
        var fareValue = Utils.readDecimal(node.get("fareValue"));
        var fareCurrency = Utils.readString(node.get("fareCurrency"));

        return new MetricsResult(distance, duration, trafficDuration, directions, origin, dest,
                fareValue, fareCurrency != null ? Currency.getInstance(fareCurrency) : null);
    }

    static Point readPoint(JsonNode node) {
        return new Point(node.get("lat").asDouble(), node.get("lng").asDouble());
    }

    static Object readValue(JsonNode node) {
        if (node.isNull())
            return null;
        if (node.isArray()) {
            ArrayNode array = (ArrayNode)node;
            List result = new ArrayList<>(array.size());
            for (var i = 0; i < array.size(); i++) {
                result.add(readValue(array.get(i)));
            }
            return result;
        }
        if (node.isObject()) {
            return readMap(node);
        }

        if (node instanceof TextNode)
            return node.asText();
        if (node instanceof LongNode)
            return node.asLong();
        if (node instanceof IntNode)
            return node.asInt();
        if (node instanceof FloatNode || node instanceof DoubleNode)
            return node.asDouble();
        if (node instanceof DecimalNode)
            return node.decimalValue();

        return node.asText();
    }

    static Map<String, Object> readMap(JsonNode node) {
        if (node == null || node.isNull())
            return null;
        Map<String, Object> result = new HashMap<>();
        var iter = node.fields();
        while(iter.hasNext()) {
            var child = iter.next();
            result.put(child.getKey(), readValue(child.getValue()));
        }
        return result;
    }

    static ServiceRateResult readServiceRateResult(JsonNode node) {
        return new ServiceRateResult(readLong(node.get("id")), readDecimal(node.get("rate")),
                readString(node.get("name")), readInteger(node.get("type")));
    }

    static <T> List<T> readList(JsonNode node, Function<JsonNode, T> fn)  {
        if (node == null || node.isNull() || !node.isArray())
            return null;
        ArrayNode array = (ArrayNode)node;

        List<T> result = new ArrayList<>(array.size());
        for (var i = 0; i < array.size(); i++) {
            result.add(fn.apply(array.get(i)));
        }
        return result;
    }

    static FareResult readFareResult(JsonNode node) {
        if (node == null || node.isNull())
            return null;
        MetricsResult metrics = readMetricsResult(node.get("metrics"));
        String currency = readString(node.get("currency"));
        List<ServiceRateResult> services = readList(node.get("services"), Utils::readServiceRateResult);
        return new FareResult(metrics, currency,services);
    }

    static OrderRequest readOrderRequest(JsonNode node) {
        OrderRequest request = new OrderRequest();
        request.setMetrics(readMetricRequest(node.get("metrics")));
        request.setDateTime(readeLocalDateTime(node.get("dateTime")));
        request.setPartner(readLong(node.get("partner")));
        request.setService(readLong(node.get("service")));
        request.setUseRateAttributes(readBoolean(node.get("useRateAttributes")));
        request.setServiceItems(readLongArray(node.get("serviceItems")));
        request.setServiceRequirements(readLongArray(node.get("serviceRequirements")));
        return request;
    }

    static MetricRequest readMetricRequest(JsonNode node) {
        if (node == null) {
            return null;
        }

        MetricRequest request = new MetricRequest();
        request.setAddresses(readBoolean(node.get("addresses")));
        request.setDirections(readBoolean(node.get("directions")));
        request.setMode(readString(node.get("mode")));
        request.setTraffic(readBoolean(node.get("traffic")));
        request.setPoints(readList(node.get("points"), Utils::readPoint ));
        return request;
    }

    static long[] readLongArray(JsonNode node) {
        if (node == null || !node.isArray()) {
            return readLongArrayFromText(node);
        }

        ArrayNode array = (ArrayNode)node;
        long[] result = new long[array.size()];
        for (var i = 0; i < result.length; i++) {
            result[i] = array.get(i).asLong();
        }
        return result;
    }

    static long[] readLongArrayFromText(JsonNode node) {
        if (node == null || node.isNull())
            return null;

        String[] longStrs = node.asText().split(",");
        long[] result = new long[longStrs.length];
        for (var i = 0; i < result.length; i++) {
            result[i] = Long.parseLong(longStrs[i]);
        }
        return result;
    }

    static LocalDateTime readeLocalDateTime(JsonNode node) {
        if (node == null || node.isNull())
            return null;
        if (node.isLong())
            return LocalDateTime.ofEpochSecond(node.asLong() / 1000 , 0, ZoneOffset.UTC);
        if (node.isInt())
            return LocalDateTime.ofEpochSecond(node.asInt() , 0, ZoneOffset.UTC);

        if (node.isTextual())
            return LocalDateTime.parse(node.asText());
        return null;
    }


}
