package ch.khinkali.cryptowatch.sink.events.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.json.bind.annotation.JsonbProperty;
import java.time.Instant;
import java.util.Objects;

@EqualsAndHashCode
public abstract class CoinEvent {

    @Getter
    @JsonbProperty
    private final Instant instant;

    protected CoinEvent() {
        instant = Instant.now();
    }

    protected CoinEvent(final Instant instant) {
        Objects.requireNonNull(instant);
        this.instant = instant;
    }

}
