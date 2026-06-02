package com.wgbtree.tree.wgb.handler;

import com.wgbtree.tree.heap.HeapTree;
import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EntryHandler {

    public static <K extends Comparable<K>, T>
    void mergeAsc(List<Entry<K, Set<T>>> list, List<Iterator<Entry<K, Set<T>>>> results) {
        merge(list, results, new MinHeapTree<>());
    }

    public static <K extends Comparable<K>, T>
    void mergeDesc(List<Entry<K, Set<T>>> list, List<Iterator<Entry<K, Set<T>>>> results) {
        merge(list, results, new MaxHeapTree<>());
    }

    private static <K extends Comparable<K>, T>
    void merge(List<Entry<K, Set<T>>> list, List<Iterator<Entry<K, Set<T>>>> results, HeapTree<K, T> heapTree) {
        boolean run;

        do {
            run = false;
            for (var res : results) {
                if (!res.hasNext()) {
                    continue;
                }
                heapTree.push(res.next());
                run = true;
            }

            if (run) {
                list.add(heapTree.pop());
            }
        } while (run);

        list.addAll(heapTree.popAll());
        results.clear();
    }
}
