package gitlet;
import java.time.ZonedDateTime;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {
    public static final Commit InitialCommit = null;
    public static Commit HEAD = InitialCommit;
    private Commit parent;
    private String hash;
    private ZonedDateTime time;
    private String branch;
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    public Commit(String sha, String m){
        message = m;
        branch = parent.branch;
        hash = sha;
        time = ZonedDateTime.now();
        parent = HEAD;

    }
    /* TODO: fill in the rest of this class. */
}
