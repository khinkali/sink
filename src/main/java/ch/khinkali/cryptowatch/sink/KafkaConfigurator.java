package ch.khinkali.cryptowatch.sink;

import ch.khinkali.cryptowatch.events.boundary.BaseKafkaConfigurator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import java.util.Properties;

@ApplicationScoped
public class KafkaConfigurator extends BaseKafkaConfigurator {

    @PostConstruct
    private void initProps() {
        initProperties();
    }

    @Produces
    @RequestScoped
    public Properties exposeKafkaProperties() {
        return getKafkaProperties();
    }

}

