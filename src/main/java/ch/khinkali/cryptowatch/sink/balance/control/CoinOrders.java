package ch.khinkali.cryptowatch.sink.balance.control;

import ch.khinkali.cryptowatch.sink.balance.entity.CoinOrder;
import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class CoinOrders {

    private Map<UUID, CoinOrder> coffeeOrders = new ConcurrentHashMap<>();

    public CoinOrder get(final UUID orderId) {
        return coffeeOrders.get(orderId);
    }

    public void apply(@Observes OrderPlaced event) {
        coffeeOrders.putIfAbsent(event.getCoinInfo().getOrderId(), new CoinOrder());
        applyFor(event.getCoinInfo().getOrderId(), o -> o.place(event.getCoinInfo()));
    }

    private void applyFor(final UUID orderId, final Consumer<CoinOrder> consumer) {
        final CoinOrder coffeeOrder = coffeeOrders.get(orderId);
        if (coffeeOrder != null)
            consumer.accept(coffeeOrder);
    }

}
