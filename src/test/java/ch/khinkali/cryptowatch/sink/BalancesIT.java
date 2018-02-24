package ch.khinkali.cryptowatch.sink;

import ch.khinkali.cryptowatch.sink.security.KeycloakHeaderCreator;
import ch.khinkali.cryptowatch.sink.users.entity.User;
import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BalancesIT {
    public static final String LOCATION = "Location";

    private static String location;

    @Rule
    public JAXRSClientProvider provider =
            buildWithURI("http://" + System.getenv("HOST") + ":" + System.getenv("PORT") + "/sink/resources");

    @After
    public void tearUp() {
        provider.client().close();
    }

    private String getToken() throws IOException {
        return KeycloakHeaderCreator
                .getTokenResponse(
                        System.getenv("APPLICATION_USER_NAME"),
                        System.getenv("APPLICATION_PASSWORD"))
                .getToken();
    }

    @Test(timeout = 20_000L)
    public void a01_shouldAddBTC() throws IOException {
        JsonObjectBuilder userBuilder = Json.createObjectBuilder();
        JsonObject coinToAdd = userBuilder
                .add("coinSymbol", "BTC")
                .add("amount", 2.2)
                .build();

        Response postResponse = provider
                .target()
                .path("orders")
                .request()
                .header("Authorization", "Bearer " + getToken())
                .post(Entity.json(coinToAdd));
        assertThat(postResponse.getStatus(), is(HttpStatus.SC_ACCEPTED));
        location = postResponse.getHeaderString(LOCATION);
    }

    @Test(timeout = 1_000L)
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

    @Test(timeout = 1_000L)
    public void a03_shouldReturnAllUsers() throws IOException {
        JsonArray users = provider
                .target()
                .path("users")
                .request()
                .header("Authorization", "Bearer " + getToken())
                .get(JsonArray.class);
        assertFalse(users.isEmpty());
        System.out.println("users = " + users);
    }

    @Test(timeout = 1_000L)
    public void a04_shouldFailWithoutBearerToken() {
        JsonObjectBuilder userBuilder = Json.createObjectBuilder();
        JsonObject coinToAdd = userBuilder
                .add("coinSymbol", "BTC")
                .add("amount", 2.2)
                .build();

        Response postResponse = provider
                .target()
                .path("orders")
                .request()
                .post(Entity.json(coinToAdd));
        assertThat(postResponse.getStatus(), is(HttpStatus.SC_UNAUTHORIZED));
    }

    @Test(timeout = 1_000L)
    public void a05_shouldReturnAllUser() throws IOException {
        String userId = System.getenv("APPLICATION_USER_ID");
        JsonObject user = provider
                .target()
                .path("users")
                .path(userId)
                .request()
                .header("Authorization", "Bearer " + getToken())
                .get(JsonObject.class);
        assertThat(new User(user).getId(), is(userId));
    }

    @Test(timeout = 1_000L)
    public void a06_shouldReturnCoins() throws IOException {
        String userId = System.getenv("APPLICATION_USER_ID");
        JsonArray coins = provider
                .target()
                .path("users")
                .path(userId)
                .path("coins")
                .request()
                .header("Authorization", "Bearer " + getToken())
                .get(JsonArray.class);
        assertFalse(coins.isEmpty());
    }

}
