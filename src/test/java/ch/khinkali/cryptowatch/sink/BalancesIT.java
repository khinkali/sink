package ch.khinkali.cryptowatch.sink;

import ch.khinkali.cryptowatch.sink.security.KeycloakHeaderCreator;
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
import java.io.IOException;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BalancesIT {
    public static final String LOCATION = "Location";

    private static String location;

    @Rule
    public JAXRSClientProvider provider =
            buildWithURI("http://" + System.getenv("HOST") + ":" + System.getenv("PORT") + "/sink/resources/balances");


    private String getToken() throws IOException {
        return KeycloakHeaderCreator
                .getTokenResponse(
                        System.getenv("APPLICATION_USER_NAME"),
                        System.getenv("APPLICATION_PASSWORD"))
                .getToken();
    }

    @Test
    public void a01_shouldAddBTC() throws IOException {
        JsonObjectBuilder userBuilder = Json.createObjectBuilder();
        JsonObject coinToAdd = userBuilder
                .add("coinSymbol", "BTC")
                .add("amount", 2.2)
                .build();

        Response postResponse = provider
                .target()
                .request()
                .header("Authorization", "Bearer " + getToken())
                .post(Entity.json(coinToAdd));
        assertThat(postResponse.getStatus(), is(202));
        location = postResponse.getHeaderString(LOCATION);
    }

    @Test
    public void a02_shouldReturnBTC() throws IOException {
        JsonObject coin = provider
                .client()
                .target(location)
                .request()
                .header("Authorization", "Bearer " + getToken())
                .get(JsonObject.class);
        assertThat(coin.getString("coinSymbol"), is("BTC"));
        assertThat(coin.getJsonNumber("amount").doubleValue(), is(2.2));
    }

}
