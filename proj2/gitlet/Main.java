package gitlet;

import java.io.IOException;

import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                init();
                break;
            case "add":
                add(args[1]);
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case "remove":
                rm(args[1]);
                break;

            case "commit":
                commit(args[1]);
        }
    }
}
