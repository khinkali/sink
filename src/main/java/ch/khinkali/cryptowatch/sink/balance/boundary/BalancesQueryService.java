package ch.khinkali.cryptowatch.sink.balance.boundary;

import ch.khinkali.cryptowatch.sink.balance.control.CoinOrders;
import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;

import javax.inject.Inject;

public class BalancesQueryService {

    @Inject
    CoinOrders coinOrders;

    public OrderPlaced getOrder(final String orderId) {
        return coinOrders.get(orderId);
    }

}
