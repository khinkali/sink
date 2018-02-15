package ch.khinkali.cryptowatch.sink.events.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.json.JsonObject;

@AllArgsConstructor
@Getter
public class OrderPlaced {
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
}
