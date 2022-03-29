package client;

import com.beust.jcommander.JCommander;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Main {

    static final String SERVER_ADDRESS = "0.0.0.0";
    static final int SERVER_PORT = 5555;

    public static void main(String[] args) {

//        actualExecution(args);
        localExecution();

    }

    private static void actualExecution(String[] args) {
        ExecutionArgs executionArgs = new ExecutionArgs();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(executionArgs)
                .build();
        jCommander.parse(args);

        String request = executionArgs.toJson();

        clientServerCommunication(request);
    }

    private static void localExecution() {
        ArrayList<String[]> argsList = new ArrayList<>();
        argsList.add(new String[]{"-t", "exit"});
//        argsList.add(new String[]{"-t", "get", "-k", "[text]"});
//        argsList.add(new String[]{"-t", "set", "-k", "[1]", "-v", "Hello world!"});
//        argsList.add(new String[]{"-t", "set", "-k", "1", "-v", "HelloWorld!"});
//        argsList.add(new String[]{"-t", "get", "-k", "1"});
//        argsList.add(new String[]{"-t", "delete", "-k", "1"});
//        argsList.add(new String[]{"-t", "delete", "-k", "1"});
//        argsList.add(new String[]{"-t", "get", "-k", "1"});
//        argsList.add(new String[]{"-t", "set", "-k", "text", "-v", "Some text here"});
//        argsList.add(new String[]{"-t", "get", "-k", "text"});
//        argsList.add(new String[]{"-t", "get", "-k", "56"});
//        argsList.add(new String[]{"-t", "delete", "-k", "100"});
//        argsList.add(new String[]{"-t", "delete", "-k", "That key doesn't exist"});
//        argsList.add(new String[]{"-in", "testSet.json"});
//        argsList.add(new String[]{"-in", "testGet.json"});
//        argsList.add(new String[]{"-in", "testDelete.json"});
//        argsList.add(new String[]{"-in", "testGet.json"});

        for (String[] args : argsList) {
            ExecutionArgs executionArgs = new ExecutionArgs();
            JCommander jCommander = JCommander.newBuilder()
                    .addObject(executionArgs)
                    .build();
            jCommander.parse(args);

            String request = executionArgs.toJson();

            clientServerCommunication(request);
        }
    }


    private static void clientServerCommunication(String request) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Client started!");

            output.writeUTF(request);
            System.out.println("Sent: " + request);

            String receivedMsg = input.readUTF();
            System.out.println("Received: " + receivedMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
