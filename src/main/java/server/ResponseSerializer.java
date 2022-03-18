package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ResponseSerializer implements JsonSerializer<Response> {
    @Override
    public JsonElement serialize(Response src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject responseJsonObj = new JsonObject();
        responseJsonObj.addProperty("response", src.getResponse());
        if(src.getReason() != null){
            responseJsonObj.addProperty("reason", src.getReason());
        }
        if(src.getValue() != null) {
            responseJsonObj.addProperty("value", src.getValue());
        }
        return responseJsonObj;
    }
}
