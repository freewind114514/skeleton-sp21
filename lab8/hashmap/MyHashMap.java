package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private final double loadFactor;
    private Set<K> keySet = new HashSet<>();
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(16);
        loadFactor = 0.75;
        size = 0;
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        loadFactor = 0.75;
        size = 0;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        loadFactor = maxLoad;
        size = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    private int getIndex(Object o) {
        int hashCode = o.hashCode();
        return Math.abs(hashCode % buckets.length);
    }

    @Override
    public void clear() {
        buckets = createTable(16);
        keySet = new HashSet<>();
        size = 0;
    }

    private Node find(K key) {
        int index = getIndex(key);
        if (buckets[index] == null) {
            return null;
        }
        for (Node n : buckets[index]) {
            if (n.key.equals(key)) {
                return n;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    @Override
    public V get(K key) {
        Node n = find(key);
        if (n == null) {
            return null;
        }
        return n.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        Node n = find(key);
        if (n != null) {
            n.value = value;
        } else {
            int index = getIndex(key);
            if (buckets[index] == null) {
                buckets[index] = createBucket();
            }
            size += 1;
            keySet.add(key);
            buckets[index].add(createNode(key, value));
        }
        if (loadRatio() > loadFactor) {
            resize(2 * buckets.length);
        }
    }

    private double loadRatio() {
        return (double) size / buckets.length;
    }

    private void resize(int newSize) {
        Collection<Node>[] newBuckets = createTable(newSize);
        for (Collection<Node> c: buckets) {
            if (c == null) {
                continue;
            }
            for (Node n: c) {
                int index = getIndex(n.key);
                if (newBuckets[index] == null) {
                    newBuckets[index] = createBucket();
                }
                newBuckets[index].add(createNode(n.key, n.value));
            }
        }
        buckets = newBuckets;
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }

}
