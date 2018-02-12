package ch.khinkali.cryptowatch.sink.events.control;

import lombok.Getter;
import lombok.ToString;

import javax.json.JsonObject;

@ToString
@Getter
public class UserCreated {
    private final String userId;
    private final String username;

    public UserCreated(final String userId, final String username) {
        this.userId = userId;
        this.username = username;
    }

    public UserCreated(JsonObject jsonObject) {
        this(jsonObject.getString("userId"),
                jsonObject.getString("username"));
    }

}
