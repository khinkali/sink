package ch.khinkali.cryptowatch.sink;

import ch.khinkali.cryptowatch.events.boundary.EventConsumer;
import ch.khinkali.cryptowatch.events.entity.BaseEvent;
import ch.khinkali.cryptowatch.order.events.entity.OrderPlaced;
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

@Startup
@Singleton
public class UpdateConsumer {

    private EventConsumer<String, BaseEvent> userConsumer;
    private EventConsumer<String, BaseEvent> orderConsumer;

    @Resource
    ManagedExecutorService mes;

    @Inject
    Properties kafkaProperties;

    @Inject
    Event<BaseEvent> events;

    @PostConstruct
    private void init() {
        kafkaProperties.put("group.id", "consumer-" + UUID.randomUUID());
        userConsumer = new EventConsumer<>(kafkaProperties, ev -> events.fire(ev), UserCreated.TOPIC);
        orderConsumer = new EventConsumer<>(kafkaProperties, ev -> events.fire(ev), OrderPlaced.TOPIC);

        mes.execute(userConsumer);
        mes.execute(orderConsumer);
    }

    @PreDestroy
    public void close() {
        userConsumer.stop();
        orderConsumer.stop();
    }

}
