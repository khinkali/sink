package ch.khinkali.cryptowatch.sink.orders.boundary;

import ch.khinkali.cryptowatch.sink.orders.control.Orders;
import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;

import javax.inject.Inject;

public class OrdersQueryService {

    @Inject
    Orders orders;

    public OrderPlaced getOrder(final String orderId) {
        return orders.get(orderId);
    }

}
