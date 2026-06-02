package com.wgbtree.tree.wgb.operations.get.range;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.model.node.black.Black;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackGetterRange {

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> getMin(Black<K, T> black) {
        if (isNull(black) || black.getEntries().isEmpty()) {
            return null;
        }

        var greys = black.getGreys();
        if (greys == null) {
            return black.getEntries().lastEntry();
        }

        return GreyGetterRange.getMin(greys[0]);
    }

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> getMax(Black<K, T> black) {
        if (isNull(black) || black.getEntries().isEmpty()) {
            return null;
        }

        return black.getEntries().firstEntry();
    }

    public static <T, K extends Comparable<K>>
    void getAllAsc(List<Entry<K, Set<T>>> list, Black<K, T> black) {
        if (isNull(black)) {
            return;
        }

        var greys = black.getGreys();
        GreyGetterRange.getAllAsc(list, greys[0]);

        for (int i = black.getEntries().size() - 1; i >= 0; i--) {
            list.add(black.getEntries().get(i));
        }
    }

    public static <T, K extends Comparable<K>>
    void getAllDesc(List<Entry<K, Set<T>>> list, Black<K, T> black) {
        if (isNull(black)) {
            return;
        }

        list.addAll(black.getEntries());

        var greys = black.getGreys();
        GreyGetterRange.getAllDesc(list, greys[0]);
    }

    public static <T, K extends Comparable<K>>
    void getBetweenAsc(List<Entry<K, Set<T>>> list, Black<K, T> black, K from, K to) {
        if (isNull(black)) {
            return;
        }

        var entries = black.getEntries();

        if (from.compareTo(entries.firstEntry().getKey()) > 0) {
            return;
        }

        if (from.compareTo(entries.lastEntry().getKey()) < 0) {
            var greys = black.getGreys();
            GreyGetterRange.getBetweenAsc(list, greys[0], from, to);
        }

        if (to.compareTo(entries.lastEntry().getKey()) >= 0) {
            int index = entries.searchClosest(to);

            for (int i = black.getEntries().size() - 1; i >= index; i--) {
                list.add(black.getEntries().get(i));
            }
        }
    }
}
