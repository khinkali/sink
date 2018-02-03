package ch.khinkali.cryptowatch.sink.events.control;

import ch.khinkali.cryptowatch.sink.events.entity.CoinEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class EventProducer {

    private Producer<String, CoinEvent> producer;
    private String topic;

    @Inject
    Properties kafkaProperties;

    @Inject
    Logger logger;

    @PostConstruct
    private void init() {
        logger.info("init");
        kafkaProperties.put("transactional.id", UUID.randomUUID().toString());
        logger.info("  after transaction");
        producer = new KafkaProducer<>(kafkaProperties);
        logger.info("  after producer");
        topic = kafkaProperties.getProperty("coins.topic");
        logger.info("  after topic");
        producer.initTransactions();
        logger.info("init finished");
    }

    public void publish(CoinEvent event) {
        logger.info("publish coin event");
        final ProducerRecord<String, CoinEvent> record = new ProducerRecord<>(topic, event);
        try {
            producer.beginTransaction();
            logger.info("publishing = " + record);
            producer.send(record);
            producer.commitTransaction();
        } catch (ProducerFencedException e) {
            producer.close();
        } catch (KafkaException e) {
            producer.abortTransaction();
        }
    }

    @PreDestroy
    public void close() {
        producer.close();
    }

}
