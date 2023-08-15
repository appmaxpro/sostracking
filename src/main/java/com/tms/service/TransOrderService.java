package com.tms.service;

import com.tms.api.Point;
import com.tms.api.input.OrderRequest;
import com.tms.api.output.FareResult;
import com.tms.api.output.MetricsResult;
import com.tms.api.output.ServiceRateResult;
import com.tms.exceptions.ForbiddenException;
import com.tms.exceptions.NoServiceFoundException;
import com.tms.model.base.OrderServiceItemSet;
import com.tms.model.base.Partner;
import com.tms.model.fleet.Driver;
import com.tms.service.driver.DriverService;
import com.tms.TmsStore;
import com.tms.model.TransService;
import com.tms.service.metrics.MetricsService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class TransOrderService {

    @Inject
    private TransZonePricingService transZonePricingService;
    @Inject
    private MetricsService metricsService;
    @Inject
    private TmsStore tmsStore;
    @Inject
    private PartnerPriceListService priceListService;
    @Inject
    private TransPricingService transPricingService;
    @Inject
    private DriverService driverService;

    @Inject
    private PartnerJournalService partnerJournalService;

    public FareResult calculateFare(OrderRequest request) {
        if (request.getDateTime() == null) {
            request.setDateTime(LocalDateTime.now());
        }
        OrderContext context =
                new OrderContext(request, tmsStore, null);

        if (request.getServiceItems() != null && request.getServiceItems().length > 0) {
            context.requestServiceItemSet =
                    OrderServiceItemSet.getOrderServiceItemSet(tmsStore, request.getServiceItems());
        }

        if (request.getServiceRequirements() != null && request.getServiceRequirements().length > 0) {
            context.requestServiceRequirements =
                    OrderServiceItemSet.getOrderServiceItemSet(tmsStore, request.getServiceRequirements());
        }



        return transPricingService.calculateFare(context);
    }

    public ServiceRateResult createServiceRateResult(OrderRequest request) {
        if (request.getDateTime() == null) {
            request.setDateTime(LocalDateTime.now());
        }
        OrderContext context =
                new OrderContext(request, tmsStore, null);
        return createServiceRateResult(context);
    }

    protected ServiceRateResult createServiceRateResult(OrderContext context) {
        var transService = tmsStore.getObject(TransService.class, context.request.getService());

        var serviceRateResult = transPricingService.calculateServiceRate(transService, context);

        if (serviceRateResult == null) {
            throw new NoServiceFoundException();
        }

        if (context.metrics == null) {
            context.metrics = MetricsResult.EMPTY_METRICS;
        }

        var metrics = context.metrics;

        if (transService.getMaxDistance() > 0
                && metrics.getDistance() > 0
                && metrics.getDistance() > transService.getMaxDistance())
            throw new ForbiddenException("DISTANCE_TOO_FAR");

        if (transService.getMinDistance() > 0
                && metrics.getDistance() > 0
                && metrics.getDistance() < transService.getMaxDistance())
            throw new ForbiddenException("DISTANCE_TOO_FAR");





        BigDecimal requiredPrePay = BigDecimal.ZERO;

        if (serviceRateResult.getType() != Partner.TYPE_BUSINESS
                && transService.getPerPayPercent() != null) {
            var balance = partnerJournalService.getPartnerBalance(context.request.getPartner());
            var amountNeedsToBePrePaid = serviceRateResult.getRate()
                    .multiply (transService.getPerPayPercent())
                    .divide(new BigDecimal(100));

            if (balance.subtract(amountNeedsToBePrePaid)
                    .compareTo(BigDecimal.ONE) < 0){
                requiredPrePay =
                        balance.subtract(amountNeedsToBePrePaid);

            }
        }
        if (requiredPrePay != BigDecimal.ZERO)
            serviceRateResult.setAttr("requiredPrePay", requiredPrePay);


        return serviceRateResult;

    }

    public void cancelOrder(OrderContext context) {

    }

    private Map<Driver, Point> filterDrivers (OrderContext context, TransService transService) {
        Map<Driver, Point> driverLocations =
                driverService.getCloses(transService,
                        context.request.getMetrics().getPoints().get(0));

        long requestServiceItemSetId = context.requestServiceItemSet != null
                ? context.requestServiceItemSet.getId() : 0;

        long requestServiceRequirementsId = context.requestServiceRequirements != null
                ? context.requestServiceRequirements.getId() : 0;

        driverLocations.entrySet()
                .stream()
                .filter(entry -> {
                    var driverOrderServiceItemSet
                            = entry.getKey().getServiceItemSet(tmsStore);
                    if (driverOrderServiceItemSet == null || !driverOrderServiceItemSet.containsItem()) {

                    }



                })
        for (var driverLocation: driverLocations.entrySet()) {
            driverLocation.
        }

    }

    boolean driverMatches(Driver driver, OrderContext context, TransService transService) {
        var driverOrderServiceItemSet
                = driver.getServiceItemSet(tmsStore);
        if (driverOrderServiceItemSet == null ||
                !driverOrderServiceItemSet.containsService(transService.getOrderServiceItem())) {

        }
    }


}
