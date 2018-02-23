package ch.khinkali.cryptowatch.sink.user.entity;

import ch.khinkali.cryptowatch.sink.orders.entity.Coin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(exclude = {"coins"})
@ToString
@Getter
public class User {
    @Setter
    private String userId;
    @Setter
    private String username;
    private final Map<Coin, Double> coins = new HashMap<>();

    public User(String userId, String username, Map<Coin, Double> coins) {
        this.userId = userId;
        this.username = username;
        this.coins.putAll(coins);
    }

    public User(String userId, String username) {
        this(userId, username, new HashMap<>());
    }
}
