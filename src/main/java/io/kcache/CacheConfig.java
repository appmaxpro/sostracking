package io.kcache;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Options for controlling a cache.
 */
public class CacheConfig {

    private int maxSize;
    private int maxIdleSecs;
    private int maxSecsToLive;
    private int trimFrequency;
    private boolean nearCache;
    private PersistentConfig persistentConfig;


    /**
     * Construct with no set options.
     */
    public CacheConfig() {
    }

    /**
     * Create from the cacheTuning deployment annotation.
     */
    public CacheConfig(CacheConfig tuning) {
        this.maxSize = tuning.getMaxSize();
        this.maxIdleSecs = tuning.getMaxIdleSecs();
        this.maxSecsToLive = tuning.getMaxSecsToLive();
        this.trimFrequency = tuning.getTrimFrequency();
        this.persistentConfig = tuning.getPersistentConfig();
    }


    /**
     * Apply any settings from the default settings that have not already been
     * specifically set.
     */
    public CacheConfig applyDefaults(CacheConfig defaults) {
        if (maxSize == 0) {
            maxSize = defaults.getMaxSize();
        }
        if (maxIdleSecs == 0) {
            maxIdleSecs = defaults.getMaxIdleSecs();
        }
        if (maxSecsToLive == 0) {
            maxSecsToLive = defaults.getMaxSecsToLive();
        }
        if (trimFrequency == 0) {
            trimFrequency = defaults.getTrimFrequency();
        }
        if (persistentConfig == null) {
            persistentConfig = defaults.getPersistentConfig();
        }
        return this;
    }

    /**
     * Return a copy of this object.
     */
    public CacheConfig copy() {
        CacheConfig copy = new CacheConfig();
        copy.maxSize = maxSize;
        copy.maxIdleSecs = maxIdleSecs;
        copy.maxSecsToLive = maxSecsToLive;
        copy.trimFrequency = trimFrequency;
        copy.nearCache = this.nearCache;
        copy.persistentConfig = this.persistentConfig;
        return copy;
    }

    /**
     * Return a copy of this object with nearCache option.
     */
    public CacheConfig copy(boolean nearCache) {
        CacheConfig copy = copy();
        copy.nearCache = nearCache;
        return copy;
    }

    /**
     * Return true if nearCache was explicitly turned on.
     */
    public boolean isNearCache() {
        return nearCache;
    }

    /**
     * Turn on nearCache option.
     */
    public void setNearCache(boolean nearCache) {
        this.nearCache = nearCache;
    }

    /**
     * Return the maximum cache size.
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Set the maximum cache size.
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Return the maximum idle time.
     */
    public int getMaxIdleSecs() {
        return maxIdleSecs;
    }

    /**
     * Set the maximum idle time.
     */
    public void setMaxIdleSecs(int maxIdleSecs) {
        this.maxIdleSecs = maxIdleSecs;
    }

    /**
     * Return the maximum time to live.
     */
    public int getMaxSecsToLive() {
        return maxSecsToLive;
    }

    /**
     * Set the maximum time to live.
     */
    public void setMaxSecsToLive(int maxSecsToLive) {
        this.maxSecsToLive = maxSecsToLive;
    }

    /**
     * Return the trim frequency in seconds.
     */
    public int getTrimFrequency() {
        return trimFrequency;
    }

    /**
     * Set the trim frequency in seconds.
     */
    public void setTrimFrequency(int trimFrequency) {
        this.trimFrequency = trimFrequency;
    }


    /**
     * Return the maximum idle time.
     */
    public PersistentConfig getPersistentConfig() {
        return persistentConfig;
    }

    /**
     * Set the maximum idle time.
     */
    public void setPersistentConfig(PersistentConfig persistentConfig) {
        this.persistentConfig = persistentConfig;
    }


    public static class PersistentConfig {
        public final Path dataPath;
        public final Path checkpointPath;
        public final int checkpointVersion;

        public PersistentConfig(Path dataPath, Path checkpointPath){
            this(dataPath, checkpointPath, 0);
        }
        public PersistentConfig(Path dataPath, Path checkpointPath, int checkpointVersion) {
            this.dataPath = Objects.requireNonNull(dataPath) ;
            this.checkpointPath = Objects.requireNonNull(checkpointPath);
            this.checkpointVersion = checkpointVersion;
        }
    }
}