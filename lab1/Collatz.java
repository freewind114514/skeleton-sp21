/**
 * Class that prints the Collatz sequence starting from a given number.
 *
 * @author YOUR NAME HERE
 */
public class Collatz {
    /**
     * Calculates the next number in the Collatz sequence for a given input number.
     * The Collatz sequence is defined as follows:
     * - If n is even, the next number is n / 2.
     * - If n is odd, the next number is 3n + 1.
     */
    public static int nextNumber(int n) {
        if (n % 2 == 0) {
            return n / 2;
        } else {
            return 3 * n + 1;
        }
    }

    public static void main(String[] args) {
        int n = 5; // Starting number for the Collatz sequence
        // Print the Collatz sequence starting from n
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}

