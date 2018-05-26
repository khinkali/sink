package ch.khinkali.cryptowatch.sink.users.control;

import ch.khinkali.cryptowatch.order.events.entity.OrderPlaced;
import ch.khinkali.cryptowatch.sink.orders.entity.Coin;
import ch.khinkali.cryptowatch.sink.users.entity.User;
import ch.khinkali.cryptowatch.user.events.entity.UserCreated;
import io.prometheus.client.Counter;
import lombok.Getter;

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
public class Users {

    @Getter
    private final Map<String, User> users = new ConcurrentHashMap<>();

    private Counter createdUsers;

    @PostConstruct
    public void initMetrics() {
        createdUsers = Counter
                .build("total_users_created", "Total user created")
                .register();
    }

    public void apply(@Observes UserCreated event) {
        createdUsers.inc();
        users.put(event.getUserId(), new User(event.getUserId(), event.getUsername()));
    }

    public void apply(@Observes OrderPlaced event) {
        User user = users.get(event.getUserId());
        if (user == null) {
            user = new User(event.getUserId(), "UNKNOWN");
            users.put(event.getUserId(), user);
        }
        Map<Coin, Double> coins = user.getCoins();
        Coin coin = new Coin(event.getCoinSymbol());
        if (coins.containsKey(coin)) {
            coins.put(coin, coins.get(coin) + event.getAmount());
        } else {
            coins.put(coin, event.getAmount());
        }
    }

}
