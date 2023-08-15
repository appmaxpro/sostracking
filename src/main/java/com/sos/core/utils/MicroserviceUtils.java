package com.sos.core.utils;

import com.sos.core.Schemas;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class MicroserviceUtils {
  public static final String MONITORING_INTERCEPTOR_PREFIX = "confluent.monitoring.interceptor.";
  private static final Logger log = LoggerFactory.getLogger(MicroserviceUtils.class);
  public static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
  public static final String DEFAULT_SCHEMA_REGISTRY_URL = "http://localhost:8081";

  public static Properties buildPropertiesFromConfigFile(final String configFile) throws IOException {
    if (!Files.exists(Paths.get(configFile))) {
      throw new IOException(configFile + " not found.");
    }
    final Properties properties = new Properties();
    try (InputStream inputStream = new FileInputStream(configFile)) {
      properties.load(inputStream);
    }
    return properties;
  }

  public static Properties baseStreamsConfig(final String bootstrapServers,
                                             final String stateDir,
                                             final String appId,
                                             final Properties defaultConfig) {
    return baseStreamsConfig(bootstrapServers, stateDir, appId, false, defaultConfig);
  }

  public static Properties baseStreamsConfigEOS(final String bootstrapServers,
                                                final String stateDir,
                                                final String appId,
                                                final Properties defaultConfig) {
    return baseStreamsConfig(bootstrapServers, stateDir, appId, true, defaultConfig);
  }

  public static Properties baseStreamsConfig(final String bootstrapServers,
                                             final String stateDir,
                                             final String appId,
                                             final boolean enableEOS,
                                             final Properties defaultConfig) {

    final Properties config = new Properties();
    config.putAll(defaultConfig);
    // Workaround for a known issue with RocksDB in environments where you have only 1 cpu core.
    config.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG, CustomRocksDBConfig.class);
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG,
            enableEOS ? "exactly_once" : "at_least_once");
    config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1); //commit as fast as possible
    config.put(StreamsConfig.consumerPrefix(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG), 30000);
    MonitoringInterceptorUtils.maybeConfigureInterceptorsStreams(config);
    return config;
  }

  public static class CustomRocksDBConfig implements RocksDBConfigSetter {

    @Override
    public void setConfig(final String storeName, final Options options,
        final Map<String, Object> configs) {
      // Workaround: We must ensure that the parallelism is set to >= 2.  There seems to be a known
      // issue with RocksDB where explicitly setting the parallelism to 1 causes issues (even though
      // 1 seems to be RocksDB's default for this configuration).
      final int compactionParallelism = Math.max(Runtime.getRuntime().availableProcessors(), 2);
      // Set number of compaction threads (but not flush threads).
      options.setIncreaseParallelism(compactionParallelism);
    }

    @Override
    public void close(final String storeName, final Options options) {

    }
  }


  public static <T> KafkaProducer startProducer(final String bootstrapServers,
                                                final Schemas.Topic<String, T> topic,
                                                final Properties defaultConfig) {
    final Properties producerConfig = new Properties();
    producerConfig.putAll(defaultConfig);
    producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    producerConfig.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
    producerConfig.put(ProducerConfig.RETRIES_CONFIG, String.valueOf(Integer.MAX_VALUE));
    producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
    producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, "order-sender");
    MonitoringInterceptorUtils.maybeConfigureInterceptorsProducer(producerConfig);

    return new KafkaProducer<>(producerConfig,
        topic.keySerde().serializer(),
        topic.valueSerde().serializer());
  }

}
