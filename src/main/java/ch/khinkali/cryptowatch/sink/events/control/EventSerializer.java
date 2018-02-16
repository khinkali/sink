package ch.khinkali.cryptowatch.sink.events.control;

import ch.khinkali.cryptowatch.sink.events.entity.BaseEvent;
import ch.khinkali.cryptowatch.sink.events.entity.OrderPlaced;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

public class EventSerializer implements Serializer<OrderPlaced> {

    private static final Logger logger = Logger.getLogger(EventSerializer.class.getName());

    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey) {
        // nothing to configure
    }

    @Override
    public byte[] serialize(final String topic, final OrderPlaced event) {
        try {
            if (event == null)
                return null;

            final JsonbConfig config = new JsonbConfig()
                    .withSerializers(new EventJsonbSerializer());

            final Jsonb jsonb = JsonbBuilder.create(config);

            return jsonb.toJson(event, BaseEvent.class).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.severe("Could not serialize event: " + e.getMessage());
            throw new SerializationException("Could not serialize event", e);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

}
