package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * does at a high level.
 * make Track(Map<Filename, BID>) for Commit?
 *
 * @author freewind
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    private static File GITLET_DIR = join(CWD, ".gitlet");
    private static File COMMIT = join(GITLET_DIR, "commits");
    private static File BOLBS = join(GITLET_DIR, "bolbs");
    private static File STAGE = join(GITLET_DIR, "stage");
    private static File BRANCH = join(GITLET_DIR, "branches");
    private static File saveHead = join(GITLET_DIR, "head");
    private static File REMOTE = join(GITLET_DIR, "remotes");
    private static String HEAD;
    private static File currentBranchHead;

    private static String getHeadID() {
        return readContentsAsString(getCurrentBranchHead());
    }

    private static File getCurrentBranchHead() {
        return readObject(getSaveHead(), File.class);
    }

    public static void checkGitlet() {
        if (!getGIT().exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void gitletExists() {
        if (getGIT().exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
    }

    public static File getGIT() {
        return GITLET_DIR;
    }

    public static File getCOMMIT() {
        return COMMIT;
    }

    public static File getBOLBS() {
        return BOLBS;
    }

    public static File getSTAGE() {
        return STAGE;
    }

    public static File getBRANCH() {
        return BRANCH;
    }

    public static File getSaveHead() {
        return saveHead;
    }

    public static File getREMOTE() {
        return REMOTE;
    }

    public static void init() {
        gitletExists();
        GITLET_DIR.mkdir();
        BRANCH.mkdir();
        COMMIT.mkdir();
        BOLBS.mkdir();
        REMOTE.mkdir();
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

    public static void add(String filename) {
        File file = join(CWD, filename);
        Stage stage = readObject(getSTAGE(), Stage.class);
        Map<String, String> track = Commit.fromFile(getHeadID()).getTrack();
        if (file.exists()) {
            byte[] content = readContents(file);
            String id = sha1(content);
            stage.add(filename, content);
            stage.save();
            if (track.containsKey(filename) && track.get(filename).equals(id)) {
                stage.addStageRemove(filename);
                stage.save();
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    public static void rm(String filename) {
        Stage stage = readObject(getSTAGE(), Stage.class);
        File file = join(CWD, filename);
        HEAD = getHeadID();
        Map<String, String> track = Commit.fromFile(HEAD).getTrack();

        if (stage.addNotContains(filename) && !track.containsKey(filename)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        stage.getAddStage().remove(filename);
        if (track.containsKey(filename)) {
            stage.remove(filename);
            if (file.exists()) {
                file.delete();
            }
        }
        stage.save();
    }

    private static void setCommit(String m, String secondCID) {
        Stage stage = readObject(getSTAGE(), Stage.class);
        HEAD = getHeadID();
        currentBranchHead = getCurrentBranchHead();
        Commit c = Commit.fromFile(HEAD);
        if (stage.ifClear()) {
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

    public static void commit(String m) {
        setCommit(m, null);
    }

    public static void log() {
        HEAD = getHeadID();
        Commit c = Commit.fromFile(HEAD);
        while (c != null) {
            Commit.printLog(c);
            c = Commit.fromFile(c.getParent1());
        }
    }

    public static void globalLog() {
        List<String> commits = plainFilenamesIn(getCOMMIT());
        for (String commit : commits) {
            Commit.printLog(Commit.fromFile(commit));
        }
    }

    public static void find(String m) {
        List<String> commits = plainFilenamesIn(getCOMMIT());
        boolean notfound = true;
        for (String commit : commits) {
            Commit c = Commit.fromFile(commit);
            String commitMessage = c.getMessage();
            if (commitMessage.contains(m)) {
                System.out.println(c.getID());
                notfound = false;
            }
        }
        if (notfound) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        Stage stage = readObject(getSTAGE(), Stage.class);
        HEAD = getHeadID();
        currentBranchHead = getCurrentBranchHead();
        List<String> branches = plainFilenamesIn(getBRANCH());

        Collections.sort(branches);
        String currentBranchName = currentBranchHead.getName();

        System.out.println("=== Branches ===");
        for (String str : branches) {
            if (Objects.equals(str, currentBranchName)) {
                System.out.println("*" + currentBranchName);
            } else {
                System.out.println(str);
            }
        }
        System.out.println();
        stage.printStage();
        printModifiedStatus();
        printUntrackedStatus();
    }

    private static void printModifiedStatus() {
        Stage stage = readObject(getSTAGE(), Stage.class);
        HEAD = getHeadID();
        Commit c = Commit.fromFile(HEAD);
        Map<String, byte[]> addStage = stage.getAddStage();
        Map<String, String> track = c.getTrack();
        List<String> filenames = plainFilenamesIn(CWD);
        List<String> M = new ArrayList<>();

        for (String filename : track.keySet()) {
            if (filenames.contains(filename)) {
                String id = sha1(readContents(join(CWD, filename)));
                if (!track.get(filename).equals(id) && stage.addNotContains(filename)) {
                    M.add(filename + " " + "(modified)");
                }
            } else {
                if (!stage.ifRmStageContains(filename)) {
                    M.add(filename + " " + "(deleted)");
                }
            }
        }
        for (String filename : addStage.keySet()) {
            if (filenames.contains(filename)) {
                String id1 = sha1(readContents(join(CWD, filename)));
                String id2 = sha1(addStage.get(filename));
                if (!id1.equals(id2)) {
                    M.add(filename + " " + "(modified)");
                }
            } else {
                M.add(filename + " " + "(deleted)");
            }
        }
        Collections.sort(M);
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String str : M) {
            System.out.println(str);
        }
        System.out.println();
    }

    private static void printUntrackedStatus() {
        List<String> filenames = plainFilenamesIn(CWD);
        Stage stage = readObject(getSTAGE(), Stage.class);
        HEAD = getHeadID();
        Commit c = Commit.fromFile(HEAD);
        Map<String, String> track = c.getTrack();
        List<String> U = new ArrayList<>();
        for (String filename : filenames) {
            if (stage.addNotContains(filename) && !track.keySet().contains(filename)) {
                U.add(filename);
            }
            if (stage.ifRmStageContains(filename)) {
                U.add(filename);
            }
        }
        Collections.sort(U);
        System.out.println("=== Untracked Files ===");
        for (String str : U) {
            System.out.println(str);
        }
        System.out.println();
    }

    private static void check(String cid, String filename) {
        Commit c = Commit.fromFile(cid);
        Map<String, String> track = c.getTrack();
        if (track.containsKey(filename)) {
            Bolb b = readObject(join(getBOLBS(), track.get(filename)), Bolb.class);
            byte[] content = b.getContent();
            File file = join(CWD, filename);
            if (!file.exists()) {
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

    public static void checkCommitFile(String id, String filename) {
        String cid = shortIdCommit(id);
        ifCommitExists(cid);
        check(cid, filename);
    }

    private static void ifCommitExists(String cid) {
        if (cid == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File commit = join(getCOMMIT(), cid);
        if (!commit.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
    }

    private static String shortIdCommit(String shortId) {
        List<String> commits = plainFilenamesIn(getCOMMIT());
        int length = shortId.length();
        if (length < 6) {
            return null;
        }
        assert commits != null;
        for (String commitId : commits) {
            if (commitId.substring(0, length).equals(shortId)) {
                return commitId;
            }
        }
        return null;
    }

    public static void checkOutBranch(String branchName) {
        checkCheckOutBranch(branchName);
        String cid = readContentsAsString(join(getBRANCH(), branchName));
        List<String> filenames = plainFilenamesIn(CWD);
        Map<String, String> track = Commit.fromFile(cid).getTrack();
        deleteUntracked(track, filenames);
        checkAll(cid, track);
        clearSaveStage();
        writeObject(getSaveHead(), join(getBRANCH(), branchName));
    }

    private static void checkAll(String cid, Map<String, String> track) {
        for (String filename : track.keySet()) {
            checkCommitFile(cid, filename);
        }
    }

    private static void clearSaveStage() {
        Stage stage = readObject(getSTAGE(), Stage.class);
        stage.clear();
        stage.save();
    }

    private static void deleteUntracked(Map<String, String> track, List<String> filenames) {
        for (String name : filenames) {
            if (!track.containsKey(name)) {
                File file = join(CWD, name);
                file.delete();
            }
        }
    }

    private static void ifOverwrite(Map<String, String> track) {
        List<String> filenames = plainFilenamesIn(CWD);
        Map<String, String> currentTrack = Commit.fromFile(getHeadID()).getTrack();
        for (String name : filenames) {
            if (!currentTrack.containsKey(name) && track.containsKey(name)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private static void checkCheckOutBranch(String branchName) {
        File file = join(getBRANCH(), branchName);
        if (!file.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String givenCID = readContentsAsString(file);
        Map<String, String> givenTrack = Commit.fromFile(givenCID).getTrack();
        String currentBranchName = getCurrentBranchHead().getName();
        if (branchName.equals(currentBranchName)) {
            if (readContentsAsString(file).equals(getHeadID())) {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }
        } else {
            ifOverwrite(givenTrack);
        }

    }

    public static void branch(String name) {
        File newBranch = join(getBRANCH(), name);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        HEAD = getHeadID();
        setBranch(name, HEAD);
    }

    private static void setBranch(String name, String cid) {
        File file = join(BRANCH, name);
        writeContents(file, cid);
    }

    public static void rmBranch(String name) {
        String currentBranchName = getCurrentBranchHead().getName();
        File file = join(getBRANCH(), name);
        if (currentBranchName.equals(name)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        if (!file.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        file.delete();
    }

    public static void reset(String id) {
        String cid = shortIdCommit(id);
        ifCommitExists(cid);
        List<String> filenames = plainFilenamesIn(CWD);
        Map<String, String> track = Commit.fromFile(cid).getTrack();
        ifOverwrite(track);
        checkAll(cid, track);
        writeContents(getCurrentBranchHead(), cid);
        deleteUntracked(track, filenames);
        clearSaveStage();
    }

    private static boolean mergeHelper(String branchName) {
        String currentCID = getHeadID();
        String givenCID = readContentsAsString(join(getBRANCH(), branchName));
        Commit splitPoint = getSpiltPoint(branchName);
        ifAncestor(branchName, splitPoint.getID(), currentCID, givenCID);
        Map<String, String> trackSplit = splitPoint.getTrack();
        Map<String, String> trackCurrent = Commit.fromFile(currentCID).getTrack();
        Map<String, String> trackGiven = Commit.fromFile(givenCID).getTrack();
        Stage stage = readObject(getSTAGE(), Stage.class);
        String currentBID;
        String givenBID;
        String splitBID;
        boolean ifConflict = false;
        for (String filename : trackGiven.keySet()) {
            givenBID = trackGiven.get(filename);
            if (trackSplit.containsKey(filename) && trackCurrent.containsKey(filename)) {
                splitBID = trackSplit.get(filename);
                currentBID = trackCurrent.get(filename);
                if (splitBID.equals(currentBID) && !splitBID.equals(givenBID)) {
                    stage.pureAdd(filename, Bolb.fromfile(givenBID).getContent());
                    checkCommitFile(givenCID, filename);
                }

                if (!currentBID.equals(givenBID)) {
                    if (!splitBID.equals(currentBID) && !splitBID.equals(givenBID)) {
                        ifConflict = true;
                        writeConflictFile(filename, currentBID, givenBID);
                        stage.pureAdd(filename, readContents(join(CWD, filename)));
                    }
                }
            }

            if (!trackSplit.containsKey(filename) && !trackCurrent.containsKey(filename)) {
                stage.pureAdd(filename, Bolb.fromfile(givenBID).getContent());
                checkCommitFile(givenCID, filename);
            }

            if (!trackSplit.containsKey(filename) && trackCurrent.containsKey(filename)) {
                currentBID = trackCurrent.get(filename);
                if (!currentBID.equals(givenBID)) {
                    ifConflict = true;
                    writeConflictFile(filename, currentBID, givenBID);
                    stage.pureAdd(filename, readContents(join(CWD, filename)));
                }
            }

            if (trackSplit.containsKey(filename) && !trackCurrent.containsKey(filename)) {
                splitBID = trackSplit.get(filename);
                if (!splitBID.equals(givenBID)) {
                    ifConflict = true;
                    writeConflictFile(filename, null, givenBID);
                    stage.pureAdd(filename, readContents(join(CWD, filename)));
                }
            }
        }

        for (String filename : trackSplit.keySet()) {
            splitBID = trackSplit.get(filename);
            if (trackCurrent.containsKey(filename) && !trackGiven.containsKey(filename)) {
                currentBID = trackCurrent.get(filename);
                if (currentBID.equals(splitBID)) {
                    stage.remove(filename);
                    join(CWD, filename).delete();
                } else {
                    ifConflict = true;
                    writeConflictFile(filename, currentBID, null);
                    stage.pureAdd(filename, readContents(join(CWD, filename)));
                }
            }
        }
        stage.save();
        return ifConflict;
    }

    public static void merge(String branchName) {
        checkMerge(branchName);
        String currentCID = getHeadID();
        String currentBranchName = getCurrentBranchHead().getName();
        String givenCID = readContentsAsString(join(getBRANCH(), branchName));
        Commit splitPoint = getSpiltPoint(branchName);
        ifAncestor(branchName, splitPoint.getID(), currentCID, givenCID);
        boolean ifConflict = mergeHelper(branchName);

        if (ifConflict) {
            System.out.println("Encountered a merge conflict.");
        }
        String message = "Merged " + branchName + " into " + currentBranchName + ".";
        setCommit(message, givenCID);
    }

    private static void writeConflictFile(String filename, String currentBId, String targetBId) {
        File file = join(CWD, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        byte[] headContent;
        byte[] targetContent;
        String newLine = System.getProperty("line.separator");
        String head = "<<<<<<< HEAD" + newLine;
        if (currentBId != null) {
            Bolb currentBlob = Bolb.fromfile(currentBId);
            headContent = currentBlob.getContent();
        } else {
            headContent = new byte[0];
        }
        String separateLine = "=======" + newLine;
        if (targetBId != null) {
            Bolb targetBlob = Bolb.fromfile(targetBId);
            targetContent = targetBlob.getContent();
        } else {
            targetContent = new byte[0];
        }
        String end = ">>>>>>>" + newLine;
        writeContents(file, head, headContent,
                separateLine, targetContent, end);
    }

    private static void ifAncestor(String branchName, String splitID,
                                   String currentID, String givenID) {
        if (splitID.equals(givenID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (splitID.equals(currentID)) {
            checkOutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
    }

    private static Commit getSpiltPoint(String branchName) {
        String commitId = readContentsAsString(join(getBRANCH(), branchName));
        Commit commitA = Commit.fromFile(commitId);
        Commit commitB = Commit.fromFile(getHeadID());
        Map<String, Integer> routeA = getRouteToInit(commitA);
        Map<String, Integer> routeB = getRouteToInit(commitB);
        String spiltPointCommitId = "";
        int minValue = Integer.MAX_VALUE;
        for (String commit : routeA.keySet()) {
            if (routeB.containsKey(commit)) {
                if (routeB.get(commit) < minValue) {
                    spiltPointCommitId = commit;
                    minValue = routeB.get(commit);
                }
            }
        }
        return Commit.fromFile(spiltPointCommitId);
    }

    private static Map<String, Integer> getRouteToInit(Commit commit) {
        Map<String, Integer> route = new TreeMap<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(commit.getID());
        route.put(commit.getID(), 0);
        while (!queue.isEmpty()) {
            String commitId = queue.poll();
            Commit thisCommit = Commit.fromFile(commitId);
            for (String parentCommit : thisCommit.getParents()) {
                if (route.containsKey(parentCommit)) {
                    break;
                } else {
                    queue.add(parentCommit);
                    route.put(parentCommit, route.get(commitId) + 1);
                }
            }
        }
        return route;
    }

    private static void checkMerge(String branchName) {
        ifStageClear();
        ifBranchExists(branchName);
        ifMergeItself(branchName);
        String cid = readContentsAsString(join(getBRANCH(), branchName));
        Map<String, String> track1 = Commit.fromFile(getHeadID()).getTrack();
        Map<String, String> track2 = Commit.fromFile(cid).getTrack();
        track1.putAll(track2);
        ifOverwrite(track2);
    }

    private static void ifStageClear() {
        Stage stage = readObject(getSTAGE(), Stage.class);
        if (!stage.ifClear()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
    }

    private static void ifBranchExists(String branchName) {
        if (!join(getBRANCH(), branchName).exists()) {
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

    public static void addRemote(String remoteName, String userPath) {
        File remoteFile = join(REMOTE, remoteName);
        if (remoteFile.exists()) {
            System.out.println("A remote with that name already exists.");
            System.exit(0);
        }
        try {
            remoteFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String filepath = userPath.replace("/", File.separator);
        File file = new File(filepath);
        writeObject(remoteFile, file);
    }

    public static void rmRemote(String remoteName) {
        File remoteFile = join(REMOTE, remoteName);
        if (!remoteFile.exists()) {
            System.out.println("A remote with that name does not exist.");
            System.exit(0);
        }
        remoteFile.delete();
    }

    private static void notFoundRemote(String remoteName) {
        File remoteFile = join(getREMOTE(), remoteName);
        if (!remoteFile.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        File remoteGitlet = readObject(remoteFile, File.class);
        if (!remoteGitlet.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
    }

    private static void changeCWD(File file) {
        GITLET_DIR = file;
        COMMIT = join(file, "commits");
        BOLBS = join(file, "bolbs");
        STAGE = join(file, "stage");
        BRANCH = join(file, "branches");
        saveHead = join(file, "head");
        REMOTE = join(file, "remotes");
    }

    private static void copyCommit(String cid, File copyTO, File copyFrom) {
        changeCWD(copyFrom);
        Commit commit = Commit.fromFile(cid);
        Map<String, Bolb> bolbs = new TreeMap<>();
        for (String bid : commit.getTrack().values()) {
            Bolb content = Bolb.fromfile(bid);
            bolbs.put(bid, content);
        }
        changeCWD(copyTO);
        File commitFile = join(getCOMMIT(), cid);
        if (!commitFile.exists()) {
            commit.saveObject();
            copyBolbs(bolbs);
        }
    }

    private static void copyBolbs(Map<String, Bolb> bolbs) {
        for (String bid : bolbs.keySet()) {
            File file = join(getBOLBS(), bid);
            Bolb b = bolbs.get(bid);
            if (!file.exists()) {
                b.saveObject();
            }
        }
    }

    public static void fetch(String remoteName, String remoteBranchName) {
        notFoundRemote(remoteName);
        File remoteGitlet = readObject(join(REMOTE, remoteName), File.class);
        File remoteBranch = join(remoteGitlet, "branches", remoteBranchName);
        if (!remoteBranch.exists()) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }
        String remoteCID = readContentsAsString(remoteBranch);
        changeCWD(remoteGitlet);
        // work in remote .gitlet now
        Commit remoteCommit = Commit.fromFile(remoteCID);
        Map<String, Integer> branchHistory = getRouteToInit(remoteCommit);
        changeCWD(join(CWD, ".gitlet"));
        // work back CWD and copy all
        for (String cid : branchHistory.keySet()) {
            copyCommit(cid, join(CWD, ".gitlet"), remoteGitlet);
        }
        File file = join(BRANCH, remoteName);
        file.mkdir();
        String newBranch = remoteName + "/" + remoteBranchName;
        setBranch(newBranch, remoteCID);
    }

    public static void push(String remoteName, String remoteBranchName) {
        notFoundRemote(remoteName);
        File remoteGitlet = readObject(join(REMOTE, remoteName), File.class);
        File remoteBranch = join(remoteGitlet, "branches", remoteBranchName);
        if (!remoteBranch.exists()) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }
        String remoteCID = readContentsAsString(remoteBranch);
        Map<String, Integer> currentHistory = getRouteToInit(Commit.fromFile(getHeadID()));
        if (!currentHistory.containsKey(remoteCID)) {
            System.out.println("Please pull down remote changes before pushing.");
            System.exit(0);
        }
        for (String cid : currentHistory.keySet()) {
            copyCommit(cid, remoteGitlet, join(CWD, ".gitlet"));
        }
        // get CWD head and point remote head to target commit
        changeCWD(join(CWD, ".gitlet"));
        String headCWD = getHeadID();
        changeCWD(remoteGitlet);
        writeContents(getCurrentBranchHead(), headCWD);
    }

    public static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        String remoteBranch = remoteName + "/" + remoteBranchName;
        merge(remoteBranch);
    }

}
