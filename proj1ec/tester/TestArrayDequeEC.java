package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void Test_all(){
        StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> correct = new ArrayDequeSolution<>();

        for (int i = 0; i < 10; i += 1) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                sad1.addLast(i);
                correct.addLast(i);
            } else {
                sad1.addFirst(i);
                correct.addLast(i);
            }
        }

        Integer a = correct.get(2);
        Integer b = sad1.get(2);
        assertEquals("random addLast addFirst get(2)",a,b);
    }
}
