package server;

import client.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    static String database;
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Request.class, new RequestDeserializer())
            .registerTypeAdapter(Response.class, new ResponseSerializer())
            .create();
    static final int PORT = 5555;
    static final String JSON_DATABASE_PATH = "JSON Database/task/src/server/data/db.json";
    static boolean exit = false;

    static ReadWriteLock lock = new ReentrantReadWriteLock();
    static Lock readLock = lock.readLock();
    static Lock writeLock = lock.writeLock();

    public static void main(String[] args) {
        initDatabase();

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
        synchronized (writeLock) {
            readLock.lock();
            Map db = gson.fromJson(database, Map.class);
            readLock.unlock();
            String value = (String) db.get(key);

            if (value == null) {
                return new Response("ERROR", "No such key", null);
            }
            return new Response("OK", null, value);
        }
    }

    private static Response save(String key, String value) {
        synchronized (readLock) {
            writeLock.lock();
            Map db = gson.fromJson(database, Map.class);
            db.put(key, value);
            database = gson.toJson(db);
            try {
                SerializationUtils.serialize(database, JSON_DATABASE_PATH);
                writeLock.unlock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Response("OK", null, null);
    }

    private static Response delete(String key) {

        Map db = gson.fromJson(database, Map.class);
        if (db.get(key) == null) {
            return new Response("ERROR", "No such key", null);
        }
        writeLock.lock();
        synchronized (readLock) {
            db.remove(key);
            database = db.toString();
            try {
                SerializationUtils.serialize(database, JSON_DATABASE_PATH);
                writeLock.unlock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Response("OK", null, null);
    }

    private static void initDatabase() {
        try {
            if (new File(JSON_DATABASE_PATH).exists()) {
                database = (String) SerializationUtils.deserialize(JSON_DATABASE_PATH);
            } else {
                database = new HashMap<String, String>().toString();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Response executeRequest(Request request){
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
        return response;
    }

    private static void serverClientCommunication(ServerSocket serverSocket) {

        ExecutorService service = Executors.newFixedThreadPool(10);

        service.submit(() -> {
            try (Socket socket = serverSocket.accept();
                 DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            ) {
                String incomingMsg = input.readUTF();
                Request request = gson.fromJson(incomingMsg, Request.class);
                Response response = executeRequest(request);
                String jsonResponse = gson.toJson(response);
                output.writeUTF(jsonResponse);
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        });

        try {
            service.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
