package ch.khinkali.cryptowatch.sink.balances.boundary;

import ch.khinkali.cryptowatch.sink.balances.control.CoinOrders;
import ch.khinkali.cryptowatch.sink.balances.entity.CoinOrder;

import javax.inject.Inject;
import java.util.UUID;

public class BalancesQueryService {

    @Inject
    CoinOrders coinOrders;

    public CoinOrder getOrder(final UUID orderId) {
        return coinOrders.get(orderId);
    }

}
