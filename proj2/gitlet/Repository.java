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
        setBranch("master", initialCommit.getID());
        writeObject(saveHead, join(BRANCH, "master"));
    }

    public static void add(String filename){
        File file = join(CWD, filename);
        Stage stage = readObject(STAGE, Stage.class);
        Map<String, String> Track = Commit.fromFile(getHeadID()).getTrack();
        if (file.exists()) {
            byte[] content = readContents(file);
            String id = sha1(content);
            stage.add(filename, content);
            stage.save();
            if (Track.containsKey(filename) && Track.get(filename).equals(id)) {
                stage.addStageRemove(filename);
                stage.save();
            }
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
            System.exit(0);
        }

        stage.getAddStage().remove(filename);
        if (track.containsKey(filename)) {
            stage.remove(filename);
            if (file.exists()){
                file.delete();
            }
        }
        stage.save();
    }

    private static void setCommit(String m, String secondCID){
        Stage stage = readObject(STAGE,Stage.class);
        HEAD = getHeadID();
        currentBranchHead = getCurrentBranchHead();
        Commit c = Commit.fromFile(HEAD);
        if (stage.ifClear()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (m.isBlank()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        c = stage.clearCommit(c);
        stage.save();
        c.update(m, HEAD, secondCID);
        c.saveObject();
        writeContents(currentBranchHead, c.getID());
    }

    public static void commit (String m) {
        setCommit(m, null);
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
                    M.add(filename + " " +"(modified)");
                }
            }else {
                if (!stage.ifRmStageContains(filename)){
                    M.add(filename + " " +"(deleted)");
                }
            }
        }
        for (String filename : addStage.keySet()){
            if (filenames.contains(filename)){
                String id1 = sha1(readContents(join(CWD, filename)));
                String id2 = sha1(addStage.get(filename));
                if (!id1.equals(id2)) {
                    M.add(filename + " " +"(modified)");
                }
            }else {
                M.add(filename + " " +"(deleted)");
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
        checkCommitFile(HEAD, filename);
    }

    public static void checkCommitFile(String ID, String filename) {
        String CID = shortIdCommit(ID);
        ifCommitExists(CID);
        check(CID, filename);
    }

    private static void ifCommitExists(String CID){
        File commit = join(COMMIT, CID);
        if (CID == null || !commit.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
    }

    private static String shortIdCommit(String shortId) {
        List<String> commits = plainFilenamesIn(COMMIT);
        int length = shortId.length();
        if (length < 6) {
            return null;
        }
        assert commits != null;
        for (String commitId: commits) {
            if (commitId.substring(0, length).equals(shortId)) {
                return commitId;
            }
        }
        return null;
    }

    public static void checkOutBranch(String branchName){
        checkCheckOutBranch(branchName);
        String CID = readContentsAsString(join(BRANCH, branchName));
        List<String> filenames = plainFilenamesIn(CWD);
        Map<String, String> Track = Commit.fromFile(CID).getTrack();
        deleteUntracked(Track, filenames);
        checkAll(CID,Track);
        clearSaveStage();
        writeObject(saveHead, join(BRANCH, branchName));
    }

    private static void checkAll (String CID, Map<String, String> Track) {
        for (String filename : Track.keySet()) {
            checkCommitFile(CID, filename);
        }
    }

    private static void clearSaveStage() {
        Stage stage = readObject(STAGE,Stage.class);
        stage.clear();
        stage.save();
    }

    private static void deleteUntracked(Map<String, String> Track, List<String> filenames) {
        for (String name : filenames) {
            if (!Track.containsKey(name)) {
                File file = join(CWD, name);
                file.delete();
            }
        }
    }

    private static void ifOverwrite(Map<String, String> Track) {
        List<String> filenames = plainFilenamesIn(CWD);
        Map<String, String> currentTrack = Commit.fromFile(getHeadID()).getTrack();
        for (String name : filenames) {
            if (!currentTrack.containsKey(name) && Track.containsKey(name)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private static void checkCheckOutBranch(String branchName){
        File file = join(BRANCH, branchName);
        if (!file.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String givenCID = readContentsAsString(file);
        Map<String, String> givenTrack = Commit.fromFile(givenCID).getTrack();
        if (file == getCurrentBranchHead()) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        } else {
            ifOverwrite(givenTrack);
        }

    }

    public static void branch(String name) {
        HEAD = getHeadID();
        setBranch(name,HEAD);
    }

    private static void setBranch(String name, String CID) {
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
        String currentBranchName = getCurrentBranchHead().getName();
        File file = join(BRANCH, name);
        if (name.equals(currentBranchName)){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        if (!file.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        file.delete();
    }

    public static void reset(String CID){
        ifCommitExists(CID);
        List<String> filenames = plainFilenamesIn(CWD);
        Map<String, String> Track = Commit.fromFile(CID).getTrack();
        ifOverwrite(Track);
        checkAll(CID, Track);
        writeContents(getCurrentBranchHead(), CID);
        deleteUntracked(Track, filenames);
        clearSaveStage();
    }

    public static void merge(String branchName) {
        checkMerge(branchName);
        String currentCID = getHeadID();
        String givenCID = readContentsAsString(join(BRANCH, branchName));
        Commit splitPoint = getSplitPoint(currentCID, givenCID);
        ifAncestor(branchName, splitPoint.getID(), currentCID, givenCID);
        Map<String, String> TrackSplit = splitPoint.getTrack();
        Map<String, String> TrackCurrent = Commit.fromFile(currentCID).getTrack();
        Map<String, String> TrackGiven = Commit.fromFile(givenCID).getTrack();
        Stage stage = readObject(STAGE, Stage.class);
        String currentBID;
        String givenBID;
        String splitBID;
        boolean ifConflict = false;


        for (String filename : TrackGiven.keySet()) {
            givenBID = TrackGiven.get(filename);
            File file = new File(filename);

            if (TrackSplit.containsKey(filename) && TrackCurrent.containsKey(filename)) {
                splitBID = TrackSplit.get(filename);
                currentBID = TrackCurrent.get(filename);
                if (splitBID.equals(currentBID) && !splitBID.equals(givenBID)){
                    stage.add(filename, Bolb.fromfile(givenBID).getContent());
                    stage.save();
                    checkCommitFile(givenCID, filename);
                }

            } else if (!TrackSplit.containsKey(filename) && !TrackCurrent.containsKey(filename)) {
                stage.add(filename,Bolb.fromfile(givenBID).getContent());
                checkCommitFile(givenCID, filename);

            } else if (TrackCurrent.containsKey(filename)) {
                currentBID = TrackCurrent.get(filename);
                if (!currentBID.equals(givenBID)) {
                    ifConflict = true;
                    String conflictContent = getConflictContent(currentBID, givenBID);
                    writeContents(file, conflictContent);
                    byte[] content = readContents(file);
                    stage.add(filename, content);
                    stage.save();

                }

            } else if (TrackSplit.containsKey(filename) && !TrackCurrent.containsKey(filename)) {
                splitBID = TrackSplit.get(filename);
                if (!splitBID.equals(givenBID)) {
                    ifConflict = true;
                    String conflictContent = getConflictContent(null, givenBID);
                    writeContents(file, conflictContent);
                    byte[] content = readContents(file);
                    stage.add(filename, content);
                    stage.save();

                }

            }

        }

        for (String filename : TrackCurrent.keySet()) {
            currentBID = TrackCurrent.get(filename);
            File file = new File(filename);
            if (TrackSplit.containsKey(filename) && !TrackGiven.containsKey(filename)) {
                splitBID = TrackSplit.get(filename);
                if (!splitBID.equals(currentBID)) {
                    ifConflict = true;
                    String conflictContent = getConflictContent(currentBID, null);
                    writeContents(file, conflictContent);
                    byte[] content = readContents(file);
                    stage.add(filename, content);
                    stage.save();

                }
            }

        }

        for (String filename : TrackSplit.keySet()) {
            splitBID = TrackSplit.get(filename);
            if (TrackCurrent.containsKey(filename) && !TrackGiven.containsKey(filename)) {
                currentBID = TrackCurrent.get(filename);
                if (currentBID.equals(splitBID)) {
                    stage.remove(filename);
                    stage.save();
                    join(CWD, filename).delete();
                }
            }
        }


        String message = "Merged " + branchName + " into " + currentCID + ".";
        setCommit(message, givenCID);

        if (ifConflict) {
            System.out.println("Encountered a merge conflict.");
        }

    }

    private static String getConflictContent(String currentBId, String targetBId) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<<<<<<< HEAD").append("\n");
        if (currentBId != null) {
            Bolb currentBlob = Bolb.fromfile(currentBId);
            contentBuilder.append(currentBlob.getContentAsString());
        }
        contentBuilder.append("=======").append("\n");
        if (targetBId != null) {
            Bolb targetBlob = Bolb.fromfile(targetBId);
            contentBuilder.append(targetBlob.getContentAsString());
        }
        contentBuilder.append(">>>>>>>");
        return contentBuilder.toString();
    }

    private static void ifAncestor(String branchName, String splitID, String currentID, String givenID) {
        if (splitID.equals(givenID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (splitID.equals(currentID)) {
            checkOutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
    }

    private static Commit getSplitPoint(String CID1, String CID2) {
        if (CID1.equals(CID2)) {
            return Commit.fromFile(CID1);
        } else if (CID1 == null) {
            return getSplitPoint(getHeadID(), getParentString(CID2));
        } else {
            return getSplitPoint(getParentString(CID1), CID2);
        }
    }

    private static String getParentString(String CID) {
        return Commit.fromFile(CID).getParent();
    }

    private static void checkMerge(String branchName) {
        ifStageClear();
        ifBranchExists(branchName);
        ifMergeItself(branchName);
        String CID = readContentsAsString(join(BRANCH, branchName));
        Map<String, String> Track1 = Commit.fromFile(getHeadID()).getTrack();
        Map<String, String> Track2 = Commit.fromFile(CID).getTrack();
        Track1.putAll(Track2);
        ifOverwrite(Track2);
    }

    private static void ifStageClear() {
        Stage stage = readObject(STAGE,Stage.class);
        if (!stage.ifClear()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
    }

    private static void ifBranchExists(String branchName) {
        if (!join(BRANCH, branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
    }

    private static void ifMergeItself(String branchName) {
        if (getCurrentBranchHead().getName().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }


}
