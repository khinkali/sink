package ch.khinkali.cryptowatch.sink.users.entity;

import ch.khinkali.cryptowatch.sink.orders.entity.Coin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.json.*;
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

    public User(JsonObject json) {
        coins.clear();
        id = json.getString("id");
        username = json.getString("username");
        for (JsonValue value : json.getJsonArray("coins")) {
            if (!(value instanceof JsonObject)) {
                continue;
            }
            JsonObject coinJson = (JsonObject) value;
            coins.put(new Coin(coinJson), coinJson.getJsonNumber("amount").doubleValue());
        }
    }

    public JsonObject getJson() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("username", username)
                .add("coins", getCoinsAsJson())
                .build();
    }

    public JsonArray getCoinsAsJson() {
        JsonArrayBuilder userCoins = Json.createArrayBuilder();
        for (Coin coin : coins.keySet()) {
            JsonObject coinJson = Json.createObjectBuilder()
                    .add("coinSymbol", coin.getCoinSymbol())
                    .add("amount", coins.get(coin))
                    .build();
            userCoins.add(coinJson);
        }
        return userCoins.build();
    }
}
