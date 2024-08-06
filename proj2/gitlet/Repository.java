package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Commit.*;
import static gitlet.Utils.*;
import static gitlet.Utils.join;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGE = join(GITLET_DIR, "stage");
    private static File toCommit;

    /** check the .gitlet directory.
     * if already existed: print some error message,
     * else initialize it
     */
    public static void init(){
        if (GITLET_DIR.exists()) {
            Commit();
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            STAGE.mkdir();
        }
    }

    public static void add(String filename){
        File file = join(CWD, filename);
        toCommit = join(STAGE, filename);
        if (file.exists()) {
            // Adds a copy of the file as it currently exists to the staging area
            try {
                toCommit.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(toCommit, readContents(file));
        } else{
            System.out.println("File does not exist.");
        }
    }

    /**
     * Create new commit
     * Save commited file
     * Change head
     * Clear stage
     * Update log
     */
    public static void commit(String message){
        String sha = sha1(readContents(toCommit));
        Commit c = new Commit(sha, message);
        HEAD = c;
    }

    /* TODO: fill in the rest of this class. */
}
