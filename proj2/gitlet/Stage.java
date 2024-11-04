package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Repository.getSTAGE;
import static gitlet.Utils.writeObject;


public class Stage implements Serializable {
    private Map<String, byte[]> addStage;
    private Map<String, byte[]> rmStage;

    public Stage() {
        addStage = new TreeMap<>();
        rmStage = new TreeMap<>();
    }

    public Map<String, byte[]> getAddStage() {
        return addStage;
    }

    public Map<String, byte[]> getRmStage() {
        return rmStage;
    }

    public void addStageRemove(String filename) {
        addStage.remove(filename);
    }

    public void add(String filename, byte[] content) {
        rmStage.remove(filename);
        addStage.put(filename, content);
    }

    public void pureAdd(String filename, byte[] content) {
        addStage.put(filename, content);
    }

    public void remove(String filename) {
        rmStage.put(filename, null);
    }

    public boolean ifClear() {
        return addStage.isEmpty() && rmStage.isEmpty();
    }

    public boolean addNotContains(String filename) {
        return !addStage.containsKey(filename);
    }

    public boolean ifRmStageContains(String filename) {
        return rmStage.containsKey(filename);
    }


    public Commit clearCommit(Commit c) {
        for (Map.Entry<String, byte[]> entry : addStage.entrySet()) {
            String filename = entry.getKey();
            byte[] content = entry.getValue();
            Bolb b = new Bolb(filename, content);
            String bid = b.getBID();
            c.addTrack(filename, bid);
            b.saveObject();
        }

        for (Map.Entry<String, byte[]> entry : rmStage.entrySet()) {
            String filename = entry.getKey();
            c.rmTrack(filename);
        }
        clear();
        return c;
    }

    public void clear() {
        addStage.clear();
        rmStage.clear();
    }

    public void printStage() {

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

    public void save() {
        writeObject(getSTAGE(), this);
    }

}
