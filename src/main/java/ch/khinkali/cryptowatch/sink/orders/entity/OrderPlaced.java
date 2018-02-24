package ch.khinkali.cryptowatch.sink.orders.entity;

import ch.khinkali.cryptowatch.events.entity.BaseEvent;
import lombok.Getter;

import javax.json.Json;
import javax.json.JsonObject;

@Getter
public class OrderPlaced extends BaseEvent {
    public static final String TOPIC = "coins";

    private final String orderId;
    private final String coinSymbol;
    private final Double amount;
    private final String userId;

    public OrderPlaced(final String orderId,
                       final String coinSymbol,
                       final Double amount,
                       final String userId) {
        this.orderId = orderId;
        this.coinSymbol = coinSymbol;
        this.amount = amount;
        this.userId = userId;
    }

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
