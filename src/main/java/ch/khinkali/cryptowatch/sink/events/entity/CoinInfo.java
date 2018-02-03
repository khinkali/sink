package ch.khinkali.cryptowatch.sink.events.entity;

import lombok.Getter;

import javax.json.JsonObject;
import java.util.UUID;

@Getter
public class CoinInfo {
    private final UUID orderId;
    private final String coinSymbol;
    private final Double amount;

    public CoinInfo(final UUID orderId, final String coinSymbol, final Double amount) {
        this.orderId = orderId;
        this.coinSymbol = coinSymbol;
        this.amount = amount;
    }

    public CoinInfo(JsonObject jsonObject) {
        this(UUID.fromString(jsonObject.getString("orderId")),
                jsonObject.getString("coinSymbol"),
                jsonObject.getJsonNumber("amount").doubleValue());
    }

}
