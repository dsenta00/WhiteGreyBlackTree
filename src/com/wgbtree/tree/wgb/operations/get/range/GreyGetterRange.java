package com.wgbtree.tree.wgb.operations.get.range;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.model.node.Node;
import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.White;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyGetterRange {

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> getMin(Grey<K, T> grey) {
        if (isNull(grey)) {
            // Node is empty, return null
            return null;
        }

        var left = (White<K, T>) grey.getLeft();

        if (left != null) {
            return WhiteGetterRange.getMin(left);
        }

        var entries = grey.getEntries();

        return entries.isEmpty() ? null : entries.firstEntry();
    }

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> getMax(Grey<K, T> grey) {
        if (isNull(grey)) {
            // Node is empty, return null
            return null;
        }

        var right = (Black<K, T>) grey.getRight();

        if (right != null) {
            return BlackGetterRange.getMax(right);
        }

        var entries = grey.getEntries();

        return entries.isEmpty() ? null : entries.lastEntry();
    }

    public static <K extends Comparable<K>, T>
    Set<T> get(Grey<K, T> grey, K key) {
        if (isNull(grey) || grey.getEntries().isEmpty()) {
            // Node is empty, return empty set
            return Set.of();
        }

        for (Node<K, T> n = grey; nonNull(n); n = n.nextNode(key, 0)) {
            var optionalEntry = n.getEntries().find(key);

            if (optionalEntry.isPresent()) {
                return optionalEntry.get().getValue();
            }
        }

        return Set.of();
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, Set<T>>> getAllAsc(Grey<K, T> grey) {
        List<Entry<K, Set<T>>> list = new LinkedList<>();
        getAllAsc(list, grey);
        return list;
    }

    public static <T, K extends Comparable<K>>
    void getAllAsc(List<Entry<K, Set<T>>> list, Grey<K, T> grey) {
        if (isNull(grey)) {
            return;
        }

        var left = (White<K, T>) grey.getLeft();
        WhiteGetterRange.getAllAsc(list, left);

        list.addAll(grey.getEntries());

        var right = (Black<K, T>) grey.getRight();
        BlackGetterRange.getAllAsc(list, right);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, Set<T>>> getAllDesc(Grey<K, T> grey) {
        List<Entry<K, Set<T>>> list = new LinkedList<>();
        getAllDesc(list, grey);
        return list;
    }

    public static <T, K extends Comparable<K>>
    void getAllDesc(List<Entry<K, Set<T>>> list, Grey<K, T> grey) {
        if (isNull(grey)) {
            return;
        }

        var right = (Black<K, T>) grey.getRight();
        BlackGetterRange.getAllDesc(list, right);

        for (int i = grey.getEntries().size() - 1; i >= 0; i--) {
            list.add(grey.getEntries().get(i));
        }

        var left = (White<K, T>) grey.getLeft();
        WhiteGetterRange.getAllDesc(list, left);
    }

    public static <T, K extends Comparable<K>> List<Set<T>> getInAsc(Grey<K, T> grey, List<K> keys) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, Set<T>>> getBetweenAsc(Grey<K, T> grey, K from, K to) {
        if (isNull(grey)) {
            return List.of();
        }

        if (from.compareTo(to) > 0) {
            return List.of();
        }

        List<Entry<K, Set<T>>> list = new LinkedList<>();
        getBetweenAsc(list, grey, from, to);
        return list;
    }

    public static <T, K extends Comparable<K>>
    void getBetweenAsc(List<Entry<K, Set<T>>> list, Grey<K, T> grey, K from, K to) {
        if (isNull(grey)) {
            return;
        }

        var entries = grey.getEntries();
        K firstKey = entries.firstEntry().getKey();
        int index = 0;

        if (firstKey.compareTo(from) > 0) {
            WhiteGetterRange.getBetweenAsc(list, (White<K, T>) grey.getLeft(), from, to);
        } else {
            index = entries.searchClosest(from);
        }

        for (int i = index; i < entries.size(); i++) {
            if (entries.get(i).getKey().compareTo(to) >= 0) {
                return;
            }
            list.add(entries.get(i));
        }

        K lastKey = entries.lastEntry().getKey();

        if (lastKey.compareTo(to) < 0) {
            BlackGetterRange.getBetweenAsc(list, (Black<K, T>) grey.getRight(), from, to);
        }
    }
}
