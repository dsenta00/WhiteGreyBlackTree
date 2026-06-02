package com.wgbtree.tree.wgb.operations.get.power;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.wgb.handler.EntryHandler;
import com.wgbtree.tree.wgb.model.node.white.White;
import com.wgbtree.tree.wgb.operations.get.acc.GreyGetterAcc;
import com.wgbtree.tree.wgb.operations.get.range.GreyGetterRange;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteGetterPower {

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

        return Arrays.stream(white.getGreys())
                .filter(Objects::nonNull)
                .map(GreyGetterAcc::getMax)
                .filter(Objects::nonNull)
                .min(Entry.comparingByKey())
                .orElse(white.getEntries().lastEntry());
    }

    public static <T, K extends Comparable<K>>
    void getAllAsc(List<Entry<K, Set<T>>> list, White<K, T> white) {
        if (isNull(white)) {
            return;
        }

        list.addAll(white.getEntries());

        var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(white.getCapacity());

        for (var grey : white.getGreys()) {
            var res = GreyGetterAcc.getAllAsc(grey);
            results.add(res.iterator());
        }

        EntryHandler.mergeAsc(list, results);
    }

    public static <T, K extends Comparable<K>>
    void getAllDesc(List<Entry<K, Set<T>>> list, White<K, T> white) {
        if (isNull(white)) {
            return;
        }

        var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(white.getCapacity());

        for (var grey : white.getGreys()) {
            var res = GreyGetterAcc.getAllDesc(grey);
            results.add(res.iterator());
        }

        EntryHandler.mergeDesc(list, results);

        for (int i = white.getEntries().size() - 1; i >= 0; i--) {
            list.add(white.getEntries().get(i));
        }
    }
}
