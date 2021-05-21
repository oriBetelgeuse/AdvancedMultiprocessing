package test;

import bst.ConcurrentPartialExternalBST;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBST {

    public static boolean check(List<Integer> arr, Set<Integer> set) {
        for (int i = 0; i < arr.size() - 1; i++) {
            if (arr.get(i) >= arr.get(i + 1)) {
                System.out.println(arr.get(i) + "  " + i);
                return false;
            }
        }
        return set.containsAll(arr);
    }

    public static void main(String[] args) {
        ConcurrentPartialExternalBST tree = new ConcurrentPartialExternalBST();
        AtomicInteger counter = new AtomicInteger(0);
        Set<Integer> synset = Collections.synchronizedSet(new HashSet<Integer>());
        List<Integer> arr = new ArrayList<>();
        TestingThread task = new TestingThread(tree, 50, counter, synset);
        int threads_num = 1;

        Thread[] threads = new Thread[threads_num];
        for (int i = 0; i < threads_num; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }

        for (int i = 0; i < threads_num; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        tree.makeArray(arr);

        System.out.println(counter.get() / 5);
        System.out.println(check(arr, synset));
    }
}
