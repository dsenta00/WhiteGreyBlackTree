package com.wgbtree.tree.wgb.operations.get.mersennefw;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.handler.EntryHandler;
import com.wgbtree.tree.wgb.model.node.white.FwMersenneWhite;
import com.wgbtree.tree.wgb.model.node.white.White;
import com.wgbtree.tree.wgb.operations.get.range.GreyGetterRange;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteGetterMersenneFw {

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> getMin(White<K, T> white) {
        if (isNull(white) || white.getEntries().isEmpty()) {
            return null;
        }

        return white.getEntries().firstEntry();
    }

    public static <K extends Comparable<K>, T>
    Entry<K, Set<T>> getMax(FwMersenneWhite<K, T> white) {
        if (isNull(white) || white.getEntries().isEmpty()) {
            return null;
        }

        Entry<K, Set<T>> max;

        if (white.greyRoot != null) {
            max = GreyGetterMersenneFw.getMax(white.greyRoot);
            for (var grey = white.greyRoot.fwRight; grey != null; grey = grey.fwRight) {
                var greyMax = GreyGetterMersenneFw.getMax(grey);
                if (isNull(greyMax)) {
                    continue;
                }

                if (isNull(max) || greyMax.getKey().compareTo(max.getKey()) < 0) {
                    max = greyMax;
                }
            }
        } else {
            max = GreyGetterRange.getMax(white.getGreys()[0]);
        }

        if (max == null) {
            return white.getEntries().lastEntry();
        }

        return max;
    }

    public static <T, K extends Comparable<K>>
    void getAllAsc(List<Entry<K, Set<T>>> list, FwMersenneWhite<K, T> white) {
        if (isNull(white)) {
            return;
        }

        list.addAll(white.getEntries());

        var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(white.getCapacity());
        if (white.greyRoot == null) {
            for (var grey : white.getGreys()) {
                var res = GreyGetterRange.getAllAsc(grey);
                results.add(res.iterator());
            }
        } else {
            for (var grey = white.greyRoot; grey != null; grey = grey.fwRight) {
                var res = GreyGetterMersenneFw.getAllAsc(grey);
                results.add(res.iterator());
            }
        }

        EntryHandler.mergeAsc(list, results);
    }

    public static <T, K extends Comparable<K>>
    void getAllDesc(List<Entry<K, Set<T>>> list, FwMersenneWhite<K, T> white) {
        if (isNull(white)) {
            return;
        }

        var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(white.getCapacity());

        if (white.greyRoot == null) {
            for (var grey : white.getGreys()) {
                var res = GreyGetterRange.getAllDesc(grey);
                results.add(res.iterator());
            }
        } else {
            for (var grey = white.greyRoot; grey != null; grey = grey.fwRight) {
                var res = GreyGetterMersenneFw.getAllDesc(grey);
                results.add(res.iterator());
            }
        }

        EntryHandler.mergeDesc(list, results);

        for (int i = white.getEntries().size() - 1; i >= 0; i--) {
            list.add(white.getEntries().get(i));
        }
    }

    public static <T, K extends Comparable<K>>
    void getBetweenAsc(List<Entry<K, Set<T>>> list, FwMersenneWhite<K, T> white, K from, K to) {
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
            var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(white.getCapacity());

            if (white.greyRoot == null) {
                for (var grey : white.getGreys()) {
                    var res = GreyGetterRange.getBetweenAsc(grey, from, to);
                    results.add(res.iterator());
                }
            } else {
                for (var grey = white.greyRoot; grey != null; grey = grey.fwRight) {
                    var res = GreyGetterMersenneFw.getBetweenAsc(grey, from, to);
                    results.add(res.iterator());
                }
            }

            EntryHandler.mergeAsc(list, results);
        }
    }
}
