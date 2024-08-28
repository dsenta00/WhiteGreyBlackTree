package com.wgbtree.tree.wgb.handler;

import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.White;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyHandler {

    public static <K extends Comparable<K>, T> int size(Grey<K, T> node) {
        if (isNull(node)) {
            return 0;
        }

        return node.getEntries().size() + node.getCountLeft() + node.getCountRight();
    }

    public static <K extends Comparable<K>, T> int depth(Grey<K, T> grey) {
        if (isNull(grey)) {
            return 0;
        }

        int depthLeft = depthLeft(grey);
        int depthRight = depthRight(grey);

        return Math.max(depthLeft, depthRight) + 1;
    }

    private static <K extends Comparable<K>, T> int depthLeft(@NonNull Grey<K, T> grey) {
        var left = grey.getLeft();

        if (left != null) {
            if (left instanceof White<K, T> leftAsWhite) {
                return WhiteHandler.depth(leftAsWhite);
            } else if (left instanceof Grey<K, T> leftAsGrey) {
                return depth(leftAsGrey);
            }
        }

        return 0;
    }

    private static <K extends Comparable<K>, T> int depthRight(@NonNull Grey<K, T> grey) {
        var right = grey.getRight();

        if (right != null) {
            if (right instanceof Black<K, T> rightAsWhite) {
                return BlackHandler.depth(rightAsWhite);
            } else if (right instanceof Grey<K, T> rightAsGrey) {
                return depth(rightAsGrey);
            }
        }

        return 0;
    }

    public static <T, K extends Comparable<K>> List<Set<T>> getBetweenAsc(Grey<K, T> grey, K from, K to) {
        if (isNull(grey)) {
            return List.of();
        }

        if (from.compareTo(to) > 0) {
            return List.of();
        }

        List<Set<T>> list = new LinkedList<>();
        getBetweenAsc(list, grey, from, to);
        return list;
    }

    private static <T, K extends Comparable<K>>
    void getBetweenAsc(List<Set<T>> list, Grey<K, T> grey, K from, K to) {
        if (isNull(grey)) {
            return;
        }

        var entries = grey.getEntries();
        K firstKey = entries.firstEntry().getKey();
        int index = 0;

        if (firstKey.compareTo(from) > 0) {
            WhiteHandler.getBetweenAsc(list, (White<K, T>) grey.getLeft(), from, to);
        } else {
            index = entries.searchClosest(from);
        }

        for (int i = index; i < entries.size(); i++) {
            if (entries.get(i).getKey().compareTo(to) >= 0) {
                return;
            }
            list.add(entries.get(i).getValue());
        }

        K lastKey = entries.lastEntry().getKey();

        if (lastKey.compareTo(to) < 0) {
            BlackHandler.getBetweenAsc(list, (Black<K, T>) grey.getRight(), from, to);
        }
    }

    public static <K extends Comparable<K>, T>
    void getBetweenAsc(MinHeapTree<K, T> minHeapTree, Grey<K, T> grey, K from, K to) {
        if (isNull(grey)) {
            return;
        }

        var entries = grey.getEntries();
        K firstKey = grey.getEntries().firstEntry().getKey();
        int index = 0;

        if (firstKey.compareTo(from) > 0) {
            WhiteHandler.getBetweenAsc(minHeapTree, (White<K, T>) grey.getLeft(), from, to);
        } else {
            index = entries.searchClosest(from);
        }

        for (int i = index; i < grey.getEntries().size(); i++) {
            if (entries.get(i).getKey().compareTo(to) >= 0) {
                return;
            }
            minHeapTree.push(entries.get(i));
        }

        K lastKey = entries.lastEntry().getKey();
        if (lastKey.compareTo(to) < 0) {
            BlackHandler.getBetweenAsc(minHeapTree, (Black<K, T>) grey.getRight(), from, to);
        }
    }
}
