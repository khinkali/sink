package ch.khinkali.cryptowatch.sink.orders.entity;

import ch.khinkali.cryptowatch.events.entity.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.json.Json;
import javax.json.JsonObject;

@AllArgsConstructor
@Getter
public class OrderPlaced implements BaseEvent {
    public static final String TOPIC = "coins";

    public enum JSON_KEYS {
        ORDER_ID("orderId"), COIN_SYMBOL("coinSymbol"), AMOUNT("amount"), USER_ID("userId");

        @Getter
        String jsonKey;

        JSON_KEYS(String jsonKey) {
            this.jsonKey = jsonKey;
        }
    }

    private final String orderId;
    private final String coinSymbol;
    private final Double amount;
    private final String userId;

    public OrderPlaced(JsonObject jsonObject) {
        this(jsonObject.getString(JSON_KEYS.ORDER_ID.getJsonKey()),
                jsonObject.getString(JSON_KEYS.COIN_SYMBOL.getJsonKey()),
                jsonObject.getJsonNumber(JSON_KEYS.AMOUNT.getJsonKey()).doubleValue(),
                jsonObject.getString(JSON_KEYS.USER_ID.getJsonKey()));
    }


    @Override
    public JsonObject getJson() {
        return Json.createObjectBuilder()
                .add(JSON_KEYS.ORDER_ID.getJsonKey(), orderId)
                .add(JSON_KEYS.COIN_SYMBOL.getJsonKey(), coinSymbol)
                .add(JSON_KEYS.AMOUNT.getJsonKey(), amount)
                .add(JSON_KEYS.USER_ID.getJsonKey(), userId)
                .build();
    }
}
