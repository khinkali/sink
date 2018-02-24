package ch.khinkali.cryptowatch.sink.users.control;

import ch.khinkali.cryptowatch.events.boundary.EventConsumer;
import ch.khinkali.cryptowatch.events.entity.BaseEvent;
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

    private EventConsumer eventConsumer;

    @Resource
    ManagedExecutorService mes;

    @Inject
    Properties kafkaProperties;

    @Inject
    Event<BaseEvent> events;

    @Inject
    Logger logger;

    @PostConstruct
    private void init() {
        kafkaProperties.put("group.id", "user-consumer-" + UUID.randomUUID());

        eventConsumer = new EventConsumer(kafkaProperties, ev -> {
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
