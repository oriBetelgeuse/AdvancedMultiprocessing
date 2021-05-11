import java.util.concurrent.RecursiveTask;


public class QuickSortAction extends RecursiveTask<Integer> {
    private int[] arr;
    private int left, right;

    public QuickSortAction(int[] arr) {
        this.arr = arr;
        this.left = 0;
        this.right = arr.length - 1;
    }

    public QuickSortAction(int[] arr, int left, int right) {
        this.arr = arr;
        this.left = left;
        this.right = right;
    }

    @Override
    protected Integer compute() {
        if (right > left) {
            int sepIndex = partition(left, right);

            QuickSortAction leftSorter = new QuickSortAction(arr, left, sepIndex - 1);
            QuickSortAction rightSorter = new QuickSortAction(arr, sepIndex + 1, right);
            leftSorter.fork();
            rightSorter.compute();
            leftSorter.join();
        }

        return null;
    }

    private int partition(int left, int right) {
        int pivot = arr[right];
        int sepIndex = left;

        for (int j = left; j < right; j++)
            if (arr[j] <= pivot)
                swap(sepIndex++, j);
        swap(sepIndex, right);
        
        return sepIndex;
    }

    private void swap(int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
