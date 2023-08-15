package com.tms.service.driver;

import com.tms.TmsStore;
import com.tms.api.Point;
import com.tms.model.fleet.Driver;
import com.tms.model.TransService;
import com.tms.shared.SharedCacheManager;
import com.tms.utils.primitive.Primitive;
import com.tms.utils.cost.DistanceCalculator;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class DriverServiceImpl implements DriverService {
    private static final double DEFAULT_SEARCH_RADIOS = 50.0; //50KM
    private static final Predicate<Driver> ACCEPT_ALL = t -> true;

    @Inject
    private SharedCacheManager sharedCacheManager;
    private DistanceCalculator distanceCalculator;
    @Inject
    private TmsStore tmsStore;

    @Override
    public Map<Driver, Point> getCloses(TransService service, Point point) {
        return getCloses(service, point, ACCEPT_ALL);
    }

    @Override
    public Map<Driver, Point> getCloses(TransService service, Point point, Predicate<Driver> predicate) {
        int searchRadius = service.getSearchRadius();
        return getCloses(point, searchRadius, predicate);
    }

    @Override
    public Map<Driver, Point> getCloses(Point point) {
        return getCloses(point, DEFAULT_SEARCH_RADIOS, ACCEPT_ALL);
    }

    Map<Driver, Point> getCloses(final Point point, double searchRadius, Predicate<Driver> predicate) {
        var longList = Primitive.longList();
        Map<Driver, Point> deviceMap = new LinkedHashMap<>();
        sharedCacheManager.visitDevicePosition((deviceId, pos) -> {

            var driverPoint = new Point(pos.getLatitude(), pos.getLongitude());
            var distance =
                    distanceCalculator.calculateDistance(
                            point, driverPoint);

            if (distance < searchRadius) {
                Driver tmsDriver = tmsStore.getObjectByRef(Driver.class, deviceId);
                if (tmsDriver != null && predicate.test(tmsDriver)) {
                    deviceMap.put(tmsStore.getObjectByRef(Driver.class, deviceId), driverPoint);
                }
            }

            return false;

        });

        return deviceMap;
    }
}
