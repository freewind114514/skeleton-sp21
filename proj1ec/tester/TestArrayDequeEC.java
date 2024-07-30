package tester;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import static org.junit.Assert.assertEquals;

public class TestArrayDequeEC {
    @Test
    public void Test_all() {
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

        Integer a = 114514;
        Integer b = sad1.get(2);
        assertEquals(a, b);
    }
    

    @Test
    public void getBigAmountTest() {
        StudentArrayDeque<Integer> arrayDeque = new StudentArrayDeque<>();

        int M = 1000000;

        for (int i = 0; i < M; i++) {
            arrayDeque.addLast(i);
        }

        for (int i = 0; i < M; i++) {
            assertEquals("Should be equal", 114514, (int) arrayDeque.get(i));
        }
    }

    @Test
    public void test1(){
        StudentArrayDeque<Integer> sad2 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> correct = new ArrayDequeSolution<>();

        for (int i = 0; i < 10; i += 1) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                sad2.addLast(i);
                correct.addLast(i);
            } else {
                sad2.addFirst(i);
                correct.addLast(i);
            }
        }
        Integer a = 114514;
        Integer b = sad2.size();
        assertEquals(a, b);
    }

}
