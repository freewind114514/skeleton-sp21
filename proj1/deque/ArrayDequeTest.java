package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void AddGetTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        a.addLast(2);
        a.addLast(3);
        a.addLast(4);
        a.addLast(5);
        a.addLast(6);
        a.addLast(7);
        a.addLast(8);
        a.addLast(9);
        a.addLast(10);
        a.addLast(11);
        a.addLast(12);
        a.addLast(13);
        int correct = 12;
        int anw = a.get(11);
        assertEquals("worong", correct, anw);
    }

    @Test
    public void IterTest(){
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        a.addLast(2);
        a.addLast(3);
        a.addLast(4);
        a.addLast(5);
        a.addLast(6);
        a.addLast(7);
        a.addLast(8);
        a.addLast(9);
        a.addLast(10);
        a.addLast(11);
        a.addLast(12);
        a.addLast(13);
        ArrayDeque<Integer> b = new ArrayDeque(a);
        b.removeLast();
        b.addLast(114514);
        assertFalse("?", a.equals(b));
        b.removeLast();
        b.addLast(13);
        assertTrue("?", a.equals(b));
    }

}
