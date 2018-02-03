package ch.khinkali.cryptowatch.sink.balances.boundary;

import ch.khinkali.cryptowatch.sink.balances.entity.CoinOrder;
import ch.khinkali.cryptowatch.sink.events.entity.CoinInfo;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("balances")
public class BalancesResource {

    @Inject
    Logger logger;

    @Context
    UriInfo uriInfo;

    @Inject
    BalancesCommandService commandService;

    @Inject
    BalancesQueryService queryService;

    @POST
    public Response orderCoin(JsonObject order) {
        final String coinSymbol = order.getString("coinSymbol", null);
        final Double amount = order.getJsonNumber("amount").doubleValue();

        if (coinSymbol == null || amount == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final UUID orderId = UUID.randomUUID();
        commandService.placeOrder(new CoinInfo(orderId, coinSymbol, amount));

        final URI uri = uriInfo
                .getRequestUriBuilder()
                .path(BalancesResource.class, "getOrder")
                .build(orderId);
        return Response.accepted().header(HttpHeaders.LOCATION, uri).build();
    }

    @GET
    @Path("{id}")
    public JsonObject getOrder(@PathParam("id") UUID orderId) {
        final CoinOrder order = queryService.getOrder(orderId);

        if (order == null) {
            throw new NotFoundException();
        }

        return Json.createObjectBuilder()
                .add("status", order.getState().name().toLowerCase())
                .add("coinSymbol", order.getCoinInfo().getCoinSymbol())
                .add("amount", order.getCoinInfo().getAmount())
                .build();
    }
}
