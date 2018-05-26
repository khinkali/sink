package ch.khinkali.cryptowatch.sink.orders.boundary;

import ch.khinkali.cryptowatch.order.events.entity.OrderPlaced;
import ch.khinkali.cryptowatch.sink.orders.control.Orders;

import javax.inject.Inject;

public class OrdersQueryService {

    @Inject
    Orders orders;

    public OrderPlaced getOrder(final String orderId) {
        return orders.get(orderId);
    }

}
