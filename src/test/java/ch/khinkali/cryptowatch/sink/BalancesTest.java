package ch.khinkali.cryptowatch.sink;

import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BalancesTest {
    public static final String LOCATION = "Location";

    private static String location;

    @Rule
    public JAXRSClientProvider provider =
            buildWithURI("http://" + System.getenv("HOST") + ":" + System.getenv("PORT") + "/sink/resources/balances");


    @Test
    public void a01_shouldAddBTC() {
        JsonObjectBuilder userBuilder = Json.createObjectBuilder();
        JsonObject coinToAdd = userBuilder
                .add("coinSymbol", "BTC")
                .add("amount", 2.2)
                .build();

        Response postResponse = provider
                .target()
                .request()
                .post(Entity.json(coinToAdd));
        assertThat(postResponse.getStatus(), is(202));
        location = postResponse.getHeaderString(LOCATION);
    }

    @Test
    public void a02_shouldReturnBTC() {
        JsonObject coin = provider
                .client()
                .target(location)
                .request()
                .get(JsonObject.class);
        assertThat(coin.getString("coinSymbol"), is("BTC"));
        assertThat(coin.getJsonNumber("amount").doubleValue(), is(2.2));
    }

}
