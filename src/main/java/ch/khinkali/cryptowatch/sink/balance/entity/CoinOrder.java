package ch.khinkali.cryptowatch.sink.balance.entity;

import ch.khinkali.cryptowatch.sink.events.entity.CoinInfo;
import lombok.Getter;

@Getter
public class CoinOrder {

    private CoinOrderState state;
    private CoinInfo coinInfo;

    public enum CoinOrderState {
        PLACED
    }

    public void place(final CoinInfo orderInfo) {
        state = CoinOrderState.PLACED;
        this.coinInfo = orderInfo;
    }

}