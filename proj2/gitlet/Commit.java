package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author freewind
 */
public class Commit implements Serializable {

    private String CID;
    private String parent;
    private String time;
    private Map<String, String> Track;
    private String message;
    private boolean merge = false;
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */


    public Commit(String m, String parentID){
        time = "Thu Jan 1 00:00:00 1970 -0800";
        message = m;
        parent = parentID;
        Track = new TreeMap<>();
        CID = sha1(message, parent, time, Track.toString());
    }

    public static Commit fromFile(String id){
        if (id == null){
            return null;
        }
        File object = join(COMMIT, id);
        return readObject(object, Commit.class);
    }

    public void addTrack(String filename, String BID){
        Track.put(filename, BID);
    }

    public void rmTrack(String filename){
        Track.remove(filename);
    }

    public void update(String m, String head){
        message = m;
        parent = head;
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy x");
        time = now.format(formatter);
        CID = sha1(message, parent, time, Track.toString());
    }


    public void saveObject(){
        File object = join(COMMIT, CID);
        try {
            object.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(object, this);
    }

    public static void printLog(Commit c){
        System.out.println("===");
        System.out.println("commit" + " " + c.CID);
        if (c.merge){
            System.out.println("Merge:" + " " + c.CID);
        }
        System.out.println("Date:" + " " + c.time);
        System.out.println(c.message);
        System.out.println();
    }

    public Map<String, String> getTrack(){
        return Track;
    }

    public String getID(){
        return CID;
    }

    public String getTime(){
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getParent() {
        return parent;
    }

    /* TODO: fill in the rest of this class. */
}
