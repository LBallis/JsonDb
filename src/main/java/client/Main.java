package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import server.Command;
import server.RequestDeserializer;
import server.Response;
import server.SerializationUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static final String SERVER_ADDRESS = "0.0.0.0";
    static final int SERVER_PORT = 5555;
    static final String INPUT_DATA_PATH = "JSON Database/task/src/client/data/";
    static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Request.class, new RequestSerializer())
            .registerTypeAdapter(Request.class, new RequestDeserializer())
            .registerTypeAdapter(Response.class, new ResponseDeserializer())
            .create();

    public static void main(String[] args) {

//        actualExecution(args);
        localExecution();

    }

    private static void actualExecution(String[] args){
        Command command = null;
        String key = null;
        String value = null;
        String fileName = null;

        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-in")){
                fileName = args[i+1];
            }
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
        if (fileName == null){
            Request request = new Request(command,key,value);
            clientServerCommunication(request);
        }else{
            clientServerCommunication(readInput(fileName));
        }
    }

    private static void localExecution() {
        ExecutorService clientService = Executors.newFixedThreadPool(4);
        for (int i = 0; i <= 1; i++) {
            int finalI = i;
            clientService.submit(() -> {
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
//                    if (finalI == 1) {
//                        request = new Request(Command.GET, "1", null);
//                    }
//                    if (finalI == 1) {
//                        request = new Request(Command.SET, "1", "HelloWorld!");
//                    }
//                    if (finalI == 2) {
//                        request = new Request(Command.SET, "1", "Hello World!!");
//                    }
//                    if (finalI == 3) {
//                        request = new Request(Command.GET, "1", null);
//                    }
//                    if (finalI == 4) {
//                        request = new Request(Command.DELETE, "1", null);
//                    }
//                    if (finalI == 5) {
//                        request = new Request(Command.DELETE, "1", null);
//                    }
//                    if (finalI == 6) {
//                        request = new Request(Command.GET, "1", null);
//                    }
                    if (finalI == 0) {
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
            });

        }
    }

    private static Request readInput(String fileName) {
        Request request = null;
        String input = null;
        try {
            input = deserializeInput(INPUT_DATA_PATH + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        request = gson.fromJson(input, Request.class);
        return request;
    }

    private static String deserializeInput(String fileName) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(fileName));
        reader.setLenient(true);
        JsonObject json = (JsonObject) JsonParser.parseReader(reader);
        return json.toString();
    }

    private static void clientServerCommunication(Request request) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Client started!");

            String jsonRequest = gson.toJson(request);
            output.writeUTF(jsonRequest);
            String receivedMsg = input.readUTF();

            System.out.println("Sent: " + jsonRequest);
            System.out.println("Received: " + receivedMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
