package gitlet;

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
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                init();
            case "add":
                checkGitlet();
                validateNumArgs(args, 2);
                add(args[1]);
                break;

            case "remove":
                checkGitlet();
                validateNumArgs(args, 2);
                rm(args[1]);
                break;

            case "commit":
                checkGitlet();
                validateNumArgs(args, 2);
                commit(args[1]);
                break;

            case "log":
                checkGitlet();
                validateNumArgs(args, 1);
                log();
                break;

            case "global-log":
                checkGitlet();
                validateNumArgs(args, 1);
                globalLog();
                break;

            case "find":
                checkGitlet();
                validateNumArgs(args, 2);
                find(args[1]);
                break;

            case "status":
                checkGitlet();
                validateNumArgs(args, 1);
                status();
                break;

            case "checkout":
                checkGitlet();
                checkoutHelper(args);
                break;

            default:
                System.out.println("No command with that name exists.");
                System.exit(0);

        }

    }

    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void checkoutHelper(String[] args){
        if(args[1].equals("--") && args.length == 3){
            checkFile(args[2]);
        } else if (args[2].equals("--") && args.length == 4) {
            checkCommitFile(args[1], args[3]);
        } else if (args.length == 2) {
            checkBranch(args[1]);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

    }

}
