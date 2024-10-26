package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import static gitlet.Utils.*;
import static gitlet.Repository.*;

/**
 * save contents of commit as BID
 * different version of Filename.
 *
 * never change?
 */
public class Bolb implements Serializable{
    private String BID;
    private String Filename;
    private byte[] content;

    public Bolb(String filename, byte[] c){
        Filename = filename;
        content = c;
        BID = sha1(content);
    }

    public void saveObject(){
        File object = join(BOLBS, BID);
        try {
            object.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(object, this);
    }

    public static Bolb fromfile(String bid){
        File object = join(BOLBS, bid);
        return readObject(object, Bolb.class);
    }

    private String getFilename(){
        return Filename;
    }

    public String getBID(){
        return BID;
    }

    private byte[] getContent() {
        return content;
    }
}
