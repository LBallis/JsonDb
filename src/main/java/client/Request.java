package client;

import com.beust.jcommander.Parameter;
import com.google.gson.JsonElement;

public class Request {
    @Parameter(names = "-t, --type")
    String type;
    @Parameter(names = "-k, --key")
    JsonElement key;
    @Parameter(names = "-v, --value")
    JsonElement value;

    public Request(String type, JsonElement key, JsonElement value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonElement getKey() {
        return key;
    }

    public void setKey(JsonElement key) {
        this.key = key;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", key=" + key +
                ", value=" + value +
                '}';
    }
}
