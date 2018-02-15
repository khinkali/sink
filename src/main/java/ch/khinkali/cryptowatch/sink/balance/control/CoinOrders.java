package ch.khinkali.cryptowatch.sink.balance.control;

import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class CoinOrders {

    private Map<String, OrderPlaced> orders = new ConcurrentHashMap<>();

    public OrderPlaced get(final String orderId) {
        return orders.get(orderId);
    }

    public void apply(@Observes OrderPlaced event) {
        orders.putIfAbsent(event.getOrderId(), new OrderPlaced(event.getOrderId(), event.getCoinSymbol(), event.getAmount(), event.getUserId()));
    }


}
