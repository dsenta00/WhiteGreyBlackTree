package com.wgbtree.tree.wgb.operations.delete.mersennefw;

import com.wgbtree.tree.wgb.calculator.MersenneCalculator;
import com.wgbtree.tree.wgb.model.node.black.FwMersenneBlack;
import com.wgbtree.tree.wgb.model.node.grey.FwGrey;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import com.wgbtree.tree.wgb.operations.delete.range.GreyRemoverRange;
import com.wgbtree.tree.wgb.operations.get.mersennefw.GreyGetterMersenneFw;
import com.wgbtree.tree.wgb.prime.Primes;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackRemoverMersenneFw {

    public static <K extends Comparable<K>, T>
    RemoveResult<K, T> remove(FwMersenneBlack<K, T> black, K key, int keyHash) {
        if (black == null) {
            return RemoveResult.empty();
        }

        var entries = black.getEntries();

        if (entries.isEmpty()) {
            return RemoveResult.empty();
        }

        // Try to remove from the black node
        var removedEntry = entries.remove(key);

        if (removedEntry != null) {
            suckMaxFromGreyNodes(black);
        } else {
            var lastEntry = entries.lastEntry();
            if (lastEntry != null && (key == null || lastEntry.getKey().compareTo(key) > 0)) {
                // Entry is not in the black node
                // Try to remove from the grey nodes
                var greys = black.getGreys();
                int i = MersenneCalculator.mod(keyHash, black.getCapacity(), Primes.mersenneExp(black.getCapacity()));

                var grey = greys[i];
                if (grey == null) {
                    return RemoveResult.empty();
                }

                if (black.greyRoot != null) {
                    var result = GreyRemoverMersenneFw.remove((FwGrey<K, T>) grey, key, keyHash);
                    greys[i] = (Grey<K, T>) result.getNode();
                    removedEntry = result.getEntry();
                } else {
                    var result = GreyRemoverRange.remove(grey, key);
                    greys[i] = (Grey<K, T>) result.getNode();
                    removedEntry = result.getEntry();
                }
            }
        }

        if (entries.isEmpty()) {
            black = null;
        }

        return RemoveResult.of(black, removedEntry);
    }

    public static <T, K extends Comparable<K>>
    RemoveResult<K, T> removeMax(FwMersenneBlack<K, T> black) {
        if (black == null) {
            return RemoveResult.empty();
        }

        var entries = black.getEntries();

        if (entries.isEmpty()) {
            return RemoveResult.empty();
        }

        var removedEntry = entries.remove(entries.firstEntry().getKey());
        suckMaxFromGreyNodes(black);

        if (entries.isEmpty()) {
            black = null;
        }

        return RemoveResult.of(black, removedEntry);
    }

    public static <K extends Comparable<K>, T>
    RemoveResult<K, T> removeMin(FwMersenneBlack<K, T> black) {
        if (black == null) {
            return RemoveResult.empty();
        }

        var entries = black.getEntries();

        if (entries.isEmpty()) {
            return RemoveResult.empty();
        }
        if (black.greyRoot != null) {
            var minResult = GreyGetterMersenneFw.getMin(black.getGreys());
            Map.Entry<K, Set<T>> removedEntry;
            if (minResult.isEmpty()) {
                // No grey nodes, remove minimum from the black node
                removedEntry = entries.remove(entries.lastEntry().getKey());
            } else {
                var greys = black.getGreys();
                int i = minResult.getIndex();
                K key = minResult.getEntry().getKey();
                int keyHash = key.hashCode();
                var result = GreyRemoverMersenneFw.remove((FwGrey<K, T>) greys[i], key, keyHash);
                greys[i] = (Grey<K, T>) result.getNode();
                removedEntry = result.getEntry();
            }

            if (entries.isEmpty()) {
                black = null;
            }

            return RemoveResult.of(black, removedEntry);
        } else {
            return GreyRemoverRange.removeMin(black.getGreys()[0]);
        }
    }

    private static <K extends Comparable<K>, T>
    void suckMaxFromGreyNodes(FwMersenneBlack<K, T> black) {
        var greys = black.getGreys();

        if (black.greyRoot != null) {
            var maxResult = GreyGetterMersenneFw.getMax(greys);
            if (maxResult.isEmpty()) {
                return;
            }

            int i = maxResult.getIndex();
            K key = maxResult.getEntry().getKey();
            int keyHash = key.hashCode();
            var result = GreyRemoverMersenneFw.remove((FwGrey<K, T>) greys[i], key, keyHash);
            greys[i] = (Grey<K, T>) result.getNode();

            black.getEntries().add(result.getEntry());
        } else {
            var maxResult = GreyRemoverRange.removeMax(greys[0]);

            if (maxResult.isEmpty()) {
                return;
            }

            greys[0] = (Grey<K, T>) maxResult.getNode();
            black.getEntries().add(maxResult.getEntry());
        }
    }
}
