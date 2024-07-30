package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private IntNode sentinel = new IntNode(null, null, null);
    private int size;

    public LinkedListDeque() {
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    /**
     * public LinkedListDeque(LinkedListDeque other){
     * sentinel.prev=sentinel;
     * sentinel.next=sentinel;
     * size=0;
     * for(int i = 0; i< other.size();i++){
     * addLast((T) other.get(i));
     * }
     * }
     */

    private class IntNode {
        private IntNode prev;
        private T item;
        private IntNode next;

        IntNode(T i, IntNode p, IntNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    public Iterator<T> iterator() {
        return new LinkIterator();
    }

    private class LinkIterator implements Iterator<T> {
        private IntNode p;

        LinkIterator() {
            p = sentinel.next;
        }

        public boolean hasNext() {
            return p != sentinel;
        }

        public T next() {
            T returnItem = p.item;
            p = p.next;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Deque)) {
            return false;
        }
        Deque<T> o = (Deque<T>) other;
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!(get(i).equals(o.get(i)))) {
                return false;
            }
        }
        return true;
    }

    public void addFirst(T i) {
        IntNode newone = new IntNode(i, sentinel, sentinel.next);
        sentinel.next.prev = newone;
        sentinel.next = newone;
        size += 1;
    }

    public void addLast(T i) {
        IntNode newone = new IntNode(i, sentinel.prev, sentinel);
        sentinel.prev.next = newone;
        sentinel.prev = newone;
        size += 1;
    }


    public int size() {
        return size;
    }

    public void printDeque() {
        IntNode p = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T r = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return r;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T r = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return r;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        IntNode p = sentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        IntNode p = sentinel.next;
        return gethelper(index, p);
    }

    private T gethelper(int i, IntNode p) {
        if (i == 0) {
            return p.item;
        } else {
            return gethelper(i - 1, p.next);
        }
    }

}
