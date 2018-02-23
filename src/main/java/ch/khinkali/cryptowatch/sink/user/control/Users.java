package ch.khinkali.cryptowatch.sink.user.control;

import ch.khinkali.cryptowatch.sink.orders.entity.Coin;
import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;
import ch.khinkali.cryptowatch.sink.user.entity.User;
import ch.khinkali.cryptowatch.user.events.entity.UserCreated;
import io.prometheus.client.Counter;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class Users {

    @Inject
    Logger logger;

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
