package com.wgbtree.tree;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AsTree<K extends Comparable<K>, T> extends Map<K, T> {
    K getMin();
    K getMax();
    int depth();
    String getName();
    List<Entry<K, Set<T>>> getAllAsc();
    List<Entry<K, Set<T>>> getAllDesc();
    List<Set<T>> getInAsc(List<K> keys);
    List<Set<T>> getInDesc(List<K> keys);
    List<Set<T>> getNotInAsc(List<K> keys);
    List<Set<T>> getNotInDesc(List<K> keys);
    List<Entry<K, Set<T>>> getBetweenAsc(K from, K to);
    List<Set<T>> getBetweenDesc(K from, K to);
    List<Set<T>> getBetweenAscInclusive(K from, K to);
    List<Set<T>> getBetweenDescInclusive(K from, K to);
    List<Set<T>> getNotBetweenAsc(K from, K to);
    List<Set<T>> getNotBetweenDesc(K from, K to);
    List<Set<T>> getNotBetweenAscInclusive(K from, K to);
    List<Set<T>> getNotBetweenDescInclusive(K from, K to);
    List<Set<T>> getGreaterThanAsc(K from);
    List<Set<T>> getGreaterThanDesc(K from);
    List<Set<T>> getGreaterThanAscInclusive(K from);
    List<Set<T>> getGreaterThanDescInclusive(K from);
    List<Set<T>> getLessThanAsc(K to);
    List<Set<T>> getLessThanDesc(K to);
    List<Set<T>> getLessThanAscInclusive(K to);
    List<Set<T>> getLessThanDescInclusive(K to);
    List<Set<T>> getNotEqualAsc(K key);
    List<Set<T>> getNotEqualDesc(K key);
}
