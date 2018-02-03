package ch.khinkali.cryptowatch.sink.events.control;

import ch.khinkali.cryptowatch.sink.events.entity.CoinEvent;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class EventJsonbSerializer implements JsonbSerializer<CoinEvent> {

    @Override
    public void serialize(final CoinEvent event, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write("class", event.getClass().getCanonicalName());
        generator.writeStartObject("data");
        ctx.serialize("data", event, generator);
        generator.writeEnd();
    }

}