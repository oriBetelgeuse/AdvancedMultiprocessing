public class QuickSort {

    private int[] arr;

    public QuickSort(int[] arr) {
        this.arr = arr;
    }

    public void sort() {
        sort(0, arr.length - 1);
    }

    private void sort(int left, int right) {
        if (right > left) {
            int sepIndex = partition(left, right);
            sort(left, sepIndex - 1);
            sort(sepIndex + 1, right);
        }
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
