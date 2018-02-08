package ch.khinkali.cryptowatch.sink.balance.boundary;

import ch.khinkali.cryptowatch.sink.balance.control.CoinOrders;
import ch.khinkali.cryptowatch.sink.balance.entity.CoinOrder;

import javax.inject.Inject;
import java.util.UUID;

public class BalancesQueryService {

    @Inject
    CoinOrders coinOrders;

    public CoinOrder getOrder(final UUID orderId) {
        return coinOrders.get(orderId);
    }

}
