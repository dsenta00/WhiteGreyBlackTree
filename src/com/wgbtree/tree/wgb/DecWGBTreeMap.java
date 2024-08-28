package com.wgbtree.tree.wgb;

import com.wgbtree.tree.AsTree;
import com.wgbtree.tree.wgb.creator.TreeConfigCreator;
import com.wgbtree.tree.wgb.operations.delete.dec.GreyRemoverDec;
import com.wgbtree.tree.wgb.operations.get.dec.GreyGetterDec;
import com.wgbtree.tree.wgb.operations.insert.dec.GreyInserterDec;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertOrder;
import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertRank;
import static com.wgbtree.tree.wgb.calculator.RankCalculator.calculateGreatestCapacity;
import static com.wgbtree.tree.wgb.calculator.RankCalculator.calculateGreatestRank;
import static com.wgbtree.tree.wgb.constants.Constants.*;
import static com.wgbtree.tree.wgb.model.info.GrowthMode.DECELERATING;
import static java.util.Objects.isNull;

@Getter
public class DecWGBTreeMap<K extends Comparable<K>, T> extends WGBTreeMap<K, T> implements Serializable, AsTree<K, T> {

	private final int effectiveCapacity;

	public DecWGBTreeMap(int effectiveCapacity) {
		super(TreeConfigCreator.create(
				DEFAULT_ORDER,
				calculateGreatestRank(DEFAULT_ORDER, effectiveCapacity),
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				DECELERATING,
				DEFAULT_IS_BALANCED
		));

		this.effectiveCapacity = effectiveCapacity;
	}

	public DecWGBTreeMap(int effectiveCapacity, int order) {
		super(TreeConfigCreator.create(
				assertOrder(order),
				calculateGreatestRank(order, effectiveCapacity),
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				DECELERATING,
				DEFAULT_IS_BALANCED
		));

		this.effectiveCapacity = effectiveCapacity;
	}

	public DecWGBTreeMap(int order, int rank, boolean balanced) {
		super(TreeConfigCreator.create(
				assertOrder(order),
				assertRank(rank),
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				DECELERATING,
				balanced
		));

		this.effectiveCapacity = calculateGreatestCapacity(order, rank);
	}

	public DecWGBTreeMap(int effectiveCapacity, int order, boolean duplicatesAllowed, boolean balanced) {
		super(TreeConfigCreator.create(
				assertOrder(order),
				calculateGreatestRank(order, effectiveCapacity),
				duplicatesAllowed,
				DECELERATING,
				balanced
		));

		this.effectiveCapacity = effectiveCapacity;
	}

	@Override
	public WGBTreeMap<K, T> cloneEmpty() {
		return new DecWGBTreeMap<>(effectiveCapacity, config.getOrder(), config.getDuplicatesAllowed(), config.getBalanced());
	}

	@Override
	public K getMin() {
		var min = GreyGetterDec.getMin(grey);
		return isNull(min) ? null : min.getKey();
	}

	@Override
	public K getMax() {
		var max = GreyGetterDec.getMax(grey);
		return isNull(max) ? null : max.getKey();
	}

	@Override
	public List<Set<T>> getAllAsc() {
		return GreyGetterDec.getAllAsc(grey);
	}

	@Override
	public List<Set<T>> getAllDesc() {
		return GreyGetterDec.getAllDesc(grey);
	}

	@Override
	public List<Set<T>> getInAsc(List<K> keys) {
		if (keys == null || keys.isEmpty()) {
			return Collections.emptyList();
		}

		List<K> keysCopy = new LinkedList<>(keys);
		keysCopy.sort(Comparator.naturalOrder());
		return GreyGetterDec.getInAsc(grey, keysCopy);
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
		return GreyGetterDec.getBetweenAsc(grey, from, to);
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
	@SuppressWarnings("unchecked")
	public boolean containsKey(Object key) {
		K k = (K) key;
		return !GreyGetterDec.get(grey, k, k.hashCode()).isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object key) {
		K k = (K) key;
		var values = GreyGetterDec.get(grey, k, k.hashCode());
		if (values.isEmpty()) {
			return null;
		}

		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().get();
	}


	public Set<T> getValues(K key) {
		return GreyGetterDec.get(grey, key, key.hashCode());
	}

	@Override
	public T put(K key, T value) {
		var oldValue = new AtomicReference<T>();
		grey = GreyInserterDec.insert(grey, key, Set.of(value), key.hashCode(), oldValue, config);
		return oldValue.get();
	}

	@Override
	public T remove(Object key) {
		K k = (K) key;
		var result = GreyRemoverDec.remove(grey, k, k.hashCode());

		var values = result.isEmpty() ? new HashSet<T>() : result.getEntry().getValue();
		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().orElse(null);
	}
}
