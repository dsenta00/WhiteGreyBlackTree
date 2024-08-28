package com.wgbtree.tree.wgb.handler;

import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.model.node.white.White;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteHandler {

    public static <K extends Comparable<K>, T> int depth(White<K, T> node) {
        if (isNull(node)) {
            return 0;
        }

        return Arrays.stream(node.getGreys())
                .map(GreyHandler::depth)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    public static <T, K extends Comparable<K>>
    void getBetweenAsc(List<Set<T>> list, White<K, T> white, K from, K to) {
        if (isNull(white)) {
            return;
        }

        var entries = white.getEntries();

        if (to.compareTo(entries.firstEntry().getKey()) < 0) {
            return;
        }

        if (from.compareTo(entries.lastEntry().getKey()) <= 0) {
            int index = entries.searchClosest(from);

            for (int i = index; i < entries.size(); i++) {
                list.add(entries.get(i).getValue());
            }
        }

        if (to.compareTo(entries.lastEntry().getKey()) > 0) {
            var minHeapTree = new MinHeapTree<K, T>();
            for (var grey : white.getGreys()) {
                if (grey != null) {
                    GreyHandler.getBetweenAsc(minHeapTree, grey, from, to);
                }
            }

            list.addAll(minHeapTree.popAll());
        }
    }

    public static <T, K extends Comparable<K>>
    void getBetweenAsc(MinHeapTree<K, T> minHeapTree, White<K, T> white, K from, K to) {
        if (isNull(white)) {
            return;
        }

        var entries = white.getEntries();

        if (to.compareTo(entries.firstEntry().getKey()) < 0) {
            return;
        }

        if (from.compareTo(entries.lastEntry().getKey()) <= 0) {
            int index = entries.searchClosest(to);

            for (int i = index; i < entries.size(); i++) {
                minHeapTree.push(entries.get(i));
            }
        }

        if (to.compareTo(entries.lastEntry().getKey()) > 0) {
            for (var grey : white.getGreys()) {
                if (grey != null) {
                    GreyHandler.getBetweenAsc(minHeapTree, grey, from, to);
                }
            }
        }
    }
}