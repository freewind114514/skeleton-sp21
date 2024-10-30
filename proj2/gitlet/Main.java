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
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        switch(args[0]) {
            case "init":
                checkArgsNumber(args, 1);
                init();
                break;

            case "add":
                checkGitlet();
                checkArgsNumber(args, 2);
                add(args[1]);
                break;

            case "rm":
                checkGitlet();
                checkArgsNumber(args, 2);
                rm(args[1]);
                break;

            case "commit":
                checkGitlet();
                checkArgsNumber(args, 2);
                commit(args[1]);
                break;

            case "log":
                checkGitlet();
                checkArgsNumber(args, 1);
                log();
                break;

            case "global-log":
                checkGitlet();
                checkArgsNumber(args, 1);
                globalLog();
                break;

            case "find":
                checkGitlet();
                checkArgsNumber(args, 2);
                find(args[1]);
                break;

            case "status":
                checkGitlet();
                checkArgsNumber(args, 1);
                status();
                break;

            case "checkout":
                checkGitlet();
                checkoutHelper(args);
                break;

            case "branch":
                checkGitlet();
                checkArgsNumber(args, 2);
                branch(args[1]);
                break;

            case "rm-branch":
                checkGitlet();
                checkArgsNumber(args, 2);
                rmBranch(args[1]);
                break;

            case "reset":
                checkGitlet();
                checkArgsNumber(args, 2);
                reset(args[1]);
                break;

            case "merge":
                checkGitlet();
                checkArgsNumber(args, 2);
                merge(args[1]);
                break;

            default:
                System.out.println("No command with that name exists.");
                System.exit(0);

        }

    }

    private static void checkArgsNumber(String[] args, int number) {
        if (args.length != number) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void checkoutHelper(String[] args){
        if(args.length == 3 && args[1].equals("--")){
            checkFile(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkCommitFile(args[1], args[3]);
        } else {
            checkArgsNumber(args, 2);
            checkOutBranch(args[1]);
        }

    }

}
