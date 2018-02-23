package ch.khinkali.cryptowatch.sink.orders.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class Coin {
    private String coinSymbol;
}
