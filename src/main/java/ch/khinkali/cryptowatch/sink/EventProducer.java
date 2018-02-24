package ch.khinkali.cryptowatch.sink;

import ch.khinkali.cryptowatch.events.boundary.BaseEventProducer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Properties;


@ApplicationScoped
public class EventProducer extends BaseEventProducer {

    @Inject
    Properties kafkaProperties;

    @PostConstruct
    private void initProducer() {
        init(kafkaProperties);
    }

    @PreDestroy
    public void closeProducer() {
        close();
    }

}

