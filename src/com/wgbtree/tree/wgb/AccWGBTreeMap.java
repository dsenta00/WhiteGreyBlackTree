package com.wgbtree.tree.wgb;

import com.wgbtree.tree.AsTree;
import com.wgbtree.tree.wgb.creator.TreeConfigCreator;
import com.wgbtree.tree.wgb.handler.GreyHandler;
import com.wgbtree.tree.wgb.operations.delete.acc.GreyRemoverAcc;
import com.wgbtree.tree.wgb.operations.get.acc.GreyGetterAcc;
import com.wgbtree.tree.wgb.operations.insert.acc.GreyInserterAcc;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertOrder;
import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertRank;
import static com.wgbtree.tree.wgb.constants.Constants.*;
import static com.wgbtree.tree.wgb.model.info.GrowthMode.ACCELERATING;
import static java.util.Objects.isNull;

@Getter
public class AccWGBTreeMap<K extends Comparable<K>, T> extends WGBTreeMap<K, T> implements Serializable, AsTree<K, T> {

	public AccWGBTreeMap() {
		super(TreeConfigCreator.create(
				DEFAULT_ORDER,
				DEFAULT_RANK,
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				ACCELERATING,
				DEFAULT_IS_BALANCED
		));
	}

	public AccWGBTreeMap(int order) {
		super(TreeConfigCreator.create(
				assertOrder(order),
				DEFAULT_RANK,
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				ACCELERATING,
				DEFAULT_IS_BALANCED
		));
	}

	public AccWGBTreeMap(int order, int rank) {
		super(TreeConfigCreator.create(
				assertOrder(order),
				assertRank(rank),
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				ACCELERATING,
				DEFAULT_IS_BALANCED
		));
	}

	public AccWGBTreeMap(int order, int rank, boolean balanced) {
		super(TreeConfigCreator.create(
				assertOrder(order),
				assertRank(rank),
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				ACCELERATING,
				balanced
		));
	}

	public AccWGBTreeMap(int order, int rank, boolean balanced, boolean duplicatesAllowed) {
		super(TreeConfigCreator.create(
				assertOrder(order),
				assertRank(rank),
				duplicatesAllowed,
				ACCELERATING,
				balanced
		));
	}

	@Override
	public WGBTreeMap<K, T> cloneEmpty() {
		return new AccWGBTreeMap<>(config.getOrder(), config.getRank(), config.getDuplicatesAllowed(), config.getDuplicatesAllowed());
	}

	@Override
	public K getMin() {
		var min = GreyGetterAcc.getMin(grey);
		return isNull(min) ? null : min.getKey();
	}

	@Override
	public K getMax() {
		var max = GreyGetterAcc.getMax(grey);
		return isNull(max) ? null : max.getKey();
	}

	@Override
	public List<Entry<K, Set<T>>> getAllAsc() {
		return GreyGetterAcc.getAllAsc(grey);
	}

	@Override
	public List<Entry<K, Set<T>>> getAllDesc() {
		return GreyGetterAcc.getAllDesc(grey);
	}

	@Override
	public List<Set<T>> getInAsc(List<K> keys) {
		if (keys == null || keys.isEmpty()) {
			return Collections.emptyList();
		}

		List<K> keysCopy = new LinkedList<>(keys);
		keysCopy.sort(Comparator.naturalOrder());
		return GreyGetterAcc.getInAsc(grey, keysCopy);
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
	public List<Entry<K, Set<T>>> getBetweenAsc(K from, K to) {
		return GreyHandler.getBetweenAsc(grey, from, to);
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
		return !GreyGetterAcc.get(grey, k, k.hashCode()).isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object key) {
		K k = (K) key;
		var values = GreyGetterAcc.get(grey, k, k.hashCode());
		if (values.isEmpty()) {
			return null;
		}

		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().get();
	}


	public Set<T> getValues(K key) {
		return GreyGetterAcc.get(grey, key, key.hashCode());
	}

	@Override
	public T put(K key, T value) {
		var oldValue = new AtomicReference<T>();
		grey = GreyInserterAcc.insert(grey, key, Set.of(value), key.hashCode(), oldValue, config);
		return oldValue.get();
	}

	@Override
	public T remove(Object key) {
		K k = (K) key;
		var result = GreyRemoverAcc.remove(grey, k, k.hashCode());

		var values = result.isEmpty() ? new HashSet<T>() : result.getEntry().getValue();
		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().orElse(null);
	}
}
