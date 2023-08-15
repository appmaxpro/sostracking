package com.tms.service.driver;

import com.tms.api.Point;
import com.tms.model.fleet.Driver;
import com.tms.model.TransService;

import java.util.Map;
import java.util.function.Predicate;

public interface DriverService {

    public Map<Driver, Point> getCloses(TransService service, Point point, Predicate<Driver> predicate) ;
    Map<Driver, Point> getCloses(TransService service, Point point);

    Map<Driver,Point> getCloses(Point point);
}
