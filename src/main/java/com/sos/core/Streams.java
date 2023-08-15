package com.sos.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sos.core.utils.MicroserviceUtils;
import com.sos.core.utils.MonitoringInterceptorUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Streams {

    private static final Logger log = LoggerFactory.getLogger(Streams.class);

    private final Properties config;
    private final String bootstrapServers;
    private final String host;
    private final int port;
    private final ObjectMapper objectMapper;
    private KafkaStreams kafkaStreams;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean running;

    public Streams(Properties config, String bootstrapServers, String host, int port, ObjectMapper objectMapper) {
        this.config = config;
        this.bootstrapServers = bootstrapServers;
        this.host = host;
        this.port = port;
        this.objectMapper = objectMapper;
        //MonitoringInterceptorUtils.maybeConfigureInterceptorsConsumer(config);
    }

    public void start(){
        running = true;
        executorService.execute(()-> {
            kafkaStreams = startKStreams();
        });
        log.info("Started Service " + getClass().getSimpleName());
    }


    public void stop() {
        running = false;
        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            log.info("Failed to stop " + getClass().getSimpleName() + " in 1000ms");
        }
        log.info(getClass().getSimpleName() + " was stopped");
    }

    private KafkaStreams startKStreams() {
        final KafkaStreams streams = new KafkaStreams(
                createMaterializedView().build(),
                config(config));
        //metadataService = new MetadataService(streams);
        //streams.cleanUp(); //don't do this in prod as it clears your state stores
        final CountDownLatch startLatch = new CountDownLatch(1);
        streams.setStateListener((newState, oldState) -> {
            if (newState == KafkaStreams.State.RUNNING && oldState != KafkaStreams.State.RUNNING) {
                startLatch.countDown();
            }
        });
        streams.start();

        try {
            if (!startLatch.await(60, TimeUnit.SECONDS)) {
                throw new RuntimeException("Streams never finished rebalancing on startup");
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return streams;
    }

    private StreamsBuilder createMaterializedView() {

        final Materialized materialized = Materialized.as(Schemas.SOS_STORE_SPACE);

        final StreamsBuilder builder = new StreamsBuilder();
        for (var entry: Schemas.Topics.ALL.entrySet())
            builder.table(
                    entry.getKey(),
                    Consumed.with(entry.getValue().keySerde(), entry.getValue().valueSerde()),
                    materialized
            );


        return builder;
    }

    private Properties config(final Properties defaultConfig) {
        final String stateDir = defaultConfig.getProperty(StreamsConfig.STATE_DIR_CONFIG);
        final Properties props = MicroserviceUtils.baseStreamsConfig(
                bootstrapServers,
                stateDir != null ? stateDir : "/tmp/kafka-streams",
                App.SERVICE_APP_ID,
                defaultConfig);
        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, host + ":" + port);
        return props;
    }

    public <T> KafkaProducer startProducer(final Schemas.Topic<String, T> topic){
        return startProducer(bootstrapServers, topic, config);
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
