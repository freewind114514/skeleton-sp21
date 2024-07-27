package deque;

public class LinkedListDeque<AnyClass> {

    private IntNode sentinel=new IntNode(null,null,null);
    private int size;

    public LinkedListDeque(){
        sentinel.prev=sentinel;
        sentinel.next=sentinel;
        size=0;
    }

    public LinkedListDeque(AnyClass i){
        IntNode first=new IntNode(i,sentinel,sentinel);
        sentinel.next=first;
        sentinel.prev=first;
        size=1;
    }

    public LinkedListDeque(LinkedListDeque other){
        sentinel.prev=sentinel;
        sentinel.next=sentinel;
        size=0;
        for(int i = 0; i< other.size();i++){
            addLast((AnyClass) other.get(i));
        }
    }

    public class IntNode {
        public IntNode prev;
        public AnyClass item;
        public IntNode next;

        public IntNode(AnyClass i,IntNode p,IntNode n){
            item=i;
            prev=p;
            next=n;
        }
    }

    public void addFirst(AnyClass i){
        IntNode newone=new IntNode(i,sentinel,sentinel.next);
        sentinel.next.prev=newone;
        sentinel.next=newone;
        size+=1;
    }

    public void addLast(AnyClass i){
        IntNode newone=new IntNode(i,sentinel.prev,sentinel);
        sentinel.prev.next=newone;
        sentinel.prev=newone;
        size+=1;
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
        IntNode p=sentinel.next;
        for(int i=0;i<size;i++) {
            System.out.print(p.item+" ");
            p=p.next;
        }
        System.out.println();
    }

    public AnyClass removeFirst(){
        if (size==0){
            return null;
        }
        AnyClass r=sentinel.next.item;
        sentinel.next.next.prev=sentinel;
        sentinel.next=sentinel.next.next;
        size-=1;
        return r;
    }

    public AnyClass removeLast(){
        if (size==0){
            return null;
        }
        AnyClass r=sentinel.prev.item;
        sentinel.prev.prev.next=sentinel;
        sentinel.prev=sentinel.prev.prev;
        size-=1;
        return r;
    }

    public AnyClass get(int index){
        if (index>=size){
            return null;
        }
        IntNode p=sentinel.next;
        for(int i=0;i<index;i++) {
            p=p.next;
        }
        return p.item;
    }

    public AnyClass getRecursive(int index){
        if (index>=size){
            return null;
        }
        IntNode p=sentinel.next;
        return gethelper(index,p);
    }

    private AnyClass gethelper(int i,IntNode p){
        if (i==0){
            return p.item;
        }else {
            return gethelper(i-1,p.next);
        }
    }

}
