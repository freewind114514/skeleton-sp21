package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> ops = new AList<>();
        ops.addLast(1000);
        ops.addLast(2000);
        ops.addLast(4000);
        ops.addLast(8000);
        ops.addLast(16000);
        ops.addLast(32000);
        ops.addLast(64000);
        ops.addLast(128000);
        printTimingTable(ops, times(ops), ops);
    }

    /** do addLast t times and return the size N of the data structure*/
    private static void do_addLast(int t){
        int i = 0;
        AList<Integer> ops = new AList<>();
        do{
            ops.addLast(1);
            i++;
        }while (i < t);
    }

    /** return the second column
     *  (the times required to complete all operations)*/
    private static AList<Double> times(AList<Integer> ops){
        AList<Double> t = new AList<>();
        for (int i = 0; i < ops.size(); i++){
            Stopwatch sw = new Stopwatch();
            do_addLast(ops.get(i));
            double timeInSeconds = sw.elapsedTime();
            t.addLast(timeInSeconds);
        }
        return t;
    }

    /** return first column
     *  (size of the data structure)

    private static AList<Integer> Ns(AList<Integer> ops){
        AList<Integer> n = new AList<>();
        for (int i = 0; i < ops.size(); i++){
            n.addLast(do_addLast(ops.get(i)));
        }
        return n;
    }
     */

}
