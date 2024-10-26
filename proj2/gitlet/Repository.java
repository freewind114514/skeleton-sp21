package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  make Track(Map<Filename, BID>) for Commit?
 *  @author freewind
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT = join(GITLET_DIR, "commits");
    public static final File BOLBS = join(GITLET_DIR, "bolbs");
    public static final File STAGE = join(GITLET_DIR, "stage");
    public static final File BRANCH = join(GITLET_DIR, "branches");
    public static final File saveHead = join(GITLET_DIR, "head");
    public static String HEAD;
    public static File currentBranchHead = readObject(saveHead, File.class);

    /** check the .gitlet directory.
     * if already existed: print some error message,
     * else initialize it
     */
    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            BRANCH.mkdir();
            COMMIT.mkdir();
            BOLBS.mkdir();
            STAGE.createNewFile();
            saveHead.createNewFile();
            Stage stage = new Stage();
            stage.save();
            Commit initialCommit = new Commit("initial commit", null);
            initialCommit.saveObject();
            HEAD = initialCommit.getID();
            branch("master");
        }
    }

    public static void add(String filename){
        File file = join(CWD, filename);
        Stage stage = readObject(STAGE, Stage.class);
        if (file.exists()) {
            byte[] content = readContents(file);
            stage.add(filename, content);
            stage.save();
        } else{
            System.out.println("File does not exist.");
        }
    }

    public static void rm(String filename){
        Stage stage = readObject(STAGE, Stage.class);
        File file = join(CWD, filename);
        HEAD = readContentsAsString(currentBranchHead);
        Map<String, String> track = Commit.fromFile(HEAD).getTrack();

        if (!stage.ifAddStageContains(filename) && !track.containsKey(filename)){
            System.out.println("No reason to remove the file.");
            return;
        }

        stage.getAddStage().remove(filename);
        if (track.containsKey(filename)){
            stage.remove(filename);
            stage.save();
            if (file.exists()){
                file.delete();
            }
        }
    }

    public static void commit(String m){
        Stage stage = readObject(STAGE,Stage.class);
        HEAD = readContentsAsString(currentBranchHead);
        Commit c = Commit.fromFile(HEAD);
        if (stage.ifClear()){
            System.out.println("No changes added to the commit.");
            return;
        }
        if (m.isBlank()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        c = stage.clear(c);
        stage.save();
        c.update(m, HEAD);
        c.saveObject();
        writeContents(currentBranchHead, c.getID());
    }

    public static void log(){
        HEAD = readContentsAsString(currentBranchHead);
        Commit c = Commit.fromFile(HEAD);
        while (c != null) {
            Commit.printLog(c);
            c = Commit.fromFile(c.getParent());
        }
    }

    public static void globalLog(){
        List<String> commits = plainFilenamesIn(COMMIT);
        for (String commit : commits) {
            Commit.printLog(Commit.fromFile(commit));
        }
    }

    public static void find(String m){
        List<String> commits = plainFilenamesIn(COMMIT);
        boolean notfound = true;
        for (String commit : commits) {
            Commit c = Commit.fromFile(commit);
            String commitMessage = c.getMessage();
            if (commitMessage.contains(m)) {
                System.out.println(c.getID());
                notfound = false;
            }
        }
        if (notfound){
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status(){
        Stage stage = readObject(STAGE,Stage.class);
        HEAD = readContentsAsString(currentBranchHead);
        List<String> branches = plainFilenamesIn(BRANCH);

        Collections.sort(branches);
        String currentBranchName = currentBranchHead.getName();

        System.out.println("=== Branches ===");
        for (String str : branches) {
            if (Objects.equals(str, currentBranchName)){
                System.out.println("*" + currentBranchName);
            }else {
                System.out.println(str);
            }
        }
        System.out.println();
        stage.printStage();
        printModifiedStatus();
        printUntrackedStatus();
    }

    private static void printModifiedStatus(){
        Stage stage = readObject(STAGE,Stage.class);
        HEAD = readContentsAsString(currentBranchHead);
        Commit c = Commit.fromFile(HEAD);
        Map<String, byte[]> addStage = stage.getAddStage();
        Map<String, String> Track = c.getTrack();
        List<String> filenames = plainFilenamesIn(CWD);
        List<String> M = new ArrayList<>();

        for (String filename : Track.keySet()){
            if (filenames.contains(filename)) {
                String id = sha1(readContents(join(CWD, filename)));
                if (!Track.get(filename).equals(id) && !stage.ifAddStageContains(filename)) {
                    M.addLast(filename);
                }
            }else {
                if (!stage.ifRmStageContains(filename)){
                    M.addLast(filename);
                }
            }
        }
        for (String filename : addStage.keySet()){
            if (filenames.contains(filename)){
                String id1 = sha1(readContents(join(CWD, filename)));
                String id2 = sha1(addStage.get(filename));
                if (!id1.equals(id2)) {
                    M.addLast(filename);
                }
            }else {
                M.addLast(filename);
            }
        }
        Collections.sort(M);
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String str : M){
            System.out.println(str);
        }
        System.out.println();
    }

    private static void printUntrackedStatus(){
        List<String> filenames = plainFilenamesIn(CWD);
        Stage stage = readObject(STAGE,Stage.class);
        HEAD = readContentsAsString(currentBranchHead);
        Commit c = Commit.fromFile(HEAD);
        Map<String, String> Track = c.getTrack();
        List<String> U = new ArrayList<>();
        for (String filename : filenames){
            if (!stage.ifAddStageContains(filename) && !Track.keySet().contains(filename)){
                U.addLast(filename);
            }
            if (stage.ifRmStageContains(filename)){
                U.addLast(filename);
            }
        }
        Collections.sort(U);
        System.out.println("=== Untracked Files ===");
        for (String str : U){
            System.out.println(str);
        }
        System.out.println();
    }

    public static void branch(String name) throws IOException {
        if (HEAD == null) {
            HEAD = readContentsAsString(currentBranchHead);
        }
        File newBranch = join(BRANCH, name);
        if (newBranch.exists()){
            System.out.println("A branch with that name already exists.");
            return;
        }
        newBranch.createNewFile();
        writeObject(saveHead, newBranch);
        writeContents(newBranch, HEAD);
    }
    /* TODO: fill in the rest of this class. */
}
