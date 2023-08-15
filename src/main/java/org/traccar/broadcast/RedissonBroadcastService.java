package org.traccar.broadcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.inject.Beans;
import com.tms.redis.RedisService;
import org.redisson.api.RShardedTopicAsync;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedissonBroadcastService extends BaseBroadcastService{

    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonBroadcastService.class);

    private final ObjectMapper objectMapper;

    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private static final String CHANNEL = "traccar";

    private RShardedTopicAsync topic;

    private final String id = UUID.randomUUID().toString();

    public RedissonBroadcastService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean singleInstance() {
        return false;
    }

    @Override
    protected void sendMessage(BroadcastMessage message) {
        try {
            String payload = id  + ":" + objectMapper.writeValueAsString(message);
            topic.publishAsync(payload);

        } catch (IOException e) {
            LOGGER.warn("Broadcast failed", e);
        }
    }

    @Override
    public void start() throws IOException {
        try {
            Beans.get(RedisService.class).start();
            topic = Beans.get(RedisService.class)
                    .getRedisson()
                    .getShardedTopic(CHANNEL);
            topic.addListenerAsync(String.class, receiver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void stop() {
        try {
            topic.removeAllListenersAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warn("Subscriber close failed", e);
        }

    }

    private final MessageListener receiver = new MessageListener<String>() {
        @Override
        public void onMessage(CharSequence channel, String message) {
            try {
                String[] parts = message.split(":", 2);
                if (channel.equals(CHANNEL) && parts.length == 2 && !id.equals(parts[0])) {
                    handleMessage(objectMapper.readValue(parts[1], BroadcastMessage.class));
                }
            } catch (IOException e) {
                LOGGER.warn("Broadcast handleMessage failed", e);
            }
        }

    };
}
