package ch.khinkali.cryptowatch.sink.balance.control;

import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;
import io.prometheus.client.Counter;

import javax.annotation.PostConstruct;
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

    private Counter createdOrders;

    @PostConstruct
    public void initMetrics() {
        createdOrders = Counter
                .build("total_orders_created", "Total orders created")
                .register();
    }

    public OrderPlaced get(final String orderId) {
        return orders.get(orderId);
    }

    public void apply(@Observes OrderPlaced event) {
        createdOrders.inc();
        orders.putIfAbsent(event.getOrderId(), new OrderPlaced(event.getOrderId(), event.getCoinSymbol(), event.getAmount(), event.getUserId()));
    }


}
