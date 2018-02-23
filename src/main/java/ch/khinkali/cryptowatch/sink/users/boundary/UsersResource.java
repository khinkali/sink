package ch.khinkali.cryptowatch.sink.users.boundary;

import ch.khinkali.cryptowatch.sink.users.control.Users;
import ch.khinkali.cryptowatch.sink.users.entity.User;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("users")
public class UsersResource {

    @Inject
    Users users;

    @GET
    public JsonArray getUsers() {
        JsonArrayBuilder result = Json.createArrayBuilder();
        for (User user : users.getUsers().values()) {
            result.add(user.getJson());
        }
        return result.build();
    }

    @GET
    @Path("{id}")
    public JsonObject getUser(@PathParam("id") String userId) {
        User user = users.getUsers().get(userId);

        if (user == null) {
            throw new NotFoundException();
        }

        return user.getJson();
    }

    @GET
    @Path("{id}/coins")
    public JsonArray getUserCoins(@PathParam("id") String userId) {
        User user = users.getUsers().get(userId);

        if (user == null) {
            throw new NotFoundException();
        }

        return user.getCoinsAsJson();
    }

}
