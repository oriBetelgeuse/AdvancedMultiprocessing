package test;

import bst.BinarySearchTree;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class TestingThread implements Runnable {
    private final BinarySearchTree tree;
    private final AtomicInteger counter;
    private final int x;

    Set<Integer> synset;

    public TestingThread(BinarySearchTree tree, int x, AtomicInteger counter, Set<Integer> synset) {
        this.tree = tree;
        this.counter = counter;
        this.x = x;
        this.synset = synset;
    }

    @Override
    public void run() {
        Random rand = new Random();

        long start = System.currentTimeMillis(), time = 0;
        int value, p;
        while (time < 5000) {
            value = rand.nextInt(100000);
            p = rand.nextInt(100);

            if (p < x) {
                tree.insert(value);
                synset.add(value);
            } else {
                if (p < 2 * x) {
                    tree.remove(value);
                    synset.remove(value);
                } else tree.contains(value);

            }

            counter.incrementAndGet();
            time = System.currentTimeMillis() - start;
        }
    }
}
