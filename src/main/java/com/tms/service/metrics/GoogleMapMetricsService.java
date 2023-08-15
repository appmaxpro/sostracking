package com.tms.service.metrics;

import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.tms.api.Point;
import com.tms.api.input.MetricRequest;
import com.tms.api.output.MetricsResult;
import com.tms.inject.Beans;
import com.tms.utils.ValueRef;
import org.traccar.config.Config;

import javax.inject.Singleton;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class GoogleMapMetricsService implements MetricsService {

    private GeoApiContext context;
    private boolean showDirections;
    private boolean useTrafficFeature;
    private GeoApiContext getGeoApiContext() {
        if (context == null) {
            Config conf = Beans.get(Config.class);
            String apiKey = conf.getString("google.service.apiKey");
            context = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();

            showDirections = Boolean.TRUE.equals(conf.getString("map.show.directions"));
            useTrafficFeature = Boolean.TRUE.equals(conf.getString("map.traffic.feature"));
        }
        return context;
    }

    @Override
    public MetricsResult getMetricsResult(MetricRequest request) {
        var useTraffic = useTrafficFeature && Boolean.TRUE.equals(request.getTraffic());
        var points = request.getPoints();
        ValueRef<Long> distanceValue = ValueRef.ofLong(0l);
        ValueRef<Long> durationValue = ValueRef.ofLong(0l);
        ValueRef<Long> trafficDurationValue = ValueRef.ofLong(0l);

        List<Point> directions;
        String origin = null;
        String dest = null;

        ValueRef<BigDecimal> fareValue = ValueRef.ofDecimal(null) ;
        ValueRef<Currency> fareCurrency = ValueRef.of(null);

        for (int i = 0; i < points.size() - 1; i++) {
            try {

                var dmRequest = DistanceMatrixApi.newRequest(getGeoApiContext())
                        .origins(new LatLng(points.get(i).getLat(), points.get(i).getLng()))
                        .destinations(new LatLng(points.get(i + 1).getLat(), points.get(i + 1).getLng()))
                        .units(Unit.METRIC);
                if (useTraffic)
                    dmRequest.mode(TravelMode.DRIVING);

                var matrix = dmRequest.await();

                Arrays.stream(matrix.rows[0].elements).filter(element ->
                        element.status.equals(DistanceMatrixElementStatus.OK) )
                        .forEach(el -> {
                            distanceValue.add(el.distance.inMeters);
                            durationValue.add(el.duration.inSeconds);
                            if (useTraffic && el.durationInTraffic != null) {
                                trafficDurationValue.add(el.durationInTraffic.inSeconds);
                            }

                            if (el.fare != null) {
                                fareValue.add(el.fare.value);
                                fareCurrency.set(el.fare.currency);
                            }

                        });
                if (Boolean.TRUE.equals(request.getAddresses())) {
                    origin = matrix.originAddresses[0];
                    dest = matrix.destinationAddresses[0];
                }

            } catch (ApiException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
           directions = getDirections(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

         return new MetricsResult(
                 distanceValue.get(),
                 durationValue.get(),
                 trafficDurationValue.get(),
                 directions,
                 origin,
                 dest,
                 fareValue.get(),
                 fareCurrency.get());

    }


    private List<Point> getDirections(MetricRequest request)
            throws IOException, InterruptedException, ApiException {
        if (Boolean.TRUE.equals(request.getDirections())) {
            var points = request.getPoints();
            if (showDirections) {

                var directions = DirectionsApi.newRequest(getGeoApiContext())
                        .origin(new LatLng(points.get(0).getLat(), points.get(0).getLng()))
                        .destination(new LatLng(points.get(points.size() - 1).getLat(),
                                points.get(points.size() - 1).getLng()))
                        .waypoints(points.size() > 2
                                ? points.subList(1, points.size() - 2)
                                .stream()
                                .map(p -> new LatLng(p.getLat(), p.getLng()))
                                .toArray(LatLng[]::new)
                                : new LatLng[]{})
                        .await();

                if (directions.geocodedWaypoints.length > 0) {
                    return directions.routes[0].overviewPolyline
                            .decodePath().stream()
                            .map(latLng -> new Point(latLng.lat, latLng.lng))
                            .collect(Collectors.toList());
                }

            }
            return List.copyOf(points);
        }
        return null;
    }

}
