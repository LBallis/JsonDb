package server;

import client.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {

    static String database;
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Request.class, new RequestDeserializer())
            .registerTypeAdapter(Response.class, new ResponseSerializer())
            .create();
    static final int PORT = 5555;
    static final String JSON_DATABASE_PATH = "database.json";
    static boolean exit = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (new File(JSON_DATABASE_PATH).exists()){
            database = (String) SerializationUtils.deserialize(JSON_DATABASE_PATH);
        }else{
            database = new HashMap<String,String>().toString();
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            while (!exit) {
                serverClientCommunication(serverSocket);
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

    private static Response save(String key, String value) throws IOException {
        Map db = gson.fromJson(database, Map.class);
        db.put(key, value);
        database = gson.toJson(db);
        SerializationUtils.serialize(database,JSON_DATABASE_PATH);
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


    private static void serverClientCommunication(ServerSocket serverSocket) {

        try (Socket socket = serverSocket.accept();
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            String incomingMsg = input.readUTF();
            Request request = gson.fromJson(incomingMsg, Request.class);
            String key;
            String value;

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
}
