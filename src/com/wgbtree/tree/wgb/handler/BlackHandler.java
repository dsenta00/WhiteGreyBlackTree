package com.wgbtree.tree.wgb.handler;

import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.model.node.black.Black;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackHandler {

    public static <K extends Comparable<K>, T> int depth(Black<K, T> node) {
        if (isNull(node)) {
            return 0;
        }

        return Arrays.stream(node.getGreys())
                .map(GreyHandler::depth)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    public static <T, K extends Comparable<K>>
    void getBetweenAsc(List<Set<T>> list, Black<K, T> black, K from, K to) {
        if (isNull(black)) {
            return;
        }

        var entries = black.getEntries();

        if (from.compareTo(entries.firstEntry().getKey()) > 0) {
            return;
        }

        if (from.compareTo(entries.lastEntry().getKey()) < 0) {
            var minHeapTree = new MinHeapTree<K, T>();
            for (var grey : black.getGreys()) {
                if (grey != null) {
                    GreyHandler.getBetweenAsc(minHeapTree, grey, from, to);
                }
            }

            list.addAll(minHeapTree.popAll());
        }

        if (to.compareTo(entries.lastEntry().getKey()) >= 0) {
            int index = entries.searchClosest(to);

            for (int i = black.getEntries().size() - 1; i >= index; i--) {
                list.add(black.getEntries().get(i).getValue());
            }
        }
    }

    public static <T, K extends Comparable<K>>
    void getBetweenAsc(MinHeapTree<K, T> minHeapTree, Black<K, T> black, K from, K to) {
        if (isNull(black)) {
            return;
        }

        var entries = black.getEntries();

        if (from.compareTo(entries.firstEntry().getKey()) > 0) {
            return;
        }

        if (to.compareTo(entries.lastEntry().getKey()) >= 0) {
            int index = entries.searchClosest(to);

            for (int i = entries.size() - 1; i >= index; i--) {
                minHeapTree.push(black.getEntries().get(i));
            }
        }

        if (from.compareTo(entries.lastEntry().getKey()) < 0) {
            for (var grey : black.getGreys()) {
                if (grey != null) {
                    GreyHandler.getBetweenAsc(minHeapTree, grey, from, to);
                }
            }
        }
    }
}