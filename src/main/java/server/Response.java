package server;

public class Response {
    String response;
    String reason;
    String value;

    public Response(String response, String reason, String value) {
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

    public String getValue() {
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
