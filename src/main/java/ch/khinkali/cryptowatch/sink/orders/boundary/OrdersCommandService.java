package ch.khinkali.cryptowatch.sink.orders.boundary;

import ch.khinkali.cryptowatch.order.events.entity.OrderPlaced;
import ch.khinkali.cryptowatch.sink.EventProducer;

import javax.inject.Inject;

public class OrdersCommandService {

    @Inject
    EventProducer eventProducer;

    public void placeOrder(final String orderId,
                           final String coinSymbol,
                           final Double amount,
                           final String userId) {
        eventProducer.publish(OrderPlaced.TOPIC, new OrderPlaced(orderId, coinSymbol, amount, userId));
    }

}
