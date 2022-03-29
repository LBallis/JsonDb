package client;

import com.beust.jcommander.Parameter;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExecutionArgs {

    @Expose
    @Parameter(
            names = "-t",
            description = "It's the type of Execution (GET,SET,DELETE,EXIT)"
    )
    String type;

    @Expose
    @Parameter(
            names = "-k",
            description = "Defines the key value"
    )
    String key;

    @Expose
    @Parameter(
            names = "-v",
            description = "Defines the value"
    )
    String value;

    @Parameter(
            names = "-in",
            description = "Defines the filename we parse as argument"
    )
    String fileName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String readFromFile(String fileName) {
        String INPUT_DATA_PATH = "C:\\MyWorkspace\\training\\JsonDb\\src\\main\\java\\client\\data\\";
        String content = null;
        try{
            content = new String(Files.readAllBytes(Paths.get(INPUT_DATA_PATH + fileName)));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
       return content;
    }

    String toJson(){
        if (fileName != null){
            return readFromFile(fileName);
        }else {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("type", type);
            map.put("key", key);
            map.put("value", value);

            return new Gson().toJson(map);
        }
    }
}
