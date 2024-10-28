package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private List<String> parents;
    private String time;
    private Map<String, String> Track;
    private String message;
    private Date date;
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */


    public Commit(){
        date = new Date(0);
        time = getTimestamp();
        message = "initial commit";
        parents = new ArrayList<>();;
        Track = new TreeMap<>();
        CID = generateId();
    }

    private String generateId(){
        return sha1(getTimestamp(), message, parents.toString(), Track.toString());
    }

    public String getTimestamp() {
        // Thu Jan 1 00:00:00 1970 +0000
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
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
        parents.clear();
        parents.add(head);
        date = new Date();
        time = getTimestamp();
        CID = generateId();
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
        if (c.parents.size() > 1){
            System.out.println("Merge:" + " " + c.parents.get(0).substring(0, 7)
                    + " " + c.parents.get(1).substring(0, 7));
            System.out.println();
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
        if (parents.isEmpty()){
            return null;
        }
        return parents.get(0);
    }

    /* TODO: fill in the rest of this class. */
}
