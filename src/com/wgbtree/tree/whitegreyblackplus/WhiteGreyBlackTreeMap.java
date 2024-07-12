package com.wgbtree.tree.whitegreyblackplus;

import com.wgbtree.tree.AsTree;
import com.wgbtree.tree.whitegreyblackplus.handler.GNodeHandler;
import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import com.wgbtree.tree.whitegreyblackplus.operations.get.GNodeGetter;
import com.wgbtree.tree.whitegreyblackplus.operations.insert.GNodeInserter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.wgbtree.tree.whitegreyblackplus.constants.Constants.*;
import static java.util.Objects.isNull;

/**
 * The fastest Data Structure
 * @param <K>
 * @param <T>
 */
public class WhiteGreyBlackTreeMap<K extends Comparable<K>, T> implements AsTree<K, T>, Serializable {
	private final int rank;
	private final int order;
	private final boolean allowDuplicates;
	private GNode<K, T> gNode;

	public WhiteGreyBlackTreeMap() {
		this.rank = DEFAULT_RANK;
		this.order = DEFAULT_ORDER;
		this.allowDuplicates = DEFAULT_ALLOW_DUPLICATES;
		this.gNode = null;
	}

	public WhiteGreyBlackTreeMap(int order) {
		this.rank = DEFAULT_RANK;
		this.order = assertOrder(order);
		this.allowDuplicates = DEFAULT_ALLOW_DUPLICATES;
		this.gNode = null;
	}

	public WhiteGreyBlackTreeMap(int order, boolean allowDuplicates) {
		this.rank = DEFAULT_RANK;
		this.order = assertOrder(order);
		this.allowDuplicates = allowDuplicates;
		this.gNode = null;
	}

	public WhiteGreyBlackTreeMap(int rank, int order, boolean allowDuplicates) {
		this.rank = rank;
		this.order = assertOrder(order);
		this.allowDuplicates = allowDuplicates;
		gNode = null;
	}

	@Override
	public K getMin() {
		var min = GNodeGetter.getMin(gNode);
		return isNull(min) ? null : min.getKey();
	}

	@Override
	public K getMax() {
		var max = GNodeGetter.getMax(gNode);
		return isNull(max) ? null : max.getKey();
	}

	public int getNumberOfNodes() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	public int getNumberOfEmptyNodes() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public int depth() {
		return GNodeHandler.depth(gNode);
	}

	@Override
	public String getName() {
		return "WGB[" + rank + "-" + order + "]";
	}

	@Override
	public List<Set<T>> getAllAsc() {
		return GNodeGetter.getAllAsc(gNode);
	}

	@Override
	public List<Set<T>> getAllDesc() {
		return GNodeGetter.getAllDesc(gNode);
	}

	@Override
	public List<Set<T>> getInAsc(List<K> keys) {
		if (keys == null || keys.isEmpty()) {
			return Collections.emptyList();
		}

		List<K> keysCopy = new LinkedList<>(keys);
		keysCopy.sort(Comparator.naturalOrder());
		return GNodeGetter.getInAsc(gNode, keysCopy);
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
		return GNodeHandler.size(gNode);
	}

	@Override
	public boolean isEmpty() {
		return GNodeHandler.size(gNode) == 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containsKey(Object key) {
		K k = (K) key;
		return !GNodeGetter.get(gNode, k, k.hashCode()).isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object key) {
		K k = (K) key;
		var values = GNodeGetter.get(gNode, k, k.hashCode());
		if (values.isEmpty()) {
			return null;
		}

		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().get();
	}

	public Set<T> getValues(K key) {
		return GNodeGetter.get(gNode, key, key.hashCode());
	}

	@Override
	public T put(K key, T value) {
		var oldValue = new AtomicReference<T>();
		gNode = GNodeInserter.insert(gNode, key, Set.of(value), key.hashCode(), order, rank, oldValue, allowDuplicates);
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
		gNode = null;
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
