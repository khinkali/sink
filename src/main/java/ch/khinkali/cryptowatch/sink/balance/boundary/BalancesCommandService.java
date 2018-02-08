package ch.khinkali.cryptowatch.sink.balance.boundary;

import ch.khinkali.cryptowatch.sink.events.entity.CoinInfo;

import javax.inject.Inject;
import java.util.logging.Logger;

public class BalancesCommandService {

    @Inject
    Logger logger;

//    @Inject
//    EventProducer eventProducer;

    public void placeOrder(final CoinInfo coinInfo) {
        logger.info("placeOder");
//        eventProducer.publish(new OrderPlaced(coinInfo));
    }

}
