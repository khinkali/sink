package ch.khinkali.cryptowatch.sink.users.control;

import ch.khinkali.cryptowatch.user.events.boundary.UserEventConsumer;
import ch.khinkali.cryptowatch.user.events.boundary.UserEventDeserializer;
import ch.khinkali.cryptowatch.user.events.entity.UserCreated;

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
public class UsersUpdateConsumer {

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
        kafkaProperties.put("value.deserializer", UserEventDeserializer.class.getCanonicalName());
        kafkaProperties.put("group.id", "user-consumer-" + UUID.randomUUID());

        eventConsumer = new UserEventConsumer(kafkaProperties, ev -> {
            logger.info("firing = " + ev);
            events.fire(ev);
        }, UserCreated.TOPIC);

        mes.execute(eventConsumer);
    }

    @PreDestroy
    public void close() {
        eventConsumer.stop();
    }

}
