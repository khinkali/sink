package ch.khinkali.cryptowatch.sink.balance.boundary;

import ch.khinkali.cryptowatch.sink.events.control.EventProducer;
import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;

import javax.inject.Inject;
import java.util.logging.Logger;

public class BalancesCommandService {

    @Inject
    Logger logger;

    @Inject
    EventProducer eventProducer;

    public void placeOrder(final String orderId,
                           final String coinSymbol,
                           final Double amount,
                           final String userId) {
        logger.info("placeOder");
        eventProducer.publish(new OrderPlaced(orderId, coinSymbol, amount, userId));
    }

}
