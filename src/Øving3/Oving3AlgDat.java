package Øving3;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Oving3AlgDat {
    public static void main(String[] args) {
        int n = 1_000_000;
        int[] arr = new int[10];
        int sum = 0;

        long[] tidsmålinger = new long[10];
        for (int i = 0; i < 10; i++) {
            //Med random array
            arr = createRandomArray(n);
            sum = Arrays.stream(arr).sum();
            Date dualPivotStartTime = new Date();
            DualPivotQuicksort.dualPivotQuickSort(arr, 0, n - 1);
            Date dualPivotEndTime = new Date();
            tidsmålinger[i] = dualPivotEndTime.getTime() - dualPivotStartTime.getTime();
        }
        int sumtid = 0;
        for (int i = 0; i < tidsmålinger.length; i++) {
            sumtid += tidsmålinger[i];
        }
        int gjennomsnittstid = sumtid / tidsmålinger.length;

        System.out.println("Dual pivot quicksort random liste:");
        System.out.println("summetest: " + (sum == Arrays.stream(arr).sum()));
        System.out.println("rekkefølgetesten: " + (rekkefolgetesten(arr)));
        System.out.println("Tidsforskjell dual pivot quicksort: " + gjennomsnittstid);
        System.out.println();

        tidsmålinger = new long[10];
        for (int i = 0; i < 10; i++) {
            arr = createRandomArray(n);
            sum = Arrays.stream(arr).sum();
            Date quicksortStartTime = new Date();
            Quicksort.quicksort(arr, 0, n - 1);
            Date quicksortEndTime = new Date();
            tidsmålinger[i] = quicksortEndTime.getTime() - quicksortStartTime.getTime();
        }

        sumtid = 0;
        for (int i = 0; i < tidsmålinger.length; i++) {
            sumtid += tidsmålinger[i];
        }
        gjennomsnittstid = sumtid / tidsmålinger.length;

        System.out.println("Øving3.Quicksort random liste:");
        System.out.println("summetest: " + (sum == Arrays.stream(arr).sum()));
        System.out.println("rekkefølgetesten: " + (rekkefolgetesten(arr)));
        System.out.println("Tidsforskjell quicksort: " + gjennomsnittstid);
        System.out.println();

        //Med duplikater
        arr = createArrayWithDuplicates(n);
        sum = Arrays.stream(arr).sum();
        Date dualPivotStartTime = new Date();
        DualPivotQuicksort.dualPivotQuickSort(arr, 0, n - 1);
        Date dualPivotEndTime = new Date();

        System.out.println("Dual pivot quicksort liste med duplikater:");
        System.out.println("summetest: " + (sum == Arrays.stream(arr).sum()));
        System.out.println("rekkefølgetesten: " + (rekkefolgetesten(arr)));
        System.out.println("Tidsforskjell dual pivot quicksort: " + (dualPivotEndTime.getTime() - dualPivotStartTime.getTime()));
        System.out.println();

        arr = createArrayWithDuplicates(n);
        sum = Arrays.stream(arr).sum();
        Date quicksortStartTime = new Date();
        Quicksort.quicksort(arr, 0, n - 1);
        Date quicksortEndTime = new Date();

        System.out.println("Øving3.Quicksort liste med duplikater:");
        System.out.println("summetest: " + (sum == Arrays.stream(arr).sum()));
        System.out.println("rekkefølgetesten: " + (rekkefolgetesten(arr)));
        System.out.println("Tidsforskjell quicksort: " + (quicksortEndTime.getTime() - quicksortStartTime.getTime()));
        System.out.println();

        //Med sortert liste
        int low = 0;
        int high = n - 1;
        sum = Arrays.stream(arr).sum();
        Quicksort.bytt(arr, 0, low + (high - low) / 3);
        Quicksort.bytt(arr, high, high - (high - low) / 3);
        dualPivotStartTime = new Date();
        DualPivotQuicksort.dualPivotQuickSort(arr, 0, n - 1);
        dualPivotEndTime = new Date();

        System.out.println("Dual pivot quicksort med sortet liste:");
        System.out.println("summetest: " + (sum == Arrays.stream(arr).sum()));
        System.out.println("rekkefølgetesten: " + (rekkefolgetesten(arr)));
        System.out.println("Tidsforskjell dual pivot quicksort: " + (dualPivotEndTime.getTime() - dualPivotStartTime.getTime()));
        System.out.println();

        quicksortStartTime = new Date();
        Quicksort.quicksort(arr, 0, n - 1);
        quicksortEndTime = new Date();

        System.out.println("Øving3.Quicksort med sortet liste:");
        System.out.println("summetest: " + (sum == Arrays.stream(arr).sum()));
        System.out.println("rekkefølgetesten: " + (rekkefolgetesten(arr)));
        System.out.println("Tidsforskjell quicksort: " + (quicksortEndTime.getTime() - quicksortStartTime.getTime()));
        System.out.println();
    }

    static boolean rekkefolgetesten(int[] arr) {
        boolean suksess = true;
        int forrigeTall = 0;

        for (int i : arr) {
            if (i < forrigeTall)
                suksess = false;
            forrigeTall = i;
        }

        return suksess;
    }

    static int[] createRandomArray(int n) {
        int[] newArray = new int[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            newArray[i] = random.nextInt(0, 100_001);
        }
        return newArray;
    }

    static int[] createArrayWithDuplicates(int n) {
        int[] newArray = new int[n];
        Random random = new Random();
        int a = random.nextInt(0, 100_001);
        int b = random.nextInt(0, 100_001);
        while (b == a) {
            b = random.nextInt(0, 100_001);
        }
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) newArray[i] = a;
            else newArray[i] = b;
        }
        return newArray;
    }
}

class Quicksort {
    static void bytt(int[] t, int i, int j) {
        int k = t[j];
        t[j] = t[i];
        t[i] = k;
    }

    static int median3sort(int[] t, int v, int h) {
        int m = (v + h) / 2;
        if (t[v] > t[m]) bytt(t, v, m);
        if (t[m] > t[h]) {
            bytt(t, m, h);
            if (t[v] > t[m]) bytt(t, v, m);
        }
        return m;
    }

    static int splitt(int[] t, int v, int h) {
        int iv, ih;
        int m = median3sort(t, v, h);
        int dv = t[m];
        bytt(t, m, h - 1);
        for (iv = v, ih = h - 1; ; ) {
            while (t[++iv] < dv) ;
            while (t[--ih] > dv) ;
            if (iv >= ih) break;
            bytt(t, iv, ih);
        }
        bytt(t, iv, h - 1);
        return iv;
    }

    static void quicksort(int[] t, int v, int h) {
        if (h - v > 2) {
            int delepos = splitt(t, v, h);
            quicksort(t, v, delepos - 1);
            quicksort(t, delepos + 1, h);
        } else median3sort(t, v, h);
    }
}


class DualPivotQuicksort {
    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    static void dualPivotQuickSort(int[] arr, int low, int high) {
        if (low < high) {

            // piv[] stores left pivot and right pivot.
            // piv[0] means left pivot and
            // piv[1] means right pivot
            int[] piv;
            piv = partition(arr, low, high);

            dualPivotQuickSort(arr, low, piv[0] - 1);
            dualPivotQuickSort(arr, piv[0] + 1, piv[1] - 1);
            dualPivotQuickSort(arr, piv[1] + 1, high);
        }
    }

    static int[] partition(int[] arr, int low, int high) {
        if (arr[low] > arr[high])
            swap(arr, low, high);

        // p is the left pivot, and q
        // is the right pivot.
        int j = low + 1;
        int g = high - 1, k = low + 1,
                p = arr[low], q = arr[high];

        while (k <= g) {

            // If elements are less than the left pivot
            if (arr[k] < p) {
                swap(arr, k, j);
                j++;
            }

            // If elements are greater than or equal
            // to the right pivot
            else if (arr[k] >= q) {
                while (arr[g] > q && k < g)
                    g--;

                swap(arr, k, g);
                g--;

                if (arr[k] < p) {
                    swap(arr, k, j);
                    j++;
                }
            }
            k++;
        }
        j--;
        g++;

        // Bring pivots to their appropriate positions.
        swap(arr, low, j);
        swap(arr, high, g);

        // Returning the indices of the pivots
        // because we cannot return two elements
        // from a function, we do that using an array.
        return new int[]{j, g};
    }
}
