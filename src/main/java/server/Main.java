package server;

import client.Request;
import com.google.gson.Gson;
import server.commands.DeleteCommand;
import server.commands.GetCommand;
import server.commands.SetCommand;
import server.exceptions.NoSuchKeyException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static server.Database.INSTANCE;

public class Main {

    static final int PORT = 5555;
    static boolean exit = false;


    public static void main(String[] args) {
        INSTANCE.init();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");

            while (!serverSocket.isClosed()) {
                serverClientCommunication(serverSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Response executeRequest(Request request){

        Response response = null;
        switch (request.getType().toUpperCase(Locale.ROOT)) {
            case "GET":
                try {
                    GetCommand get = new GetCommand(request.getKey());
                    get.execute();
                    response = new Response("OK", null, get.getResult());
                }catch (NoSuchKeyException e){
                    response = new Response("ERROR", e.getMessage(),null);
                }
                break;
            case "SET":
                try{
                    new SetCommand(request.getKey(), request.getValue()).execute();
                    response = new Response("OK", null, null);
                }catch (NoSuchKeyException e){
                    response = new Response("ERROR", e.getMessage(),null);
                }
                break;
            case "DELETE":
                try{
                    new DeleteCommand(request.getKey()).execute();
                    response = new Response("OK", null, null);
                }catch (NoSuchKeyException e){
                    response = new Response("ERROR", e.getMessage(),null);
                }
                break;
            case "EXIT":
                exit = true;
                response = new Response("OK", null, null);
                break;
        }
        return response;
    }

    private static void serverClientCommunication(ServerSocket serverSocket) {

        Gson gson = new Gson();

        new Thread(() -> {
            try (Socket socket = serverSocket.accept();
                 DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            ) {
                String incomingMsg = input.readUTF();
                Request request = gson.fromJson(incomingMsg, Request.class);

                Response response = executeRequest(request);
                String jsonResponse = gson.toJson(response);
                output.writeUTF(jsonResponse);
                if (exit){
                   serverSocket.close();
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
