package com.tms.service;

import com.tms.api.output.FareResult;
import com.tms.api.output.MetricsResult;
import com.tms.api.output.ServiceRateResult;
import com.tms.model.*;
import com.tms.model.base.PartnerPriceList;
import com.tms.utils.Utils;
import com.tms.utils.primitive.Primitive;
import com.tms.utils.primitive.PrimitiveLongSet;
import com.tms.TmsStore;
import com.tms.service.metrics.MetricsService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class PartnerPriceListService {
    @Inject
    private TransZonePricingService transZonePricingService;
    @Inject
    private MetricsService metricsService;
    @Inject
    private TmsStore tmsStore;

    public FareResult calculateFare(PartnerPriceList priceList, OrderContext context) {
        var points = context.request.getMetrics().getPoints();
        var date = context.request.getDateTime().toLocalDate();
        List<TransZonePricing> zonePricingList = null;
        PrimitiveLongSet availableServiceIds = Primitive.longSet();
        if (points.size() == 2) {

            zonePricingList =
                    transZonePricingService.getTransZonePricingList(
                            priceList.getTransZonePricingList(tmsStore),
                            points.get(0),
                            points.get(1),
                            date
                    );
            if (!zonePricingList.isEmpty()) {
                zonePricingList.stream()
                        .filter(zonePricing -> zonePricing.getTypeSelect() == TransZonePricing.TYPE_REPLACE)
                        .forEach(zonePricing ->
                                availableServiceIds.add(zonePricing.getTransService()));
            }
        }

        List<TransPricing> transPricingList =
                priceList.getTransPricingList(tmsStore)
                        .stream()
                        .filter(pricing ->
                                Utils.isAfter(date, pricing.getStartDate()) &&
                                Utils.isAfter(pricing.getEndDate(), date) &&
                                !availableServiceIds.contains(pricing.getTransService()))
                        .peek(pricing -> availableServiceIds.add(pricing.getTransService()))
                        .collect(Collectors.toList());

        long[] serviceIds = Utils.toLongArray(availableServiceIds);
        Collection<TransService> availableServices =
                tmsStore.getObjects(TransService.class, serviceIds);

        boolean useDistanceLimit = transPricingList.stream()
                .anyMatch(pricing -> pricing.getMinDistance() > 0
                        || pricing.getMaxDistance() > 0
                        || (pricing.getRatePer100m() != null
                                && pricing.getRatePer100m().compareTo(BigDecimal.ZERO) > 0))
                || availableServices.stream()
                    .anyMatch(service -> service.getSearchRadius() > 0);

        if (context.metrics == null) {
            var metrics = useDistanceLimit
                    ? metricsService.getMetricsResult(context.request.getMetrics())
                    : MetricsResult.EMPTY_METRICS;

            context.setMetrics(metrics);
        }
        long distance = context.metrics.getDistance() ;
        int distanceInKm = (int) distance/ 1000;

        List<ServiceRateResult> serviceRateResults = new ArrayList<>();
        MAIN_LOOP:
        for(var service : availableServices){
            if (service.getSearchRadius() > distanceInKm) {
                continue ;
            }
            TransZonePricing zonePrice = null;
            if (zonePricingList != null) {
                zonePrice = Utils.findOne(zonePricingList, zone -> zone.getTransService() == service.getId());

                if (zonePrice != null) {
                    if (zonePrice.getTypeSelect() == TransZonePricing.TYPE_REPLACE) {
                        serviceRateResults.add(new ServiceRateResult(
                                service.getId(),
                                zonePrice.getPrice(),
                                service.getName(),
                                context.partnerType
                        ));
                        continue;
                    }
                }
            }

            for(var pricing : transPricingList) {
                if (pricing.getTransService() == service.getId()) {
                    BigDecimal price = TransPricingService.getTransPricing(pricing, context);
                    if (price != null) {
                        if (zonePrice != null) {
                            if (zonePrice.getTypeSelect() == TransZonePricing.TYPE_ADDITIONNAL) {
                                price = price.add(zonePrice.getPrice());
                            } else {
                                price = price.subtract(zonePrice.getPrice());
                            }
                        }
                        serviceRateResults.add(new ServiceRateResult(
                                service.getId(),
                                price,
                                service.getName(),
                                context.partnerType
                        ));
                        continue MAIN_LOOP;
                    }
                }
            }
        }
        return new FareResult(
                context.metrics, (String) context.get("currency"), serviceRateResults
        );
    }

    public ServiceRateResult calculateServiceRate(PartnerPriceList priceList,
                                                  TransService transService,
                                                  OrderContext context) {
        var points = context.request.getMetrics().getPoints();
        var date = context.request.getDateTime().toLocalDate();
        TransZonePricing zonePricing = null;
        if (points.size() == 2) {
            zonePricing =
                    transZonePricingService.getTransZonePricing(
                            priceList,
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
                        context.partnerType,
                        Boolean.TRUE.equals(context.request.getUseRateAttributes())
                                ? Map.of("zonePricingId", zonePricing.getId())
                                : null);
            }

        }


        List<TransPricing> transPricingList =
                priceList.getTransPricingList(tmsStore)
                        .stream()
                        .filter(pricing ->
                                Utils.isAfter(date, pricing.getStartDate()) &&
                                Utils.isAfter(pricing.getEndDate(), date))
                        .collect(Collectors.toList());


        if (context.metrics == null) {

            boolean useDistanceLimit = transPricingList.stream()
                            .anyMatch(pricing -> pricing.getMinDistance() > 0
                                    || pricing.getMaxDistance() > 0
                                    || (pricing.getRatePer100m() != null
                                    && pricing.getRatePer100m().compareTo(BigDecimal.ZERO) > 0));

            var metrics = useDistanceLimit
                    ? metricsService.getMetricsResult(context.request.getMetrics())
                    : MetricsResult.EMPTY_METRICS;

            context.setMetrics(metrics);
        }

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
                        context.partnerType,
                        attributes
                );
            }
        }
        return null;
    }


}
