package com.tms.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.api.input.OrderRequest;
import com.tms.service.IRemoteTrackingService;
import com.tms.service.TransOrderService;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class RemoteRemoteTrackingServiceImpl implements IRemoteTrackingService {

    @Inject
    private TransOrderService orderService;
    @Inject
    private ObjectMapper objectMapper;

    @Override
    public String getFareResult(String rawRequest) {
        try {
            OrderRequest request = objectMapper.readValue(rawRequest, OrderRequest.class);
            var result = orderService.calculateFare(request);
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createServiceRateResult(String rawRequest) {
        try {
            OrderRequest request = objectMapper.readValue(rawRequest, OrderRequest.class);
            var result = orderService.createServiceRateResult(request);
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
