package com.tms.service.metrics;


import com.tms.api.input.MetricRequest;
import com.tms.api.output.MetricsResult;

public interface MetricsService {

    MetricsResult getMetricsResult(MetricRequest request);
}
