package ch.khinkali.cryptowatch.sink.user.control;

import ch.khinkali.cryptowatch.sink.balance.entity.Coin;
import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;
import ch.khinkali.cryptowatch.sink.user.entity.User;
import ch.khinkali.cryptowatch.user.events.entity.UserCreated;
import lombok.Getter;

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

    public void apply(@Observes UserCreated event) {
        logger.info("userEvent: " + event);
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
