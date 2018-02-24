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

    private final String orderId;
    private final String coinSymbol;
    private final Double amount;
    private final String userId;

    public OrderPlaced(JsonObject jsonObject) {
        this(jsonObject.getString("orderId"),
                jsonObject.getString("coinSymbol"),
                jsonObject.getJsonNumber("amount").doubleValue(),
                jsonObject.getString("userId"));
    }


    @Override
    public JsonObject getJson() {
        return Json.createObjectBuilder()
                .add("orderId", orderId)
                .add("coinSymbol", coinSymbol)
                .add("amount", amount)
                .add("userId", userId)
                .build();
    }
}
