package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items = (T[]) new Object[8];
    private int size;
    private int nextfirst;
    private int nextlast;

    public ArrayDeque() {
        size = 0;
        nextfirst = 3;
        nextlast = 4;
    }

    public Iterator<T> iterator() {
        return new ArrayIterator();
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

    private void addsize(int cap) {
        T[] r = (T[]) new Object[cap + items.length];
        System.arraycopy(items, 0, r, 0, nextlast);
        System.arraycopy(items, nextlast, r, nextlast + cap, size - nextlast);
        nextfirst = nextlast + cap - 1;
        items = r;
    }

    public void addFirst(T i) {
        if (size == items.length) {
            addsize(size);
        }
        items[nextfirst] = i;
        size++;
        nextfirst--;
        if (nextfirst < 0) {
            nextfirst += items.length;
        }
    }

    public void addLast(T i) {
        if (size == items.length) {
            addsize(size);
        }
        items[nextlast] = i;
        size++;
        nextlast++;
        if (nextlast >= items.length) {
            nextlast -= items.length;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = nextfirst + 1; i < items.length; i++) {
            System.out.print(items[i] + " ");
        }
        for (int i = 0; i < nextlast; i++) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        size--;
        if (nextfirst == items.length - 1) {
            nextfirst = 0;
        } else {
            nextfirst++;
        }
        T r = items[nextfirst];
        items[nextfirst] = null;
        Shrinksize();
        return r;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        size--;
        if (nextlast == 0) {
            nextlast = items.length - 1;
        } else {
            nextlast--;
        }
        T r = items[nextlast];
        items[nextlast] = null;
        Shrinksize();
        return r;
    }

    private void Shrinksize() {
        boolean r = (size * 4) < items.length;
        if (items.length >= 16 && r) {
            int cab = items.length / 2;
            T[] t = (T[]) new Object[cab];
            if (nextlast < nextfirst) {
                System.arraycopy(items, 0, t, 0, nextlast);
                System.arraycopy(items, nextfirst + 1, t, nextfirst + 1 - cab, size - nextlast);
                nextfirst -= cab;
            } else {
                System.arraycopy(items, nextfirst + 1, t, 1, size);
                nextfirst = 0;
                nextlast = size + 1;
            }
            items = t;
        }
    }

    public T get(int index) {
        index += nextfirst + 1;
        if (index >= items.length) {
            index -= items.length;
        }
        return items[index];
    }

    private class ArrayIterator implements Iterator<T> {
        private int wizPos;

        ArrayIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

}
