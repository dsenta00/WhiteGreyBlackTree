package com.wgbtree.tree.test;

import com.wgbtree.tree.wgb.utils.InsertionSort;

import java.util.*;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;

public class InsertionSortTest extends Test {

    private static final int REPEAT_NUM = 1_000_000;

    public static void main(String[] args) {
        testSortAscFromBack();
        testSortAscFromBackPerformance();

        testSortDescFromBack();
        testSortDescFromBackPerformance();

        testSortAscFromFront();
        testSortAscFromFrontPerformance();

        testSortDescFromFront();
        testSortDescFromFrontPerformance();
    }

    static void testSortAscFromBack() {
        var array = createArray(1, 2, 3, 4, 0);

        InsertionSort.sortAscFromBack(array, 5);

        assertEquals(0, array[0].getKey());
        assertEquals(1, array[1].getKey());
        assertEquals(2, array[2].getKey());
        assertEquals(3, array[3].getKey());
        assertEquals(4, array[4].getKey());
    }

    static void testSortAscFromBackPerformance() {
        long timeBinarySort = 0L;
        int timeBinarySortPercentage;
        long timeInsertionSort = 0L;
        int timeInsertionSortPercentage;

        for (int i = 0; i < REPEAT_NUM; i++) {
            // Warmup
            var array1 = createArray(1, 2, 3, 4, 0);
            InsertionSort.sortAscFromBack(array1, 5);
            var array2 = createArray(1, 2, 3, 4, 0);
            Arrays.sort(array2, 0, 5, comparingByKey());
        }

        for (int i = 0; i < REPEAT_NUM; i++) {
            var array1 = createArray(1, 2, 3, 4, 0);
            timeInsertionSort += measureTime(() -> InsertionSort.sortAscFromBack(array1, 5));
            var array2 = createArray(1, 2, 3, 4, 0);
            timeBinarySort += measureTime(() -> Arrays.sort(array2, 0, 5, comparingByKey()));
        }

        System.out.println("sortAscFromBack ->");
        timeBinarySortPercentage = (int)(timeBinarySort * 100.0 / (timeBinarySort + timeInsertionSort));
        System.out.println("  binary sort: " + timeBinarySort + " (" + timeBinarySortPercentage + ")");
        timeInsertionSortPercentage = (int)(timeInsertionSort * 100.0 / (timeBinarySort + timeInsertionSort));
        System.out.println("  insert sort: " + timeInsertionSort+ " (" + timeInsertionSortPercentage + ")");
        System.out.println();
    }

    static void testSortDescFromBack() {
        var array = createArray(3, 2, 1, 0, 4);

        InsertionSort.sortDescFromBack(array, 5);

        assertEquals(4, array[0].getKey());
        assertEquals(3, array[1].getKey());
        assertEquals(2, array[2].getKey());
        assertEquals(1, array[3].getKey());
        assertEquals(0, array[4].getKey());
    }

    static void testSortDescFromBackPerformance() {
        long timeBinarySort = 0L;
        int timeBinarySortPercentage;
        long timeInsertionSort = 0L;
        int timeInsertionSortPercentage;

        for (int i = 0; i < REPEAT_NUM; i++) {
            // Warmup
            var array1 = createArray(3, 2, 1, 0, 4);
            InsertionSort.sortDescFromBack(array1, 5);
            var array2 = createArray(3, 2, 1, 0, 4);
            Arrays.sort(array2, 0, 5, comparingByKey(reverseOrder()));
        }

        for (int i = 0; i < REPEAT_NUM; i++) {
            var array1 = createArray(3, 2, 1, 0, 4);
            timeInsertionSort += measureTime(() -> InsertionSort.sortDescFromBack(array1, 5));
            var array2 = createArray(3, 2, 1, 0, 4);
            timeBinarySort += measureTime(() -> Arrays.sort(array2, 0, 5, comparingByKey(reverseOrder())));
        }

        System.out.println("sortDescFromBack ->");
        timeBinarySortPercentage = (int)(timeBinarySort * 100.0 / (timeBinarySort + timeInsertionSort));
        System.out.println("  binary sort: " + timeBinarySort + " (" + timeBinarySortPercentage + ")");
        timeInsertionSortPercentage = (int)(timeInsertionSort * 100.0 / (timeBinarySort + timeInsertionSort));
        System.out.println("  insert sort: " + timeInsertionSort+ " (" + timeInsertionSortPercentage + ")");
        System.out.println();
    }

    static void testSortAscFromFront() {
        var array = createArray(4, 0, 1, 2, 3);

        InsertionSort.sortAscFromFront(array, 5);

        assertEquals(0, array[0].getKey());
        assertEquals(1, array[1].getKey());
        assertEquals(2, array[2].getKey());
        assertEquals(3, array[3].getKey());
        assertEquals(4, array[4].getKey());
    }

    static void testSortAscFromFrontPerformance() {
        long timeBinarySort = 0L;
        int timeBinarySortPercentage;
        long timeInsertionSort = 0L;
        int timeInsertionSortPercentage;

        for (int i = 0; i < REPEAT_NUM; i++) {
            // Warmup
            var array1 = createArray(4, 0, 1, 2, 3);
            InsertionSort.sortAscFromFront(array1, 5);
            var array2 = createArray(4, 0, 1, 2, 3);
            Arrays.sort(array2, 0, 5, comparingByKey());
        }

        for (int i = 0; i < REPEAT_NUM; i++) {
            var array1 = createArray(4, 0, 1, 2, 3);
            timeInsertionSort += measureTime(() -> InsertionSort.sortAscFromFront(array1, 5));
            var array2 = createArray(4, 0, 1, 2, 3);
            timeBinarySort += measureTime(() -> Arrays.sort(array2, 0, 5, comparingByKey()));
        }

        System.out.println("sortAscFromFront ->");
        timeBinarySortPercentage = (int)(timeBinarySort * 100.0 / (timeBinarySort + timeInsertionSort));
        System.out.println("  binary sort: " + timeBinarySort + " (" + timeBinarySortPercentage + ")");
        timeInsertionSortPercentage = (int)(timeInsertionSort * 100.0 / (timeBinarySort + timeInsertionSort));
        System.out.println("  insert sort: " + timeInsertionSort+ " (" + timeInsertionSortPercentage + ")");
        System.out.println();
    }

    static void testSortDescFromFront() {
        var array = createArray(0, 4, 3, 2, 1);

        InsertionSort.sortDescFromFront(array, 5);

        assertEquals(4, array[0].getKey());
        assertEquals(3, array[1].getKey());
        assertEquals(2, array[2].getKey());
        assertEquals(1, array[3].getKey());
        assertEquals(0, array[4].getKey());
    }

    static void testSortDescFromFrontPerformance() {
        long timeBinarySort = 0L;
        int timeBinarySortPercentage;
        long timeInsertionSort = 0L;
        int timeInsertionSortPercentage;

        for (int i = 0; i < REPEAT_NUM; i++) {
            // Warmup
            var array1 = createArray(0, 4, 3, 2, 1);
            InsertionSort.sortDescFromFront(array1, 5);
            var array2 = createArray(0, 4, 3, 2, 1);
            Arrays.sort(array2, 0, 5, comparingByKey(reverseOrder()));
        }

        for (int i = 0; i < REPEAT_NUM; i++) {
            var array1 = createArray(0, 4, 3, 2, 1);
            timeInsertionSort += measureTime(() -> InsertionSort.sortDescFromFront(array1, 5));
            var array2 = createArray(0, 4, 3, 2, 1);
            timeBinarySort += measureTime(() -> Arrays.sort(array2, 0, 5, comparingByKey(reverseOrder())));
        }

        System.out.println("sortDescFromFront ->");
        timeBinarySortPercentage = (int)(timeBinarySort * 100.0 / (timeBinarySort + timeInsertionSort));
        System.out.println("  binary sort: " + timeBinarySort + " (" + timeBinarySortPercentage + ")");
        timeInsertionSortPercentage = (int)(timeInsertionSort * 100.0 / (timeBinarySort + timeInsertionSort));
        System.out.println("  insert sort: " + timeInsertionSort+ " (" + timeInsertionSortPercentage + ")");
        System.out.println();
    }

    @SuppressWarnings("unchecked")
    private static Map.Entry<Integer, Set<Integer>>[] createArray(int i0, int i1, int i2, int i3, int i4) {
        var array = (Map.Entry<Integer, Set<Integer>>[]) new Map.Entry<?, ?>[5];
        array[0] = new AbstractMap.SimpleEntry<>(i0, Set.of(i0));
        array[1] = new AbstractMap.SimpleEntry<>(i1, Set.of(i1));
        array[2] = new AbstractMap.SimpleEntry<>(i2, Set.of(i2));
        array[3] = new AbstractMap.SimpleEntry<>(i3, Set.of(i3));
        array[4] = new AbstractMap.SimpleEntry<>(i4, Set.of(i4));
        return array;
    }
}
