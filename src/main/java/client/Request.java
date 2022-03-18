package client;

import server.Command;

public class Request {
    Command type;
    String key;
    String value;

    public Request(Command type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Command getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
