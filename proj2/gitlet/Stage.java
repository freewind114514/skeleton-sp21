package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;


public class Stage implements Serializable {
    public static Map<String, byte[]> addStage;
    public static Map<String, byte[]> rmStage;

    public Stage(){
        addStage = new TreeMap<>();
        rmStage = new TreeMap<>();
    }

    public Map<String, byte[]> getAddStage(){
        return addStage;
    }

    public Map<String, byte[]> getRmStage(){
        return rmStage;
    }

    public void add(String filename, byte[] content){
        rmStage.remove(filename);
        addStage.put(filename, content);
    }

    public void remove(String filename){
        addStage.remove(filename);
        rmStage.put(filename, null);
    }

    public boolean ifClear(){
        return addStage.isEmpty() && rmStage.isEmpty();
    }

    public boolean AddNotContains(String filename){
        return !addStage.containsKey(filename);
    }

    public boolean ifRmStageContains(String filename){
        return rmStage.containsKey(filename);
    }


    public Commit clear(Commit c){
        for (Map.Entry<String, byte[]> entry : addStage.entrySet()){
            String filename = entry.getKey();
            byte[] content = entry.getValue();
            Bolb b = new Bolb(filename, content);
            String BID = b.getBID();
            c.addTrack(filename, BID);
            b.saveObject();
        }

        for (Map.Entry<String, byte[]> entry : rmStage.entrySet()){
            String filename = entry.getKey();
            c.rmTrack(filename);
        }

        addStage.clear();
        rmStage.clear();
        return c;
    }

    public void printStage(){

        System.out.println("=== Staged Files ===");
        for (String key : addStage.keySet()) {
            System.out.println(key);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String key : rmStage.keySet()) {
            System.out.println(key);
        }
        System.out.println();
    }

    public void save(){
        writeObject(STAGE, this);
    }

}