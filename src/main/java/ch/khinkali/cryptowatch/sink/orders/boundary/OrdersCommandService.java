package ch.khinkali.cryptowatch.sink.orders.boundary;

import ch.khinkali.cryptowatch.sink.EventProducer;
import ch.khinkali.cryptowatch.sink.orders.entity.OrderPlaced;

import javax.inject.Inject;
import java.util.logging.Logger;

public class OrdersCommandService {

    @Inject
    Logger logger;

    @Inject
    EventProducer eventProducer;

    public void placeOrder(final String orderId,
                           final String coinSymbol,
                           final Double amount,
                           final String userId) {
        logger.info("placeOder");
        eventProducer.publish(OrderPlaced.TOPIC, new OrderPlaced(orderId, coinSymbol, amount, userId));
    }

}
