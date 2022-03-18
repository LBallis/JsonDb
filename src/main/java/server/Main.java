package server;

import client.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {

    static String database = new HashMap<String,String>().toString();
    static Scanner scanner = new Scanner(System.in);
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Request.class, new RequestDeserializer())
            .registerTypeAdapter(Response.class, new ResponseSerializer())
            .create();
    static final int PORT = 5555;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            boolean exit = false;
            String key;
            String value;

            while (!exit) {
                try (Socket socket = serverSocket.accept();
                     DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                ) {
                    String incomingMsg = input.readUTF();
                    Request request = gson.fromJson(incomingMsg, Request.class);

                    Response response = null;

                    switch (request.getType()) {
                        case GET:
                            key = request.getKey();
                            response = retrieve(key);
                            break;
                        case SET:
                            key = request.getKey();
                            value = request.getValue();
                            response = save(key, value);
                            break;
                        case DELETE:
                            key = request.getKey();
                            response = delete(key);
                            break;
                        case EXIT:
                            exit = true;
                            response = new Response("OK", null, null);
                            break;
                    }

                    String jsonResponse = gson.toJson(response);


                    output.writeUTF(jsonResponse);

                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Response retrieve(String key) {
        Map db = gson.fromJson(database, Map.class);
        if (db.get(key) == null) {
            return new Response("ERROR", "No such key", null);
        }
            return new Response("OK", null, (String) db.get(key));
    }

    private static Response save(String key, String value) {
        Map db = gson.fromJson(database, Map.class);
        db.put(key, value);
        database = gson.toJson(db);
        return new Response("OK", null, null);
    }

    private static Response delete(String key) {
        Map db = gson.fromJson(database, Map.class);
        if (db.remove(key) == null) {
            return new Response("ERROR", "No such key", null);
        }
        database = db.toString();
        return new Response("OK", null, null);
    }

}
