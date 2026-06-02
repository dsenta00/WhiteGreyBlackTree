package com.wgbtree.tree.wgb.operations.delete.mersennefw;

import com.wgbtree.tree.wgb.model.node.black.FwMersenneBlack;
import com.wgbtree.tree.wgb.model.node.grey.FwGrey;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.FwMersenneWhite;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map.Entry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyRemoverMersenneFw {

    public static <K extends Comparable<K>, T>
    RemoveResult<K, T> remove(FwGrey<K, T> grey, K key, int keyHash) {
        if (grey == null) {
            return RemoveResult.empty();
        }

        var entries = grey.getEntries();

        if (entries.isEmpty()) {
            return RemoveResult.empty();
        }

        // Try to remove from the grey node
        var removedEntry = entries.remove(key);

        if (removedEntry != null) {
            switch (grey.getMorePopulatedDirection()) {
                case LEFT -> {
                    var maxEntry = removeMaxFromLeft(grey);
                    grey.getEntries().add(maxEntry);
                }
                case RIGHT -> {
                    var minEntry = removeMinFromRight(grey);
                    grey.getEntries().add(minEntry);
                }
            }
        } else if (entries.lastEntry().getKey().compareTo(key) < 0) {
            removedEntry = removeFromRight(grey, key, keyHash);
        } else if (entries.firstEntry().getKey().compareTo(key) > 0) {
            removedEntry = removeFromLeft(grey, key, keyHash);
        }

        if (entries.isEmpty()) {
            unlinkGrey(grey);
            grey = null;
        }

        return RemoveResult.of(grey, removedEntry);
    }

    public static <K extends Comparable<K>, T>
    RemoveResult<K, T> removeMax(FwGrey<K, T> grey) {
        if (grey == null) {
            return RemoveResult.empty();
        }

        var right = grey.getRight();

        if (right == null) {
            var entries = grey.getEntries();
            if (entries.isEmpty()) {
                return RemoveResult.empty();
            }

            var maxEntry = entries.lastEntry();
            K key = maxEntry.getKey();
            int keyHash = key.hashCode();

            return remove(grey, key, keyHash);
        }

        var result = BlackRemoverMersenneFw.removeMax((FwMersenneBlack<K, T>) right);

        grey.setRight(result.getNode());

        if (!result.isEmpty()) {
            grey.decCountRight();
        }

        return RemoveResult.of(grey, result.getEntry());
    }

    public static <K extends Comparable<K>, T>
    RemoveResult<K, T> removeMin(FwGrey<K, T> grey) {
        if (grey == null) {
            return RemoveResult.empty();
        }

        var left = grey.getLeft();

        if (left == null) {
            var entries = grey.getEntries();
            if (entries.isEmpty()) {
                return RemoveResult.empty();
            }

            var minEntry = entries.firstEntry();
            K key = minEntry.getKey();
            int keyHash = key.hashCode();

            return remove(grey, key, keyHash);
        }

        var result = WhiteRemoverMersenneFw.removeMin((FwMersenneWhite<K, T>) left);

        grey.setLeft(result.getNode());

        if (!result.isEmpty()) {
            grey.decCountLeft();
        }

        return RemoveResult.of(grey, result.getEntry());
    }

    private static <K extends Comparable<K>, T>
    Entry<K, Set<T>> removeFromRight(@NonNull Grey<K, T> grey, K key, int keyHash) {
        var right = grey.getRight();

        if (right == null) {
            // No right node, nothing to delete, key doesn't exist
            return null;
        }

        var result = BlackRemoverMersenneFw.remove((FwMersenneBlack<K, T>) right, key, keyHash);

        grey.setRight(result.getNode());

        if (!result.isEmpty()) {
            grey.decCountRight();
        }

        return result.getEntry();
    }

    private static <K extends Comparable<K>, T>
    Entry<K, Set<T>> removeFromLeft(@NonNull Grey<K, T> grey, K key, int keyHash) {
        var left = grey.getLeft();

        if (left == null) {
            // No left node, nothing to delete, key doesn't exist
            return null;
        }

        var result = WhiteRemoverMersenneFw.remove((FwMersenneWhite<K, T>) grey.getLeft(), key, keyHash);

        grey.setLeft(result.getNode());

        if (!result.isEmpty()) {
            grey.decCountLeft();
        }

        return result.getEntry();
    }

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> removeMaxFromLeft(@NonNull Grey<K, T> grey) {
        var left = grey.getLeft();

        if (left == null) {
            throw new IllegalStateException("Left node is null");
        }

        var maxResult = WhiteRemoverMersenneFw.removeMax((FwMersenneWhite<K, T>) grey.getLeft());

        grey.setLeft(maxResult.getNode());

        if (!maxResult.isEmpty()) {
            grey.decCountLeft();
        }

        return maxResult.getEntry();
    }

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> removeMinFromRight(@NonNull Grey<K, T> grey) {
        var right = grey.getRight();

        if (right == null) {
            throw new IllegalStateException("Right node is null");
        }

        var minResult = BlackRemoverMersenneFw.removeMin((FwMersenneBlack<K, T>) grey.getRight());

        grey.setRight(minResult.getNode());

        if (!minResult.isEmpty()) {
            grey.decCountRight();
        }

        return minResult.getEntry();
    }

    private static <K extends Comparable<K>, T>
    void unlinkGrey(@NonNull FwGrey<K, T> grey) {
        var fwLeft = grey.fwLeft;
        var fwRight = grey.fwRight;

        if (fwLeft != null) {
            fwLeft.fwRight = fwRight;
        }

        if (fwRight != null) {
            fwRight.fwLeft = fwLeft;
        }
    }
}
