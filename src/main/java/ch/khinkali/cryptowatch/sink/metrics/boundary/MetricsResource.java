package ch.khinkali.cryptowatch.sink.metrics.boundary;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.OutputStreamWriter;
import java.io.Writer;

@Produces(MediaType.TEXT_PLAIN)
@Path("metrics")
public class MetricsResource {

    @GET
    public StreamingOutput metrics() {
        return output -> {
            try (Writer writer = new OutputStreamWriter(output)) {
                TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
            }
        };
    }

}
