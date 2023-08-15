package com.tms.model;

import com.tms.TmsStore;
import com.tms.model.base.OrderServiceItemSet;
import com.tms.model.base.Partner;
import com.tms.model.fleet.Driver;
import com.tms.model.fleet.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransOrder extends Model{
    
    public static final int STATUS_REQUEST = 1;
    public static final int STATUS_BOOKED = 2;
    public static final int STATUS_ACCEPTED = 3;
    public static final int STATUS_ASSIGNED = 4;
    public static final int STATUS_ARRIVED_TO_PICKUP = 5;
    public static final int STATUS_SHIPPED = 6;
    public static final int STATUS_ARRIVED_TO_DEST = 7;
    public static final int STATUS_COMPLETE = 8;
    public static final int STATUS_CANCELED = 9;
    public static final int STATUS_CLIENT_CANCELED = 10;
    public static final int STATUS_DRIVER_CANCELED = 11;
    public static final int STATUS_EXPIRED = 12;
    public static final int STATUS_EXCEPTION = 13;
    public static final int STATUS_AWAITING_PAYMENT = 14;
    
    private String name;
    private LocalDate orderDate;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private LocalDateTime schedule;
    private LocalDateTime acceptedAt;
    private LocalDateTime etaPickup;
    private LocalDateTime etaDelivery;
    private BigDecimal estTotalRate = BigDecimal.ZERO;
    private BigDecimal rate = BigDecimal.ZERO;

    private int duration = 0;
    private int distance = 0;
    private int waitMinutes = 0;
    private int estDuration = 0;
    private int estDistance = 0;
    private long clientPartner;
    private long driver;
    private long vehicle;
    private long service;
    private long serviceItemSet;
    private long serviceRequirements;

    private long currency;
    private long order;
    private long transOrderItem;
    private long cancelReason;
    private long deviceRef = 0L;
    private int status = 0;


    private String addresses;
    private String directions;
    private String route;
    private String description;
    private String internalNote;
    private String cancelReasonStr;
    private String specificNotes;
    private int timezoneOffset;
    private String lang;




    public Partner getClientPartner(TmsStore store) {
        return store.getObject(Partner.class, clientPartner);
    }

    public Driver getDriver(TmsStore store) {
        return store.getObject(Driver.class, driver);
    }

    public Vehicle getVehicle(TmsStore store) {
        return store.getObject(Vehicle.class, vehicle);
    }

    public TransService getService(TmsStore store) {
        return store.getObject(TransService.class, service);
    }

    public OrderServiceItemSet getServiceItemSet(TmsStore store) {
        return store.getObject(OrderServiceItemSet.class, serviceItemSet);
    }

    public OrderServiceItemSet getServiceRequirements(TmsStore store) {
        return store.getObject(OrderServiceItemSet.class, serviceRequirements);
    }


}
