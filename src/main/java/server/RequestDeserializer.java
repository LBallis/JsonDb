package server;

import client.Request;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Locale;

public class RequestDeserializer implements JsonDeserializer<Request> {
    @Override
    public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonRequestType = jsonObject.get("type");
        JsonElement jsonRequestKey = jsonObject.get("key");
        JsonElement jsonRequestValue = jsonObject.get("value");

        return new Request(Command.valueOf(jsonRequestType.getAsString().toUpperCase(Locale.ROOT)),
                jsonRequestKey == null ? null : jsonRequestKey.getAsString(),
                jsonRequestValue == null ? null : jsonRequestValue.getAsString());
    }
}
