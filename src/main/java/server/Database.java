package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.exceptions.NoSuchKeyException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum Database {
    INSTANCE;

    private JsonObject database;
    private final Lock readLock;
    private final Lock writeLock;
    private final String DATABASE_PATH = "C:\\MyWorkspace\\training\\JsonDb\\src\\main\\java\\server\\data\\database.json";

    Database(){}

    {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public void init(){
        try {
            File file = new File(DATABASE_PATH);
            if(file.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(DATABASE_PATH)));
                database = new Gson().fromJson(content, JsonObject.class);
            }else{
                database = new JsonObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonElement get(JsonElement key){
        try{
            readLock.lock();
            if (key.isJsonPrimitive() && database.has(key.getAsString())){
                return database.get(key.getAsString());
            }else if(key.isJsonArray()){
                return findElement(key.getAsJsonArray(), false);
            }
            throw new NoSuchKeyException();
        }finally {
            readLock.unlock();
        }
    }

    public void set(JsonElement key, JsonElement value){
        try{
            writeLock.lock();
            if (key.isJsonPrimitive()){
                database.add(key.getAsString(), value);
            }else if(key.isJsonArray()){
                JsonArray keys = key.getAsJsonArray();
                String keyToAdd = keys.remove(keys.size() - 1).getAsString();
                findElement(keys,true).getAsJsonObject().add(keyToAdd, value);
            }
            saveDB();
        }finally {
            writeLock.unlock();
        }
    }

    public void delete(JsonElement key){
        try{
            writeLock.lock();
            if (key.isJsonPrimitive() && database.has(key.getAsString())){
                database.remove(key.getAsString());
            }else if (key.isJsonArray()){
                JsonArray keys = key.getAsJsonArray();
                String keyToDelete = keys.remove(keys.size() - 1).getAsString();
                findElement(keys, false).getAsJsonObject().remove(keyToDelete);
            }else {
                throw new NoSuchKeyException();
            }
            saveDB();
        }finally {
            writeLock.unlock();
        }
    }


    private JsonElement findElement(JsonArray keys, boolean createIfAbsent) {
        JsonElement tmp = database;
        if (createIfAbsent){
            for (JsonElement key : keys){
                if(!tmp.getAsJsonObject().has(key.getAsString())){
                    tmp.getAsJsonObject().add(key.getAsString(), new JsonObject());
                }
                tmp = tmp.getAsJsonObject().get(key.getAsString());
            }
        }else{
            for (JsonElement key : keys){
                if (!key.isJsonPrimitive() || !tmp.getAsJsonObject().has(key.getAsString())){
                    throw new NoSuchKeyException();
                }
                tmp = tmp.getAsJsonObject().get(key.getAsString());
            }
        }
        return tmp;
    }

    private void saveDB() {
        try {
            FileWriter writer = new FileWriter("C:\\MyWorkspace\\training\\JsonDb\\src\\main\\java\\server\\data\\database.json");
            writer.write(database.toString());
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
