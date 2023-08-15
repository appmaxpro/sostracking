package com.tms.service;

import com.tms.utils.Utils;
import com.tms.TmsStore;
import com.tms.model.base.GeoZone;
import com.tms.model.base.PartnerPriceList;
import com.tms.model.TransService;
import com.tms.model.TransZonePricing;
import com.tms.api.Point;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TransZonePricingService {

    @Inject
    private TmsStore tmsStore;


    public TransZonePricing getTransZonePricing(TransService service, Point p1, Point p2, LocalDate date){
        if (service.isUseZonePricing()) {
            return getMatchesTransZonePricing(service.getTransZonePricingList(tmsStore), p1, p2, date);
        }
        return null;
    }

    public TransZonePricing getTransZonePricing(PartnerPriceList service, Point p1, Point p2, LocalDate date){
        return getMatchesTransZonePricing(service.getTransZonePricingList(tmsStore), p1, p2, date);
    }

    private TransZonePricing getMatchesTransZonePricing(Collection<TransZonePricing> zonePricingList,
                                    Point p1, Point p2, LocalDate date){

        return Utils.findOne(zonePricingList, pricing ->  zonePricingMatches(pricing, p1, p2, date));
    }

    public List<TransZonePricing> getTransZonePricingList(Collection<TransZonePricing> zonePricingList,
                                                          Point p1, Point p2, LocalDate date){

        return zonePricingList.stream().filter(pricing -> zonePricingMatches(pricing, p1, p2, date))
                .collect(Collectors.toList());
    }

    boolean zonePricingMatches(TransZonePricing pricing, Point p1, Point p2, LocalDate date) {
        if (Utils.isAfter(date, pricing.getStartDate()) && Utils.isAfter(pricing.getEndDate(), date)) {
            GeoZone from = pricing.getFromZone(tmsStore);
            if (from != null && from.contains(p1.getLat(), p1.getLng())) {

                GeoZone to = pricing.getToZone(tmsStore);
                if (to != null && to.contains(p2.getLat(), p2.getLng())) {
                    return true;
                }
            }
        }
        return false;
    }


}
