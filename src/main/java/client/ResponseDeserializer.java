package client;

import com.google.gson.*;
import server.Response;

import java.lang.reflect.Type;

public class ResponseDeserializer implements JsonDeserializer<Response> {
    @Override
    public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonResponse = jsonObject.get("reasponse");
        JsonElement jsonResponseReason = jsonObject.get("reason");
        JsonElement jsonResponseValue = jsonObject.get("value");

        return new Response(jsonResponse.getAsString(),
                jsonResponseReason == null ? null : jsonResponseReason.getAsString(),
                jsonResponseValue == null ? null : jsonResponseValue.getAsString());
    }
}
