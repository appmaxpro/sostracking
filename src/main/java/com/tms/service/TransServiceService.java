package com.tms.service;

import com.tms.api.output.MetricsResult;
import com.tms.api.output.ServiceRateResult;
import com.tms.model.base.Partner;
import com.tms.model.TransPricing;
import com.tms.model.TransZonePricing;
import com.tms.utils.Utils;
import com.tms.TmsStore;
import com.tms.model.TransService;
import com.tms.service.metrics.MetricsService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransServiceService {

    @Inject
    private TransZonePricingService transZonePricingService;
    @Inject
    private MetricsService metricsService;
    @Inject
    private TmsStore tmsStore;

    public ServiceRateResult calculateServiceRate(TransService transService, OrderContext context) {
        var points = context.request.getMetrics().getPoints();
        var date = context.request.getDateTime().toLocalDate();
        TransZonePricing zonePricing = null;
        if (points.size() == 2) {
            zonePricing =
                    transZonePricingService.getTransZonePricing(
                            transService,
                            points.get(0),
                            points.get(1),
                            date
                    );

            if (zonePricing != null &&
                    zonePricing.getTypeSelect() == TransZonePricing.TYPE_REPLACE) {
                return new ServiceRateResult(
                        transService.getId(),
                        zonePricing.getPrice(),
                        transService.getName(),
                        Partner.TYPE_PERSONAL,
                        Boolean.TRUE.equals(context.request.getUseRateAttributes())
                         ? Map.of("zonePricingId", zonePricing.getId())
                         : null
                );
            }

        }


        List<TransPricing> transPricingList =
                transService.isUseAdvancedPricing()
                    ? transService.getTransPricingList(tmsStore)
                            .stream()
                            .filter(pricing -> Utils.isAfter(date, pricing.getStartDate()) &&
                                    Utils.isAfter(pricing.getEndDate(), date))
                            .collect(Collectors.toList())
                    : Collections.EMPTY_LIST;


        if (context.metrics == null) {

            boolean useDistanceLimit =
                    transService.getMinDistance() > 0
                        || transService.getMaxDistance() > 0
                        || (transService.getRatePer100m() != null
                            && transService.getRatePer100m().compareTo(BigDecimal.ZERO) > 0)
                        ||transPricingList.stream()
                            .anyMatch(pricing -> pricing.getMinDistance() > 0
                                    || pricing.getMaxDistance() > 0
                                    || (pricing.getRatePer100m() != null
                                    && pricing.getRatePer100m().compareTo(BigDecimal.ZERO) > 0));

            var metrics = useDistanceLimit
                    ? metricsService.getMetricsResult(context.request.getMetrics())
                    : MetricsResult.EMPTY_METRICS;

            context.setMetrics(metrics);
        }
        if (transService.isUseAdvancedPricing()) {
            for (var pricing : transPricingList) {
                BigDecimal price = TransPricingService.getTransPricing(pricing, context);

                if (price != null) {
                    Map<String, Object> attributes =
                            Boolean.TRUE.equals(context.request.getUseRateAttributes())
                                    ? Map.of("zonePricingId", zonePricing.getId())
                                    : null;

                    if (zonePricing != null) {
                        if (zonePricing.getTypeSelect() == TransZonePricing.TYPE_ADDITIONNAL) {
                            price = price.add(zonePricing.getPrice());
                        } else {
                            price = price.subtract(zonePricing.getPrice());
                        }
                        if (attributes != null)
                            attributes.put("zonePricingId", zonePricing.getId());
                    }
                    return new ServiceRateResult(
                            transService.getId(),
                            price,
                            transService.getName(),
                            Partner.TYPE_PERSONAL,
                            attributes
                    );
                }
            }
        }

        long distance = context.metrics.getDistance() ;
        int distanceInKm = (int) distance/ 1000;

        if (distanceInKm < transService.getMinDistance() )
            return null;

        if (distanceInKm > transService.getMaxDistance() )
            return null;

        BigDecimal base = transService.getBaseRate();
        BigDecimal price = base;
        if (transService.getRatePer100m().compareTo(BigDecimal.ZERO) > 0) {
            price = price.add(transService.getRatePer100m().multiply(new BigDecimal(distance / 100)));
        }

        if (transService.getRatePerMinute().compareTo(BigDecimal.ZERO) > 0) {
            var duration = Math.max(context.metrics.getTrafficDuration(), context.metrics.getDuration()) / 60;
            BigDecimal minutePrice = transService.getRatePerMinute()
                    .multiply(new BigDecimal(duration));

            price = price.max(minutePrice);
        }

        return new ServiceRateResult(
                transService.getId(),
                price,
                transService.getName(),
                Partner.TYPE_PERSONAL
        );
    }

}
