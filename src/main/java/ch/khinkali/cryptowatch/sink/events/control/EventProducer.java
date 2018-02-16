package ch.khinkali.cryptowatch.sink.events.control;

import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;
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
    private Producer<String, OrderPlaced> producer;
    private String topic;

    @Inject
    Properties kafkaProperties;

    @Inject
    Logger logger;

    @PostConstruct
    private void init() {
        try {
            kafkaProperties.put("transactional.id", UUID.randomUUID().toString());
            logger.info("init");
            producer = new KafkaProducer<>(kafkaProperties);
            logger.info("after KafkaProducer");
            topic = kafkaProperties.getProperty("coins.topic");
            producer.initTransactions();
            logger.info("after initTX");
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    public void publish(OrderPlaced event) {
        final ProducerRecord<String, OrderPlaced> record = new ProducerRecord<>(topic, event);
        try {
            producer.beginTransaction();
            producer.send(record);
            producer.commitTransaction();
        } catch (ProducerFencedException e) {
            logger.severe(e.getMessage());
            producer.close();
        } catch (KafkaException e) {
            logger.severe(e.getMessage());
            producer.abortTransaction();
        }
    }

    @PreDestroy
    public void close() {
        producer.close();
    }

}
