package com.wgbtree.tree.wgb.creator;

import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.white.*;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteCreator {

    public static <K extends Comparable<K>, T>
    White<K, T> create(K key, Set<T> value, TreeConfig config) {
        var white = new White<K, T>(config.getOrder(), config.getRank(), config.getDuplicatesAllowed());
        white.getEntries().add(new SimpleEntry<>(key, value));
        return white;
    }

    public static <K extends Comparable<K>, T>
    White<K, T> createMersenne(K key, Set<T> value, TreeConfig config) {
        var white = new MersenneWhite<K, T>(config.getOrder(), config.getPower(), config.getDuplicatesAllowed());
        white.getEntries().add(new SimpleEntry<>(key, value));
        return white;
    }

    public static <K extends Comparable<K>, T>
    FwMersenneWhite<K, T> createMersenneFw(K key, Set<T> value, TreeConfig config) {
        var white = new FwMersenneWhite<K, T>(config.getOrder(), config.getPower(), config.getDuplicatesAllowed());
        white.getEntries().add(new SimpleEntry<>(key, value));
        return white;
    }

    public static <K extends Comparable<K>, T>
    White<K, T> createPower(K key, Set<T> value, TreeConfig config) {
        var white = new PowerWhite<K, T>(config.getOrder(), config.getPower(), config.getDuplicatesAllowed());
        white.getEntries().add(new SimpleEntry<>(key, value));
        return white;
    }

    public static <K extends Comparable<K>, T>
    White<K, T> createRange(K key, Set<T> value, TreeConfig config) {
        var white = new RangeWhite<K, T>(config.getOrder(), config.getDuplicatesAllowed());
        white.getEntries().add(new SimpleEntry<>(key, value));
        return white;
    }
}
