package com.tms.shared;

import org.traccar.model.BaseModel;
import org.traccar.model.Device;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;

import javax.inject.Inject;
import java.util.List;

public class SharedCacheManager {

    @Inject
    private CacheManager cacheManager;

    public void visitDevicePosition(ObjectVisitor<Position> visitor){
        cacheManager.visitDevicePosition((deviceId, pos)-> {
            visitor.visit(deviceId, pos);
            return false;
        });
    }

    public void visitDevicePosition(ObjectPredicate<Position> visitor){
        cacheManager.visitDevicePosition((deviceId, pos)-> {
            return visitor.test(deviceId, pos);
        });
    }

    public Position getPosition(long deviceId) {
        return cacheManager.getPosition(deviceId);
    }

    public Device getDevice(long deviceId) {
        return cacheManager.getDevice(deviceId);
    }

    public <T extends BaseModel> List<T> getDeviceObjects(long deviceId, Class<T> clazz) {
        return cacheManager.getDeviceObjects(deviceId, clazz);
    }

    public <T extends BaseModel> T getObject(Class<T> clazz, long id) {
        return cacheManager.getObject(clazz, id);
    }
}
