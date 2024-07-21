package com.wgbtree.tree.whitegreyblackplus;

import com.wgbtree.tree.AsTree;
import com.wgbtree.tree.whitegreyblackplus.handler.GreyHandler;
import com.wgbtree.tree.whitegreyblackplus.node.Grey;
import com.wgbtree.tree.whitegreyblackplus.node.LevelInfo;
import com.wgbtree.tree.whitegreyblackplus.operations.get.GreyGetter;
import com.wgbtree.tree.whitegreyblackplus.operations.insert.GreyInserter;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.whitegreyblackplus.constants.Constants.*;
import static java.util.Objects.isNull;

/**
 * The fastest Data Structure
 *
 * @param <K>
 * @param <T>
 */
public class WhiteGreyBlackTreeMap<K extends Comparable<K>, T> implements AsTree<K, T>, Serializable {
	@Getter
	private final LevelInfo firstLevel;
	@Getter
	private Grey<K, T> grey;

	public WhiteGreyBlackTreeMap() {
		firstLevel = LevelInfo.of(DEFAULT_RANK, DEFAULT_ORDER, DEFAULT_ALLOW_DUPLICATES, DEFAULT_DECREASING_PRIMES);
		grey = null;
	}

	public WhiteGreyBlackTreeMap(int order) {
		firstLevel = LevelInfo.of(assertOrder(order), DEFAULT_RANK, DEFAULT_ALLOW_DUPLICATES, DEFAULT_DECREASING_PRIMES);
		grey = null;
	}

	public WhiteGreyBlackTreeMap(int order, boolean allowDuplicates) {
		firstLevel = LevelInfo.of(assertOrder(order), DEFAULT_RANK, allowDuplicates, DEFAULT_DECREASING_PRIMES);
		grey = null;
	}

	public WhiteGreyBlackTreeMap(int rank, int order, boolean allowDuplicates) {
		firstLevel = LevelInfo.of(assertOrder(order), rank, allowDuplicates, DEFAULT_DECREASING_PRIMES);
		grey = null;
	}

	public WhiteGreyBlackTreeMap(int order, boolean allowDuplicates, int effectiveCapacity) {
		firstLevel = LevelInfo.of(order, effectiveCapacity, allowDuplicates);
		grey = null;
	}

	@Override
	public K getMin() {
		var min = GreyGetter.getMin(grey);
		return isNull(min) ? null : min.getKey();
	}

	@Override
	public K getMax() {
		var max = GreyGetter.getMax(grey);
		return isNull(max) ? null : max.getKey();
	}

	@Override
	public int depth() {
		return GreyHandler.depth(grey);
	}

	@Override
	public String getName() {
		return "WGB[" + firstLevel.getRank() + "-" + firstLevel.getOrder() + "]";
	}

	@Override
	public List<Set<T>> getAllAsc() {
		return GreyGetter.getAllAsc(grey);
	}

	@Override
	public List<Set<T>> getAllDesc() {
		return GreyGetter.getAllDesc(grey);
	}

	@Override
	public List<Set<T>> getInAsc(List<K> keys) {
		if (keys == null || keys.isEmpty()) {
			return Collections.emptyList();
		}

		List<K> keysCopy = new LinkedList<>(keys);
		keysCopy.sort(Comparator.naturalOrder());
		return GreyGetter.getInAsc(grey, keysCopy);
	}

	@Override
	public List<Set<T>> getInDesc(List<K> keys) {
		return null;
	}

	@Override
	public List<Set<T>> getNotInAsc(List<K> keys) {
		return null;
	}

	@Override
	public List<Set<T>> getNotInDesc(List<K> keys) {
		return null;
	}

	@Override
	public List<Set<T>> getBetweenAsc(K from, K to) {
		return null;
	}

	@Override
	public List<Set<T>> getBetweenDesc(K from, K to) {
		return null;
	}

	@Override
	public List<Set<T>> getBetweenAscInclusive(K from, K to) {
		return null;
	}

	@Override
	public List<Set<T>> getBetweenDescInclusive(K from, K to) {
		return null;
	}

	@Override
	public List<Set<T>> getNotBetweenAsc(K from, K to) {
		return null;
	}

	@Override
	public List<Set<T>> getNotBetweenDesc(K from, K to) {
		return null;
	}

	@Override
	public List<Set<T>> getNotBetweenAscInclusive(K from, K to) {
		return null;
	}

	@Override
	public List<Set<T>> getNotBetweenDescInclusive(K from, K to) {
		return null;
	}

	@Override
	public List<Set<T>> getGreaterThanAsc(K from) {
		return null;
	}

	@Override
	public List<Set<T>> getGreaterThanDesc(K from) {
		return null;
	}

	@Override
	public List<Set<T>> getGreaterThanAscInclusive(K from) {
		return null;
	}

	@Override
	public List<Set<T>> getGreaterThanDescInclusive(K from) {
		return null;
	}

	@Override
	public List<Set<T>> getLessThanAsc(K to) {
		return null;
	}

	@Override
	public List<Set<T>> getLessThanDesc(K to) {
		return null;
	}

	@Override
	public List<Set<T>> getLessThanAscInclusive(K to) {
		return null;
	}

	@Override
	public List<Set<T>> getLessThanDescInclusive(K to) {
		return null;
	}

	@Override
	public List<Set<T>> getNotEqualAsc(K key) {
		return null;
	}

	@Override
	public List<Set<T>> getNotEqualDesc(K key) {
		return null;
	}

	@Override
	public int size() {
		return GreyHandler.size(grey);
	}

	@Override
	public boolean isEmpty() {
		return GreyHandler.size(grey) == 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containsKey(Object key) {
		K k = (K) key;
		return !GreyGetter.get(grey, k, k.hashCode()).isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object key) {
		K k = (K) key;
		var values = GreyGetter.get(grey, k, k.hashCode());
		if (values.isEmpty()) {
			return null;
		}

		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().get();
	}

	public Set<T> getValues(K key) {
		return GreyGetter.get(grey, key, key.hashCode());
	}

	@Override
	public T put(K key, T value) {
		var oldValue = new AtomicReference<T>();
		grey = GreyInserter.insert(grey, key, Set.of(value), key.hashCode(), oldValue, firstLevel);
		return oldValue.get();
	}

	@Override
	public T remove(Object key) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public void putAll(Map<? extends K, ? extends T> m) {
		m.forEach(this::put);
	}

	@Override
	public void clear() {
		grey = null;
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Collection<T> values() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Set<Entry<K, T>> entrySet() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	private int assertOrder(int order) {
		if (order <= 0) {
			throw new IllegalArgumentException("Order must be greater than 0, got " + order + " instead.");
		}

		return order;
	}
}
