package ch.khinkali.cryptowatch.sink.user.control;

import ch.khinkali.cryptowatch.sink.events.control.UserCreated;
import ch.khinkali.cryptowatch.sink.events.control.UserEventConsumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

@Startup
@Singleton
public class UserUpdateConsumer {

    private UserEventConsumer eventConsumer;

    @Resource
    ManagedExecutorService mes;

    @Inject
    Event<UserCreated> events;

    @Inject
    Logger logger;

    @PostConstruct
    private void init() {
        Properties kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", System.getenv("KAFKA_ADDRESS"));
        kafkaProperties.put("isolation.level", "read_committed");
        kafkaProperties.put("enable.auto.commit", false);
        kafkaProperties.put("auto.offset.reset", "earliest");
        kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("value.deserializer", "ch.khinkali.cryptowatch.sink.events.control.UserEventDeserializer");
        kafkaProperties.put("group.id", "user-consumer-" + UUID.randomUUID());

        eventConsumer = new UserEventConsumer(kafkaProperties, ev -> {
            logger.info("firing = " + ev);
            events.fire(ev);
        }, "users");

        mes.execute(eventConsumer);
    }

    @PreDestroy
    public void close() {
        eventConsumer.stop();
    }

}
