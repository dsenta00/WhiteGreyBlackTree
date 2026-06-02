package com.wgbtree.tree.test;

import com.wgbtree.tree.heap.HeapTree;
import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.heap_opt.Heap;
import com.wgbtree.tree.heap_opt.MaxHeap;
import com.wgbtree.tree.heap_opt.MinHeap;

import java.util.*;
import java.util.Map.Entry;

public class HeapTreeTest extends Test {

    public static final int SIZE = 1000000;

    public static void main(String[] args) {
        testMinHeapTree(List.of(1, 2, 3, 4, 5));
        testMinHeapTree(List.of(5, 4, 3, 2, 1));
        testMinHeapTree(List.of(5, 4, 3, 2, 1, 0));
        testMinHeapTree(List.of(5, 4, 3, 2, 1, 0, 6));
        testMinHeapTree(List.of(5, 4, 3, 6, 7, 0, 2, 1));

        testMinHeap(List.of(1, 2, 3, 4, 5));
        testMinHeap(List.of(5, 4, 3, 2, 1));
        testMinHeap(List.of(5, 4, 3, 2, 1, 0));
        testMinHeap(List.of(5, 4, 3, 2, 1, 0, 6));
        testMinHeap(List.of(5, 4, 3, 6, 7, 0, 2, 1));

        testMaxHeapTree(List.of(5, 4, 3, 2, 1));
        testMaxHeapTree(List.of(1, 2, 3, 4, 5));
        testMaxHeapTree(List.of(0, 1, 2, 3, 4, 5));
        testMaxHeapTree(List.of(6, 0, 1, 2, 3, 4, 5));
        testMaxHeapTree(List.of(1, 2, 0, 3, 4, 5, 6, 7));

        testMaxHeap(List.of(5, 4, 3, 2, 1));
        testMaxHeap(List.of(1, 2, 3, 4, 5));
        testMaxHeap(List.of(0, 1, 2, 3, 4, 5));
        testMaxHeap(List.of(6, 0, 1, 2, 3, 4, 5));
        testMaxHeap(List.of(1, 2, 0, 3, 4, 5, 6, 7));
        testPerformance(new MinHeap<>(), new MinHeapTree<>());
        testPerformance(new MaxHeap<>(), new MaxHeapTree<>());
    }

    private static void testPerformance(Heap<String, Object> heap, HeapTree<String, String> heapTree) {
        long timeInsertTree = 0L;
        long timeInsertOpt = 0L;

        for (int i = 0; i < SIZE; i++) {
            var uuid = UUID.randomUUID().toString();
            timeInsertTree += measureTime(() -> heapTree.push(Map.entry(uuid, Set.of(uuid))));
            timeInsertOpt += measureTime(() -> heap.offer(Map.entry(uuid, Set.of(uuid))));
        }

        long timePopTree;
        long timePopOpt;

        timePopTree = measureTime(heapTree::popAll);
        timePopOpt = measureTime(heap::popAll);

        System.out.println("Tree: ");
        System.out.println("  insertAll ... " + timeInsertTree);
        System.out.println("  popAll ...... " + timePopTree);
        System.out.println("  total ....... " + (timeInsertTree + timePopTree));
        System.out.println("Opt: ");
        System.out.println("  insertAll ... " + timeInsertOpt);
        System.out.println("  popAll ...... " + timePopOpt);
        System.out.println("  total ....... " + (timeInsertOpt + timePopOpt));
        System.out.println();
    }

    private static void testMinHeapTree(List<Integer> list) {
        var heap = new MinHeapTree<Integer, Integer>();

        for (var i : list) {
            heap.push(Map.entry(i, Set.of(i)));
        }

        var result = heap.popAll().stream().map(Entry::getValue).map(Set::iterator).map(Iterator::next).toList();
        var sortedResult = result.stream().sorted().toList();

        for (int i = 0; i < sortedResult.size(); i++) {
            assertEquals(sortedResult.get(i), result.get(i));
        }
    }

    private static void testMinHeap(List<Integer> list) {
        var heap = new MinHeap<Integer, Integer>();

        for (var i : list) {
            heap.offer(Map.entry(i, Set.of(i)));
        }

        var result = heap.popAll().stream().map(Set::iterator).map(Iterator::next).toList();
        var sortedResult = result.stream().sorted().toList();

        for (int i = 0; i < sortedResult.size(); i++) {
            assertEquals(sortedResult.get(i), result.get(i));
        }
    }

    private static void testMaxHeapTree(List<Integer> list) {
        var heap = new MaxHeapTree<Integer, Integer>();

        for (var i : list) {
            heap.push(Map.entry(i, Set.of(i)));
        }

        var result = heap.popAll().stream().map(Entry::getValue).map(Set::iterator).map(Iterator::next).toList();
        var sortedResult = result.stream().sorted(Comparator.reverseOrder()).toList();

        for (int i = 0; i < sortedResult.size(); i++) {
            assertEquals(sortedResult.get(i), result.get(i));
        }
    }

    private static void testMaxHeap(List<Integer> list) {
        var heap = new MaxHeap<Integer, Integer>();

        for (var i : list) {
            heap.offer(Map.entry(i, Set.of(i)));
        }

        var result = heap.popAll().stream().map(Set::iterator).map(Iterator::next).toList();
        var sortedResult = result.stream().sorted(Comparator.reverseOrder()).toList();

        for (int i = 0; i < sortedResult.size(); i++) {
            assertEquals(sortedResult.get(i), result.get(i));
        }
    }
}
