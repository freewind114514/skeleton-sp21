package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;

/**
 * save contents of commit as BID
 * different version of Filename.
 * <p>
 * never change?
 */
public class Bolb implements Serializable {
    private String BID;
    private String filename;
    private byte[] content;

    public Bolb(String filename, byte[] c) {
        filename = filename;
        content = c;
        BID = sha1(content);
    }

    public void saveObject() {
        File object = join(Repository.getBOLBS(), BID);
        try {
            object.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(object, this);
    }

    public static Bolb fromfile(String bid) {
        File object = join(Repository.getBOLBS(), bid);
        return readObject(object, Bolb.class);
    }

    private String getFilename() {
        return filename;
    }

    public String getBID() {
        return BID;
    }

    public byte[] getContent() {
        return content;
    }


}
