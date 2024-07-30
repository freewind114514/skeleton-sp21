package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void AddGetTest() {
        ArrayDeque<Integer> A = new ArrayDeque<>();
        A.addLast(0);
        A.addLast(1);
        A.addFirst(2);
        A.get(0);
        A.addLast(4);
        A.addFirst(5);
        A.addLast(6);
        A.addFirst(7);
        A.addFirst(8);
        A.addLast(9);
        A.removeLast();
        A.addFirst(11);
        A.addLast(12);
        A.addLast(13);
        A.addLast(14);
        A.addFirst(15);
        A.removeFirst();
        int correct = 4;
        int anw = A.get(7);
        Deque<Integer> B = new LinkedListDeque<>();
        assertEquals("worong", correct, anw);
        System.out.println(A.getClass());
        System.out.println(B.getClass());
    }

}
