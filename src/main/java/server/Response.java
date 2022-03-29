package server;

import com.google.gson.JsonElement;

public class Response {
    String response;
    String reason;
    JsonElement value;

    public Response(String response, String reason, JsonElement value) {
        this.response = response;
        this.reason = reason;
        this.value = value;
    }

    public String getResponse() {
        return response;
    }

    public String getReason() {
        return reason;
    }

    public JsonElement getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Response{" +
                "response='" + response + '\'' +
                ", reason='" + reason + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
