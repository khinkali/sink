package ch.khinkali.cryptowatch.sink.events.control;

import ch.khinkali.cryptowatch.sink.events.entity.BaseEvent;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class EventJsonbSerializer implements JsonbSerializer<BaseEvent> {

    @Override
    public void serialize(final BaseEvent event, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write("class", event.getClass().getCanonicalName());
        generator.writeStartObject("data");
        ctx.serialize("data", event, generator);
        generator.writeEnd();
    }

}
