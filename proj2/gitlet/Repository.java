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
    private static String HEAD;
    private static File currentBranchHead;

    private static String getHeadID() {
        return readContentsAsString(getCurrentBranchHead());
    }

    private static File getCurrentBranchHead(){
        return readObject(saveHead, File.class);
    }

    public static void checkGitlet() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void gitletExists(){
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public static void init() {
        gitletExists();
        GITLET_DIR.mkdir();
        BRANCH.mkdir();
        COMMIT.mkdir();
        BOLBS.mkdir();
        try {
            STAGE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            saveHead.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage();
        stage.save();
        Commit initialCommit = new Commit();
        initialCommit.saveObject();
        branch("master", initialCommit.getID());
        writeObject(saveHead, join(BRANCH, "master"));
    }

    public static void add(String filename){
        File file = join(CWD, filename);
        Stage stage = readObject(STAGE, Stage.class);
        Map<String, String> Track = readObject(join(COMMIT, getHeadID()), Commit.class).getTrack();
        if (file.exists()) {
            byte[] content = readContents(file);
            String id = sha1(content);
            if (Track.containsValue(id)) {
                stage.addStageRemove(filename);
                stage.save();
                System.exit(0);
            }
            stage.add(filename, content);
            stage.save();
        } else{
            System.out.println("File does not exist.");
        }
    }

    public static void rm(String filename){
        Stage stage = readObject(STAGE, Stage.class);
        File file = join(CWD, filename);
        HEAD = getHeadID();
        Map<String, String> track = Commit.fromFile(HEAD).getTrack();

        if (stage.AddNotContains(filename) && !track.containsKey(filename)){
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
        HEAD = getHeadID();
        currentBranchHead = getCurrentBranchHead();
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
        HEAD = getHeadID();
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
        HEAD = getHeadID();
        currentBranchHead = getCurrentBranchHead();
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
        HEAD = getHeadID();
        Commit c = Commit.fromFile(HEAD);
        Map<String, byte[]> addStage = stage.getAddStage();
        Map<String, String> Track = c.getTrack();
        List<String> filenames = plainFilenamesIn(CWD);
        List<String> M = new ArrayList<>();

        for (String filename : Track.keySet()){
            if (filenames.contains(filename)) {
                String id = sha1(readContents(join(CWD, filename)));
                if (!Track.get(filename).equals(id) && stage.AddNotContains(filename)) {
                    M.add(filename);
                }
            }else {
                if (!stage.ifRmStageContains(filename)){
                    M.add(filename);
                }
            }
        }
        for (String filename : addStage.keySet()){
            if (filenames.contains(filename)){
                String id1 = sha1(readContents(join(CWD, filename)));
                String id2 = sha1(addStage.get(filename));
                if (!id1.equals(id2)) {
                    M.add(filename);
                }
            }else {
                M.add(filename);
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
        HEAD = getHeadID();
        Commit c = Commit.fromFile(HEAD);
        Map<String, String> Track = c.getTrack();
        List<String> U = new ArrayList<>();
        for (String filename : filenames){
            if (stage.AddNotContains(filename) && !Track.keySet().contains(filename)){
                U.add(filename);
            }
            if (stage.ifRmStageContains(filename)){
                U.add(filename);
            }
        }
        Collections.sort(U);
        System.out.println("=== Untracked Files ===");
        for (String str : U){
            System.out.println(str);
        }
        System.out.println();
    }

    private static void check(String CID, String filename) {
        Commit c = Commit.fromFile(CID);
        Map<String, String> Track = c.getTrack();
        if (Track.containsKey(filename)) {
            Bolb b = readObject(join(BOLBS, Track.get(filename)), Bolb.class);
            byte[] content = b.getContent();
            File file = join(CWD, filename);
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            writeContents(file, content);
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    public static void checkFile(String filename) {
        HEAD = getHeadID();
        check(HEAD, filename);
    }

    public static void checkCommitFile(String CID, String filename) {
        File commit = join(COMMIT, CID);
        if (commit.exists()) {
            check(CID, filename);
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public static void checkBranch(String branchName){

    }

    public static void branch(String name, String CID) {
        File newBranch = join(BRANCH, name);
        if (newBranch.exists()){
            System.out.println("A branch with that name already exists.");
        }else {
            try {
                newBranch.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(newBranch, CID);
        }
    }

    public static void rmBranch(String name) {
        File branch = join(BRANCH, name);
        currentBranchHead = getCurrentBranchHead();
        if (branch == currentBranchHead){
            System.out.println("Cannot remove the current branch.");
        } else {
            if (!restrictedDelete(branch)) {
                System.out.println("A branch with that name does not exist.");
            }
        }
    }

    /* TODO: fill in the rest of this class. */
}
