package client;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Locale;

public class RequestSerializer implements JsonSerializer<Request> {
    @Override
    public JsonElement serialize(Request src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject requestJsonObj = new JsonObject();

        requestJsonObj.addProperty("type", src.getType().name().toLowerCase(Locale.ROOT));
        if(src.getKey() != null){
            requestJsonObj.addProperty("key", src.getKey());
        }
        if(src.getValue() != null) {
            requestJsonObj.addProperty("value", src.getValue());
        }
        return requestJsonObj;
    }
}
