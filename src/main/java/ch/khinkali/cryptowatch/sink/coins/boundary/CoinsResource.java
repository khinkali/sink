package ch.khinkali.cryptowatch.sink.coins.boundary;

import javax.json.Json;
import javax.json.JsonArray;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("coins")
public class CoinsResource {

    @GET
    public JsonArray getCoins() {
        return Json.createArrayBuilder()
                .add("ETH")
                .add("BTC")
                .add("XRP")
                .build();
    }

}
