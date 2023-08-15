package com.tms.redis;

import com.tms.inject.Beans;
import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.traccar.LifecycleObject;
import org.traccar.config.Keys;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
public class RedisService implements LifecycleObject {
    private static final LocalCachedMapOptions CACHED_MAP_OPTIONS = buildCache();
    private RedissonClient client;

    public Map<String, ?> getLocalCachedMapCache(String key) {
        return client.getLocalCachedMap(key, CACHED_MAP_OPTIONS);
    }

    public Map<String, ?> getMapCache(String key) {
        return client.getMapCache(key);
    }

    //How to load data to avoid invalidation messages traffic.
    public void loadData(String cacheName, Map<String, Object> data) {

        RLocalCachedMap<String, Object> clearMap = client.getLocalCachedMap(cacheName,
                LocalCachedMapOptions.<String, Object>defaults()
                        .cacheSize(1)
                        .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE));

        RLocalCachedMap<String, Object> loadMap = client.getLocalCachedMap(cacheName,
                LocalCachedMapOptions.<String, Object>defaults()
                        .cacheSize(1)
                        .syncStrategy(LocalCachedMapOptions.SyncStrategy.NONE));

        loadMap.putAll(data);
        clearMap.clearLocalCache();
    }

    private static LocalCachedMapOptions buildCache(){
        LocalCachedMapOptions options = LocalCachedMapOptions.defaults()
                // Defines whether to store a cache miss into the local cache.
                // Default value is false.
                .storeCacheMiss(false)
                // Defines store mode of cache data.
                // Follow options are available:
                // LOCALCACHE - store data in local cache only and use Redis only for data update/invalidation.
                // LOCALCACHE_REDIS - store data in both Redis and local cache.
                .storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)

                // Defines Cache provider used as local cache store.
                // Follow options are available:
                // REDISSON - uses Redisson own implementation
                // CAFFEINE - uses Caffeine implementation
                .cacheProvider(LocalCachedMapOptions.CacheProvider.REDISSON)

                // Defines local cache eviction policy.
                // Follow options are available:
                // LFU - Counts how often an item was requested. Those that are used least often are discarded first.
                // LRU - Discards the least recently used items first
                // SOFT - Uses weak references, entries are removed by GC
                // WEAK - Uses soft references, entries are removed by GC
                // NONE - No eviction
                .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.NONE)

                // If cache size is 0 then local cache is unbounded.
                .cacheSize(1000)

                // Defines strategy for load missed local cache updates after Redis connection failure.
                //
                // Follow reconnection strategies are available:
                // CLEAR - Clear local cache if map instance has been disconnected for a while.
                // LOAD - Store invalidated entry hash in invalidation log for 10 minutes
                //        Cache keys for stored invalidated entry hashes will be removed
                //        if LocalCachedMap instance has been disconnected less than 10 minutes
                //        or whole cache will be cleaned otherwise.
                // NONE - Default. No reconnection handling
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.LOAD)

                // Defines local cache synchronization strategy.
                //
                // Follow sync strategies are available:
                // INVALIDATE - Default. Invalidate cache entry across all LocalCachedMap instances on map entry change
                // UPDATE - Insert/update cache entry across all LocalCachedMap instances on map entry change
                // NONE - No synchronizations on map changes
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)

                // or
                .timeToLive(10, TimeUnit.SECONDS)

                // max idle time for each map entry in local cache
                .maxIdle(10000)

                // or
                .maxIdle(10, TimeUnit.SECONDS);

        return options;
    }


    public void start() throws Exception {
        if (client == null) {
            org.traccar.config.Config config =
                    Beans.get(org.traccar.config.Config.class);
            String redisUrl = config.getString(Keys.TMS_REDID_URL);
            if (redisUrl != null) {

                // 1. Create config object
                Config clusterConfig = new Config();
                ClusterServersConfig clusterServersConfig
                        = clusterConfig.useClusterServers();


                for (String url : redisUrl.split("|")) {
                    clusterServersConfig.addNodeAddress(url);
                }

                client = Redisson.create(clusterConfig);

            }
        }
    }


    public void stop() throws Exception {
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }

    public RedissonClient getRedisson() {
        return client;
    }
}
