package ch.khinkali.cryptowatch.sink.users.entity;

import ch.khinkali.cryptowatch.sink.orders.entity.Coin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(exclude = {"coins"})
@ToString
@Getter
public class User {
    public enum JSON_KEYS {
        ID("id"), USERNAME("username"), AMOUNT("amount");

        @Getter
        String jsonKey;

        JSON_KEYS(String jsonKey) {
            this.jsonKey = jsonKey;
        }
    }

    @Setter
    private String id;
    @Setter
    private String username;
    private final Map<Coin, Double> coins = new HashMap<>();

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(JsonObject json) {
        coins.clear();
        id = json.getString(JSON_KEYS.ID.getJsonKey());
        username = json.getString(JSON_KEYS.USERNAME.getJsonKey());
    }

    public JsonObject getJson() {
        return Json.createObjectBuilder()
                .add(JSON_KEYS.ID.getJsonKey(), id)
                .add(JSON_KEYS.USERNAME.getJsonKey(), username)
                .build();
    }

    public JsonArray getCoinsAsJson() {
        JsonArrayBuilder userCoins = Json.createArrayBuilder();
        for (Coin coin : coins.keySet()) {
            JsonObject coinJson = Json.createObjectBuilder()
                    .add(Coin.JSON_KEYS.COIN_SYMBOL.getJsonKey(), coin.getCoinSymbol())
                    .add(JSON_KEYS.AMOUNT.getJsonKey(), coins.get(coin))
                    .build();
            userCoins.add(coinJson);
        }
        return userCoins.build();
    }
}
