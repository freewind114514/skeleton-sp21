package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>{
    private T[] items;
    private int size;
    private int nextfirst;
    private int nextlast;

    public ArrayDeque(){
        items=(T[]) new Object[8];
        size=0;
        nextfirst=3;
        nextlast=4;
    }

    public Iterator<T> iterator(){
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<T> {
        private int wizPos;

        public ArrayIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = items[wizPos];
            wizPos += 1;
            return returnItem;
        }
    }

    public boolean equals(Object other){
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        ArrayDeque<T> o = (ArrayDeque<T>) other;
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!o.get(i).equals(this.get(i))){
                return false;
            }
        }
        return true;

    }

    /**public ArrayDeque(ArrayDeque other){
        items=(T[]) new Object[other.items.length];
        System.arraycopy(other.items, 0, items, 0, other.items.length);
        size= other.size;
        nextfirst= other.nextfirst;
        nextlast= other.nextlast;
    }*/

    private void resize(int cap){
        T[] r=(T[]) new Object[cap+ items.length];
        System.arraycopy(items,0, r,0, nextlast);
        System.arraycopy(items, nextfirst+1, r,nextfirst+1+cap, items.length-nextfirst-1);
        nextfirst+=cap;
        nextlast++;
        items=r;
    }

    public void addFirst(T i){
        if(size==items.length){
            resize(size);
        }
        items[nextfirst]= i;
        size++;
        nextfirst--;
        if(nextfirst<0){
            nextfirst+= items.length;
        }
    }

    public void addLast(T i){
        if(size==items.length){
            resize(size);
        }
        items[nextlast]= i;
        size++;
        nextlast++;
        if(nextlast>= items.length){
            nextlast-= items.length;
        }
    }

    public boolean isEmpty(){
        if(size==0){
            return true;
        }
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        for(int i=nextfirst+1;i< items.length;i++) {
            System.out.print(items[i] + " ");
        }
        for(int i=0;i<nextlast;i++){
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    public T removeFirst(){
        if (size==0){
            return null;
        }
        size--;
        if(nextfirst== items.length-1){
            nextfirst= 0;
        }else {
            nextfirst++;
        }
        T r=items[nextfirst];
        items[nextfirst]=null;
        return r;
    }

    public T removeLast(){
        if (size==0){
            return null;
        }
        size--;
        if(nextlast== 0){
            nextlast= items.length-1;
        }else {
            nextlast--;
        }
        T r=items[nextlast];
        items[nextlast]=null;
        return r;
    }

    public T get(int index){
        index+=nextfirst+1;
        if(index>= items.length){
            index-= items.length;
        }
        return items[index];
    }

}
