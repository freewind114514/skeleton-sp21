package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<SLList<Integer>> N = new AList<>();
        N.addLast(do_addLast(1000));
        N.addLast(do_addLast(2000));
        N.addLast(do_addLast(4000));
        N.addLast(do_addLast(8000));
        N.addLast(do_addLast(16000));
        N.addLast(do_addLast(32000));
        N.addLast(do_addLast(64000));
        N.addLast(do_addLast(128000));
        int i = 0;
        AList<Integer> ops = new AList<>();
        do{
            ops.addLast(10000);
            i++;
        }while (i < N.size());
        AList<Integer> ns = new AList<>();
        ns.addLast(1000);
        ns.addLast(2000);
        ns.addLast(4000);
        ns.addLast(8000);
        ns.addLast(16000);
        ns.addLast(32000);
        ns.addLast(64000);
        ns.addLast(128000);
        printTimingTable(ns, times(N), ops);
    }

    /** do addLast t times and return a SLList*/
    private static SLList<Integer> do_addLast(int t){
        int i = 0;
        SLList<Integer> N = new SLList<>();
        do{
            N.addLast(1);
            i++;
        }while (i < t);
        return N;
    }

    private static void do_getLast(SLList<Integer> N, int t){
        int i = 0;
        do{
            N.getLast();
            i++;
        }while (i < t);
    }



    private static AList<Double> times(AList<SLList<Integer>> N){
        AList<Double> t = new AList<>();
        for (int i = 0; i < N.size(); i++){
            Stopwatch sw = new Stopwatch();
            do_getLast(N.get(i),10000);
            double timeInSeconds = sw.elapsedTime();
            t.addLast(timeInSeconds);
        }
        return t;
    }

}
