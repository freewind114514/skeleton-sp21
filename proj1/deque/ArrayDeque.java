package deque;

public class ArrayDeque<Any> {
    private Any[] items;
    private int size;
    private int nextfirst;
    private int nextlast;

    public ArrayDeque(){
        items=(Any[]) new Object[8];
        size=0;
        nextfirst=3;
        nextlast=4;
    }

    public ArrayDeque(ArrayDeque other){
        items=(Any[]) new Object[other.items.length];
        System.arraycopy(other.items, 0, items, 0, other.items.length);
        size= other.size;
        nextfirst= other.nextfirst;
        nextlast= other.nextlast;
    }

    private void resize(int cap){
        Any[] r=(Any[]) new Object[cap+size];
        System.arraycopy(items,0, r,0, nextlast);
        System.arraycopy(items,nextlast, r,nextlast+cap, size-nextlast);
        nextfirst+=cap;
        nextlast++;
        items=r;
    }

    public void addFirst(Any i){
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

    public void addLast(Any i){
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

    public Any removeFirst(){
        if (size==0){
            return null;
        }
        size--;
        if(nextfirst== items.length-1){
            nextfirst= 0;
        }else {
            nextfirst++;
        }
        Any r=items[nextfirst];
        items[nextfirst]=null;
        return r;
    }

    public Any removeLast(){
        if (size==0){
            return null;
        }
        size--;
        if(nextlast== 0){
            nextlast= items.length-1;
        }else {
            nextlast--;
        }
        Any r=items[nextlast];
        items[nextlast]=null;
        return r;
    }

    public Any get(int index){
        index+=nextfirst+1;
        if(index>= items.length){
            index-= items.length;
        }
        return items[index];
    }

}
