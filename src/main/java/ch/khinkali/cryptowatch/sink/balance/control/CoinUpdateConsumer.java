package ch.khinkali.cryptowatch.sink.balance.control;

import ch.khinkali.cryptowatch.sink.events.control.CoinEventConsumer;
import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;

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
public class CoinUpdateConsumer {

    private CoinEventConsumer eventConsumer;

    @Resource
    ManagedExecutorService mes;

    @Inject
    Properties kafkaProperties;

    @Inject
    Event<OrderPlaced> events;

    @Inject
    Logger logger;

    @PostConstruct
    private void init() {
        kafkaProperties.put("group.id", "order-consumer-" + UUID.randomUUID());
        String orders = kafkaProperties.getProperty("coins.topic");

        eventConsumer = new CoinEventConsumer(kafkaProperties, ev -> {
            logger.info("firing = " + ev);
            events.fire(ev);
        }, orders);

        mes.execute(eventConsumer);
    }

    @PreDestroy
    public void close() {
        eventConsumer.stop();
    }

}
