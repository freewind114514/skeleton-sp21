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
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("No arguments provided.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                try {
                    init();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "add":
                add(args[1]);
                // TODO: handle the `add [filename]` command
                break;

            case "remove":
                rm(args[1]);
                break;

            case "commit":
                commit(args[1]);
                break;

            case "log":
                log();
                break;

            case "global-log":
                globalLog();
                break;

            case "find":
                find(args[1]);
                break;

            case "status":
                status();
                break;

            case "checkout":
                if(args[1].equals("--")){
                    try {
                        checkFile(args[2]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (args[2].equals("--")) {
                    try {
                        checkCommitFile(args[1], args[3]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    checkBranch(args[1]);
                }

        }

    }
}
