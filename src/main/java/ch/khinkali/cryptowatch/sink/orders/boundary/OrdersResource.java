package ch.khinkali.cryptowatch.sink.orders.boundary;

import ch.khinkali.cryptowatch.sink.orders.entity.OrderPlaced;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.UUID;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("orders")
public class OrdersResource {

    @Context
    UriInfo uriInfo;

    @Inject
    OrdersCommandService commandService;

    @Inject
    OrdersQueryService queryService;

    @Context
    private SecurityContext securityContext;

    @POST
    public Response order(JsonObject order) {
        final String coinSymbol = order.getString("coinSymbol", null);
        final Double amount = order.getJsonNumber("amount").doubleValue();
        final String userId = securityContext.getUserPrincipal().getName();

        if (coinSymbol == null || amount == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final UUID orderId = UUID.randomUUID();
        commandService.placeOrder(orderId.toString(), coinSymbol, amount, userId);

        final URI uri = uriInfo
                .getRequestUriBuilder()
                .path(OrdersResource.class, "getOrder")
                .build(orderId);
        return Response.accepted().header(HttpHeaders.LOCATION, uri).build();
    }

    @GET
    @Path("{id}")
    public JsonObject getOrder(@PathParam("id") UUID orderId) {
        final OrderPlaced order = queryService.getOrder(orderId.toString());

        if (order == null) {
            throw new NotFoundException();
        }

        return Json.createObjectBuilder()
                .add("coinSymbol", order.getCoinSymbol())
                .add("amount", order.getAmount())
                .build();
    }
}
