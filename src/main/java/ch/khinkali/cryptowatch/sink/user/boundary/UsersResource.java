package ch.khinkali.cryptowatch.sink.user.boundary;

import ch.khinkali.cryptowatch.sink.orders.entity.Coin;
import ch.khinkali.cryptowatch.sink.user.control.Users;
import ch.khinkali.cryptowatch.sink.user.entity.User;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
            JsonArrayBuilder coins = Json.createArrayBuilder();
            for (Coin coin : user.getCoins().keySet()) {
                JsonObject coinJson = Json.createObjectBuilder()
                        .add("coinSymbol", coin.getCoinSymbol())
                        .add("amount", user.getCoins().get(coin))
                        .build();
                coins.add(coinJson);
            }

            JsonObject userJson = Json.createObjectBuilder()
                    .add("userId", user.getUserId())
                    .add("username", user.getUsername())
                    .add("coins", coins)
                    .build();
            result.add(userJson);
        }
        return result.build();
    }

}
