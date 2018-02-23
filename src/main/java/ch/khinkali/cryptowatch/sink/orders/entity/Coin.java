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
    private String coinSymbol;

    public Coin(JsonObject json) {
        coinSymbol = json.getString("coinSymbol");
    }

    public JsonObject getJson() {
        return Json.createObjectBuilder()
                .add("coinSymbol", coinSymbol)
                .build();
    }
}
