package ch.khinkali.cryptowatch.sink.users.entity;

import ch.khinkali.cryptowatch.sink.orders.entity.Coin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(exclude = {"coins"})
@ToString
@Getter
public class User {
    @Setter
    private String id;
    @Setter
    private String username;
    private final Map<Coin, Double> coins = new HashMap<>();

    public User(String id, String username, Map<Coin, Double> coins) {
        this.id = id;
        this.username = username;
        this.coins.putAll(coins);
    }

    public User(String id, String username) {
        this(id, username, new HashMap<>());
    }

    public JsonObject getJson() {
        JsonArrayBuilder userCoins = Json.createArrayBuilder();
        for (Coin coin : coins.keySet()) {
            JsonObject coinJson = Json.createObjectBuilder()
                    .add("coinSymbol", coin.getCoinSymbol())
                    .add("amount", coins.get(coin))
                    .build();
            userCoins.add(coinJson);
        }
        return Json.createObjectBuilder()
                .add("id", id)
                .add("username", username)
                .add("coins", userCoins)
                .build();
    }
}
