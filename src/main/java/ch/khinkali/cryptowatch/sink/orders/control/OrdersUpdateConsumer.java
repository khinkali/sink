package ch.khinkali.cryptowatch.sink.orders.control;

import ch.khinkali.cryptowatch.events.boundary.EventConsumer;
import ch.khinkali.cryptowatch.events.entity.BaseEvent;
import ch.khinkali.cryptowatch.order.events.entity.OrderPlaced;

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
public class OrdersUpdateConsumer {

    private EventConsumer<String, BaseEvent> eventConsumer;

    @Resource
    ManagedExecutorService mes;

    @Inject
    Properties kafkaProperties;

    @Inject
    Event<BaseEvent> events;

    @PostConstruct
    private void init() {
        kafkaProperties.put("group.id", "order-consumer-" + UUID.randomUUID());

        eventConsumer = new EventConsumer<>(kafkaProperties, ev -> {
            events.fire(ev);
        }, OrderPlaced.TOPIC);

        mes.execute(eventConsumer);
    }

    @PreDestroy
    public void close() {
        eventConsumer.stop();
    }

}
