package ch.khinkali.cryptowatch.sink.orders.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.json.Json;
import javax.json.JsonObject;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class Coin {
    public enum JSON_KEYS {
        COIN_SYMBOL("coinSymbol");

        @Getter
        String jsonKey;

        JSON_KEYS(String jsonKey) {
            this.jsonKey = jsonKey;
        }
    }

    private String coinSymbol;

    public Coin(JsonObject json) {
        coinSymbol = json.getString(JSON_KEYS.COIN_SYMBOL.getJsonKey());
    }

    public JsonObject getJson() {
        return Json.createObjectBuilder()
                .add(JSON_KEYS.COIN_SYMBOL.getJsonKey(), coinSymbol)
                .build();
    }
}
