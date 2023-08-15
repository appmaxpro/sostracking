package com.tms.service.metrics;

import com.tms.api.input.MetricRequest;
import com.tms.api.output.MetricsResult;
import com.tms.utils.cost.CircleDistanceCalculator;
import com.tms.utils.cost.SimpleTransportCosts;
import com.tms.utils.cost.TransportCosts;

import java.util.List;

public class SimpleMetricsService implements MetricsService {

    private TransportCosts transportCosts;

    private TransportCosts getTransportCosts() {
        if (transportCosts == null) {
            transportCosts = new SimpleTransportCosts(new CircleDistanceCalculator());
        }
        return transportCosts;
    }

    public MetricsResult getMetricsResult(MetricRequest request){
        var points = request.getPoints();
        double distance = 0.0;
        double duration = 0.0;
        TransportCosts transportCosts = getTransportCosts();
        for (int i = 0; i < points.size() - 1; i++) {
            distance += transportCosts.getDistance(points.get(i), points.get(i +1), 0, 1);
            duration += transportCosts.getTransportTime(points.get(i), points.get(i +1), 0, 1);
        }
        boolean requestDirections = Boolean.TRUE.equals(request.getDirections());
        boolean requestAddresses = Boolean.TRUE.equals(request.getAddresses());
        var lastPoint = points.get(points.size() -1);
        return new MetricsResult(
                (long)Math.floor(distance),
                (long)Math.floor(duration),
                (long)Math.floor(duration),
                requestDirections ? List.copyOf(points) : null,
                requestAddresses ? String.format("%s,%s", points.get(0).getLat(), points.get(0).getLng()) : null,
                requestAddresses ? String.format("%s,%s", lastPoint.getLat(), lastPoint.getLng()) : null,
                null,
                null);

    }

}
