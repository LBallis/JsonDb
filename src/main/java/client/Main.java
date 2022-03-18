package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.Command;
import server.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;

public class Main {

    static final String SERVER_ADDRESS = "127.0.0.1";
    static final int SERVER_PORT = 5555;

    public static void main(String[] args) {

//        actualExecution(args);
        localExecution();

    }

    private static void actualExecution(String[] args){
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Client started!");

            Command command = null;
            String key = null;
            String value = null;
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].equals("-t")) {
                    command = Command.valueOf(args[i+1].toUpperCase(Locale.ROOT));
                }
                if (args[i].equals("-k")) {
                    key = args[i+1];
                }
                if (args[i].equals("-v")) {
                    value = args[i+1];
                }
            }
            Request request = new Request(command,key,value);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Request.class, new RequestSerializer())
                    .registerTypeAdapter(Response.class, new ResponseDeserializer())
                    .create();

            String jsonRequest = gson.toJson(request);
            output.writeUTF(jsonRequest);
            System.out.println("Sent: " + jsonRequest);

            String receivedMsg = input.readUTF();
            System.out.println("Received: " + receivedMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void localExecution() {
        for (int i = 0; i <= 7; i++) {

            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            ) {
                System.out.println("Client started!");

                Request request = null;

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Request.class, new RequestSerializer())
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
                if (i == 0) {
                    request = new Request(Command.GET, "1", null);
                }
                if (i == 1) {
                    request = new Request(Command.SET, "1", "HelloWorld!");
                }
                if (i == 2) {
                    request = new Request(Command.SET, "1", "Hello World!!");
                }
                if (i == 3) {
                    request = new Request(Command.GET, "1", null);
                }
                if (i == 4) {
                    request = new Request(Command.DELETE, "1", null);
                }
                if (i == 5) {
                    request = new Request(Command.DELETE, "1", null);
                }
                if (i == 6) {
                    request = new Request(Command.GET, "1", null);
                }
                if (i == 7) {
                    request = new Request(Command.EXIT, null, null);
                }

                String jsonRequest = gson.toJson(request);
                output.writeUTF(jsonRequest);
                System.out.println("Sent: " + jsonRequest);

                String receivedMsg = input.readUTF();
                System.out.println("Received: " + receivedMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
