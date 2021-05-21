package bst;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentPartialExternalBST implements BinarySearchTree {
    private Node root = null;
    private final ReentrantLock rootLock = new ReentrantLock();

    private static class Node {
        public Node left = null;
        public Node right = null;
        public boolean routing = false;
        public boolean deleted = false;
        public int value;

        public final ReentrantLock lock = new ReentrantLock();

        public Node(int value) {
            this.value = value;
        }
    }

    private static class Window {
        public Node gprev, prev, curr;

        public Window(Node gprev, Node prev, Node curr) {
            update(gprev, prev, curr);
        }

        public void update(Node gprev, Node prev, Node curr) {
            this.gprev = gprev;
            this.prev = prev;
            this.curr = curr;
        }
    }

    private Window search(int value) {
        Window window = new Window(null, null, root);
        while (window.curr != null && window.curr.value != value) {
            if (window.curr.value < value) window.update(window.prev, window.curr, window.curr.right);
            else window.update(window.prev, window.curr, window.curr.left);
        }

        return window;
    }

    public boolean contains(int data) {
        Window window = search(data);
        Node curr = window.curr;

        if (curr != null) {
            curr.lock.lock();
            try {
                return !window.curr.routing;
            } finally {
                curr.lock.unlock();
            }
        }

        return false;
    }

    public boolean insert(int data) {
        if (root == null) {
            rootLock.lock();
            try {
                if (root == null) {
                    root = new Node(data);
                    return true;
                }
            } finally {
                rootLock.unlock();
            }
        }

        Window window = search(data);
        Node curr = window.curr, prev = window.prev;

        if (curr != null) return tryToMakeReal(curr);
        else return insertLeaf(prev, data);
    }

    private boolean tryToMakeReal(Node curr) {
        curr.lock.lock();
        try {
            if (curr.routing && !curr.deleted) {
                curr.routing = false;
                return true;
            } else {
                return false;
            }
        } finally {
            curr.lock.unlock();
        }
    }

    private boolean insertLeaf(Node prev, int data) {
        prev.lock.lock();
        try {
            if (prev.deleted) return false;

            if (data > prev.value && prev.right == null) prev.right = new Node(data);
            else if (data < prev.value && prev.left == null) prev.left = new Node(data);
            else return false;
        } finally {
            prev.lock.unlock();
        }

        return true;
    }

    public boolean remove(int data) {
        Window window = search(data);
        Node curr = window.curr, prev = window.prev, gprev = window.gprev;

        if (curr == null) return false;
        curr.lock.lock();
        try {
            if (curr.deleted || curr.routing) return false;

            if (curr.right != null && curr.left != null) {
                curr.routing = true;
                return true;
            }

            if (curr.right == null && curr.left == null) return removeLeaf(gprev, prev, curr);
            else return removeWithOneChild(prev, curr);
        } finally {
            curr.lock.unlock();
        }

    }

    private boolean removeLeaf(Node gprev, Node prev, Node curr) {
        if (prev == null) {
            rootLock.lock();
            try {
                if (root == null || root.deleted || curr != root) return false;
                root.deleted = true;
                root = null;
                return true;
            } finally {
                rootLock.unlock();
            }
        }

        prev.lock.lock();
        try {
            if (prev.deleted) return false;
            if (prev.left != curr && prev.right != curr) return false;

            if (!prev.routing) {
                curr.deleted = true;
                if (prev.left == curr) prev.left = null;
                else prev.right = null;
            } else {
                if (gprev == null) {
                    rootLock.lock();
                    try {
                        if (root == null || root.deleted || root != prev) return false;
                        curr.deleted = true;
                        if (prev.left == curr) root = prev.right;
                        else root = prev.left;
                        return true;
                    } finally {
                        rootLock.unlock();
                    }
                }

                gprev.lock.lock();
                try {
                    if (gprev.deleted) return false;
                    if (gprev.left != prev && gprev.right != prev) return false;

                    curr.deleted = true;
                    prev.deleted = true;
                    Node futureChild;

                    if (prev.left == curr) futureChild = prev.right;
                    else futureChild = prev.left;

                    if (gprev.left == prev) gprev.left = futureChild;
                    else gprev.right = futureChild;
                } finally {
                    gprev.lock.unlock();
                }
            }
        } finally {
            prev.lock.unlock();
        }

        return true;
    }

    private boolean removeWithOneChild(Node prev, Node curr) {
        if (prev == null) {
            rootLock.lock();
            try {
                if (root == null || root.deleted || curr != root) return false;
                curr.deleted = true;
                if (curr.left == null) root = curr.right;
                else root = curr.left;
                return true;
            } finally {
                rootLock.unlock();
            }
        }

        prev.lock.lock();
        try {
            if (prev.deleted) return false;
            if (prev.left != curr && prev.right != curr) return false;

            curr.deleted = true;
            Node futureChild;

            if (curr.left == null) futureChild = curr.right;
            else futureChild = curr.left;

            if (prev.left == curr) prev.left = futureChild;
            else prev.right = futureChild;
        } finally {
            prev.lock.unlock();
        }

        return true;
    }

    private static void makeArray(Node node, List<Integer> container) {
        if (node != null) {
            makeArray(node.left, container);
            if (!node.routing) container.add(node.value);
            makeArray(node.right, container);
        }
    }

    public void makeArray(List<Integer> container) {
        makeArray(root, container);
    }
}
