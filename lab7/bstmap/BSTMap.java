package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private int size;
    private bstNode root;

    public BSTMap() {
        size = 0;
    }

    private class bstNode {
        private final K key;
        private V value;
        private int deep;
        private bstNode parent;
        private bstNode left;
        private bstNode right;

        bstNode(K k, V v) {
            key = k;
            value = v;
        }
    }

    /** find closest bstNode */
    private bstNode find(K target, bstNode node, int deep) {
        if (node.key.compareTo(target) == 0) {
            node.deep = deep;
            return node;
        }
        if (node.key.compareTo(target) > 0) {
            if (node.left == null) {
                node.deep = deep;
                return node;
            }
            return find(target, node.left, deep + 1);
        }
        if (node.right == null) {
            node.deep = deep;
            return node;
        }
        return find(target, node.right, deep + 1);
    }

    private void setRoot() {
        if (root.left != null) {
            bstNode middle = goRight(root.left);
            middle.right = root.right;
            if (middle.parent != root) {
                middle.parent.right = middle.left;
                middle.left = root.left;
            }
            root = middle;
        } else {
            bstNode middle = goLeft(root.right);
            middle.left = root;
            root = middle;
        }
    }

    private bstNode goRight(bstNode start) {
        if (start.right == null) {
            return start;
        }
        return goRight(start.right);
    }

    private bstNode goLeft(bstNode start) {
        if (start.left == null) {
            return start;
        }
        return goRight(start.left);
    }

    @Override
    public void clear() {
        size = 0;
        root = null;

    }

    @Override
    public boolean containsKey(K key) {
        bstNode closestNode = find(key, root, 0);
        if (closestNode.key.compareTo(key) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        bstNode closestNode = find(key, root, 0);
        if (closestNode.key.compareTo(key) == 0) {
            return closestNode.value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new bstNode(key, value);
            size += 1;
            return;
        }
        bstNode closestNode = find(key, root, 0);
        int flag = closestNode.key.compareTo(key);
        if (flag == 0) {
            closestNode.value = value;
        } else if (flag > 0) {
            closestNode.left = new bstNode(key, value);
            size += 1;
        } else {
            closestNode.right = new bstNode(key, value);
            size += 1;
        }

    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        this.printHelper(root);
    }

    private void printHelper(bstNode T) {
        if (T == null || T.key == null) {
            return;
        } else {
            printHelper(T.left);
            System.out.print(T.key.toString() + " ");
            printHelper(T.right);
        }
    }
}
