package ch.khinkali.cryptowatch.sink.events.entity;

import lombok.Getter;

import javax.json.JsonObject;
import java.time.Instant;

public class OrderPlaced extends CoinEvent {

    @Getter
    private final CoinInfo coinInfo;

    public OrderPlaced(final CoinInfo coinInfo) {
        this.coinInfo = coinInfo;
    }

    public OrderPlaced(final CoinInfo coinInfo, Instant instant) {
        super(instant);
        this.coinInfo = coinInfo;
    }

    public OrderPlaced(JsonObject jsonObject) {
        this(new CoinInfo(jsonObject.getJsonObject("coinInfo")), Instant.parse(jsonObject.getString("instant")));
    }
}
