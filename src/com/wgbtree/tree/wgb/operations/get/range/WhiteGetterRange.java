package com.wgbtree.tree.wgb.operations.get.range;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.model.node.white.White;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteGetterRange {

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> getMin(White<K, T> white) {
        if (isNull(white) || white.getEntries().isEmpty()) {
            return null;
        }

        return white.getEntries().firstEntry();
    }

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> getMax(White<K, T> white) {
        if (isNull(white) || white.getEntries().isEmpty()) {
            return null;
        }

        var greys = white.getGreys();
        if (greys == null) {
            return white.getEntries().lastEntry();
        }

        return GreyGetterRange.getMax(greys[0]);
    }

    public static <T, K extends Comparable<K>>
    void getAllAsc(List<Entry<K, Set<T>>> list, White<K, T> white) {
        if (isNull(white)) {
            return;
        }

        list.addAll(white.getEntries());

        var greys = white.getGreys();
        GreyGetterRange.getAllAsc(list, greys[0]);
    }

    public static <T, K extends Comparable<K>>
    void getAllDesc(List<Entry<K, Set<T>>> list, White<K, T> white) {
        if (isNull(white)) {
            return;
        }

        var greys = white.getGreys();
        GreyGetterRange.getAllDesc(list, greys[0]);

        for (int i = white.getEntries().size() - 1; i >= 0; i--) {
            list.add(white.getEntries().get(i));
        }
    }

    public static <T, K extends Comparable<K>>
    void getBetweenAsc(List<Entry<K, Set<T>>> list, White<K, T> white, K from, K to) {
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
                list.add(entries.get(i));
            }
        }

        if (to.compareTo(entries.lastEntry().getKey()) > 0) {
            var greys = white.getGreys();
            GreyGetterRange.getBetweenAsc(list, greys[0], from, to);
        }
    }
}
