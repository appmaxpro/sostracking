package com.tms.service;

import com.tms.model.*;
import com.tms.TmsStore;
import com.tms.api.output.FareResult;
import com.tms.api.output.MetricsResult;
import com.tms.api.output.ServiceRateResult;
import com.tms.model.base.GeoZoneGroup;
import com.tms.model.base.Partner;
import com.tms.model.types.TransRateRuleFactor;
import com.tms.api.Point;
import com.tms.service.metrics.MetricsService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TransPricingService {
    @Inject
    private TransZonePricingService transZonePricingService;
    @Inject
    private MetricsService metricsService;
    @Inject
    private TmsStore tmsStore;
    @Inject
    private PartnerPriceListService priceListService;
    @Inject
    private TransServiceService transServiceService;

    public FareResult calculateFare(OrderContext context) {
        var points = context.request.getMetrics().getPoints();
        var date = context.request.getDateTime().toLocalDate();

        if (context.request.getPartner() != null && context.request.getPartner() > 0) {
            var partner = tmsStore.of(Partner.class, context.request.getPartner());
            if (partner.isPresent()) {
                var priceList = partner.get().getSalesPartnerPriceList(tmsStore);
                if (priceList != null) {
                    context.partnerType = partner.get().getType();
                    return priceListService.calculateFare(priceList, context);
                }
            }
        }

        var services = getServicesForPoint(points.get(0));

        if (context.metrics == null) {

            boolean useDistanceLimit =
                    services.stream().anyMatch( transService ->
                        transService.getMinDistance() > 0
                                || transService.getMaxDistance() > 0
                                || (transService.getRatePer100m() != null
                                && transService.getRatePer100m().compareTo(BigDecimal.ZERO) > 0)
                    );
            var metrics = useDistanceLimit
                    ? metricsService.getMetricsResult(context.request.getMetrics())
                    : MetricsResult.EMPTY_METRICS;

            context.setMetrics(metrics);
        }

        MetricsResult metrics = context.metrics;
        long distance = context.metrics.getDistance() ;
        int distanceInKm = (int) distance/ 1000;

        List<ServiceRateResult> serviceRateResults = new ArrayList<>();
        for(var service : services){

            if (service.getMinDistance() > 0 && distanceInKm < service.getMinDistance() )
                continue;

            if (service.getMaxDistance() > 0 && distanceInKm > service.getMaxDistance() )
                continue;

            var serviceRateResult =
                    transServiceService.calculateServiceRate(service, context);

            if (serviceRateResult != null){
                serviceRateResults.add(serviceRateResult);
            }
        }

        return new FareResult(
                context.metrics, (String) context.get("currency"), serviceRateResults
        );
    }

    public ServiceRateResult calculateServiceRate(TransService transService, OrderContext context) {
        ServiceRateResult serviceRateResult = null;
        boolean isBusiness = false;
        if (context.request.getPartner() != null && context.request.getPartner() > 0) {
            var partner = tmsStore.of(Partner.class, context.request.getPartner());

            if (partner.isPresent()) {
                var priceList = partner.get().getSalesPartnerPriceList(tmsStore);
                if (priceList != null) {
                    isBusiness = true;
                    context.partnerType = partner.get().getType();
                    serviceRateResult = priceListService.calculateServiceRate(priceList, transService, context);
                }
            }
        }
        if (!isBusiness) {
            serviceRateResult = transServiceService.calculateServiceRate(transService, context);
        }

        return serviceRateResult;
    }

    static BigDecimal getTransPricing(PricingRuleContainer pricing, OrderContext context) {

        long distance = context.metrics.getDistance() ;
        int distanceInKm = (int) distance/ 1000;

        if (distanceInKm < pricing.getMinDistance() )
            return null;

        if (distanceInKm > pricing.getMaxDistance() )
            return null;

        BigDecimal base = pricing.getBaseRate();
        BigDecimal price = base;
        if (pricing.getRatePer100m().compareTo(BigDecimal.ZERO) > 0) {
            price = price.add(pricing.getRatePer100m().multiply(new BigDecimal(distance / 100)));
        }
        if (pricing.getRatePerMinute().compareTo(BigDecimal.ZERO) > 0) {
            var duration = Math.max(context.metrics.getTrafficDuration(), context.metrics.getDuration()) / 60;
            BigDecimal minutePrice = pricing.getRatePerMinute()
                    .multiply(new BigDecimal(duration));

            price = price.max(minutePrice);
        }

        context.set("price", price);
        BigDecimal accumulate = BigDecimal.ZERO;
        boolean found = false;
        for (var rule : pricing.getRateRuleList(context.store)) {
            var result = getRateRulePrice(rule, context);
            if (result != null) {
                found = true;
                if (rule.isAccumulate()) {
                    accumulate = accumulate.add(result);
                } else {
                    accumulate = result;
                    break;
                }
            }
        }
        if (found) {
            price = accumulate;
        }

        LocalTime currentTime = context.request.getDateTime()
                .toLocalTime();

        for (var rule : pricing.getTimeRateRuleList(context.store)) {
            if ((currentTime.equals(rule.getStartTime()) ||
                    currentTime.isAfter(rule.getStartTime())) &&
                    (currentTime.equals(rule.getEndTime()) ||
                            currentTime.isBefore(rule.getEndTime())) ) {
                price = price.add(price.multiply(rule.getMultiple())) ;
                break;
            }
        }

        Double trafficRate = (Double) context.get("traffic");
        if (trafficRate != null) {
            BigDecimal maxTrafficRate = BigDecimal.ZERO;
            for (var rule : pricing.getTrafficRateRuleList(context.store)) {
                if(rule.getTrafficValue() >=  trafficRate){
                    maxTrafficRate = rule.getMultiple().max(maxTrafficRate);
                }
            }

            price = price.multiply(maxTrafficRate);
        }

        return price;
    }


    static BigDecimal getRateRulePrice(TransRateRule rule, OrderContext context) {

        var value = context.get(rule.getVariable().getValue());
        if (value == null || !(value instanceof Number))
            return null;

        var input = rule.getInput();
        var decimalValue = value instanceof BigDecimal
                ?   (BigDecimal) value
                : new BigDecimal(((Number) value).doubleValue())
                .setScale(input.scale());


        boolean matches = false;
        switch (rule.getOp()) {
            case EQ:
                matches = input.equals(decimalValue);
                break;
            case GT:
                matches = decimalValue.compareTo(input) > 0;
                break;
            case GTE:
                matches = decimalValue.compareTo(input) >= 0;
                break;
            case LT:
                matches = decimalValue.compareTo(input) < 0;
                break;
            case LTE:
                matches = decimalValue.compareTo(input) <= 0;
                break;

        }
        if (matches) {
            var result = rule.getFactor() == TransRateRuleFactor.DEF
                    ? decimalValue.subtract(input).multiply(rule.getMultiple())
                    : decimalValue.multiply(rule.getMultiple());

            return result;
        }
        return null;
    }


    public List<TransService> getServicesForPoint(Point point) {
        List<TransService> availableServices = new ArrayList<>();
        var allTransServices = tmsStore.getAll(TransService.class);
        synchronized (allTransServices){
            for(var service : allTransServices) {
                if (service.isUseForPublic()) {
                    GeoZoneGroup group = service.getZoneGroup(tmsStore);
                    if (group != null && group.contains(tmsStore, point.getLat(), point.getLng())) {
                        availableServices.add(service);
                    }
                }
            }
        }
        return availableServices;
    }




}
