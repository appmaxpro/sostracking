package com.tms.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.broadcast.BroadcastInterface;
import org.traccar.model.Geofence;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

import javax.inject.Inject;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ZoneCacheManager implements BroadcastInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneCacheManager.class);
    private Cache<Long, Geofence> geofenceCache =
            Caffeine.newBuilder()
                    .maximumSize(300)
                    .expireAfterWrite(60, TimeUnit.MINUTES)
                    .weakKeys()
                    .build();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean loaded;
    @Inject
    private Storage storage;

    public Geofence getGeofence(long id) {
        try{
            lock.readLock().lock();
            geofenceCache.get(id, key -> {

                try {
                    return storage.getObject(Geofence.class, Request.builder()
                            .and(new Condition.Equals("id", key)).build());
                } catch (StorageException e) {
                    Geofence geofence = new Geofence();
                    geofence.setId(key);
                    geofence.setName("Auto generated");
                    add(geofence, new Request(new Columns.Include("id", "name"))) ;
                    return geofence;
                }
            });
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }


    public Collection<Geofence> getAll() {
        load();
        try {
            lock.readLock().lock();

            return geofenceCache.asMap().values();
        } finally {
            lock.readLock().unlock();
        }
    }

    private void add(Geofence geofence, Request request) {
        try {
            geofence.setId(storage.addObject(geofence, request));
        } catch (StorageException e) {
            LOGGER.error("add geofence: {}", geofence.getName(), e);
        }
    }

    private void load(){
        if (!loaded) {
            try {
                lock.readLock().lock();
                if(!loaded) {
                    try {
                        storage.getObjects(Geofence.class, Request.builder().build())
                                .stream().forEach(g -> geofenceCache.put(g.getId(), g));
                    } catch (StorageException e) {
                        LOGGER.error("load geofence cause error:", e);
                    }
                }
            } finally {
                loaded = true;
                lock.readLock().unlock();
            }
        }
    }


}
