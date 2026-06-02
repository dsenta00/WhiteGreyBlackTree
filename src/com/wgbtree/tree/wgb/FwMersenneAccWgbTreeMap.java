package com.wgbtree.tree.wgb;

import com.wgbtree.tree.AsTree;
import com.wgbtree.tree.wgb.creator.TreeConfigCreator;
import com.wgbtree.tree.wgb.model.node.grey.FwGrey;
import com.wgbtree.tree.wgb.operations.delete.mersennefw.GreyRemoverMersenneFw;
import com.wgbtree.tree.wgb.operations.get.mersennefw.GreyGetterMersenneFw;
import com.wgbtree.tree.wgb.operations.insert.mersennefw.GreyInserterMersenneFw;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertMersenneExp;
import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertOrder;
import static com.wgbtree.tree.wgb.constants.Constants.*;
import static com.wgbtree.tree.wgb.model.info.GrowthMode.FW_MERSENNE_ACCELERATING;
import static com.wgbtree.tree.wgb.prime.Primes.FIRST_MERSENNE_EXP;
import static java.util.Objects.isNull;

public class FwMersenneAccWgbTreeMap<K extends Comparable<K>, T> extends WGBTreeMap<K, T> implements Serializable, AsTree<K, T> {

	private static final boolean DEFAULT_IS_BALANCED = false;

	public FwMersenneAccWgbTreeMap() {
		super(TreeConfigCreator.createMersenne(
				DEFAULT_ORDER,
				FIRST_MERSENNE_EXP,
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				FW_MERSENNE_ACCELERATING,
				DEFAULT_IS_BALANCED
		));
	}

	public FwMersenneAccWgbTreeMap(int order) {
		super(TreeConfigCreator.createMersenne(
				assertOrder(order),
				FIRST_MERSENNE_EXP,
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				FW_MERSENNE_ACCELERATING,
				DEFAULT_IS_BALANCED
		));
	}

	public FwMersenneAccWgbTreeMap(int order, int power) {
		super(TreeConfigCreator.createMersenne(
				assertOrder(order),
				assertMersenneExp(power),
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				FW_MERSENNE_ACCELERATING,
				DEFAULT_IS_BALANCED
		));
	}
	public FwMersenneAccWgbTreeMap(int order, int power, boolean duplicatesAllowed) {
		super(TreeConfigCreator.createMersenne(
				assertOrder(order),
				assertMersenneExp(power),
				duplicatesAllowed,
				FW_MERSENNE_ACCELERATING,
				DEFAULT_IS_BALANCED
		));
	}

	@Override
	public WGBTreeMap<K, T> cloneEmpty() {
		return new FwMersenneAccWgbTreeMap<>(config.getOrder(), config.getPower(), config.getDuplicatesAllowed());
	}

	@Override
	public K getMin() {
		var min = GreyGetterMersenneFw.getMin(grey);
		return isNull(min) ? null : min.getKey();
	}

	@Override
	public K getMax() {
		var max = GreyGetterMersenneFw.getMax(grey);
		return isNull(max) ? null : max.getKey();
	}

	@Override
	public List<Entry<K, Set<T>>>  getAllAsc() {
		return GreyGetterMersenneFw.getAllAsc((FwGrey<K, T>) grey);
	}

	@Override
	public List<Entry<K, Set<T>>>  getAllDesc() {
		return GreyGetterMersenneFw.getAllDesc((FwGrey<K, T>) grey);
	}

	@Override
	public List<Set<T>> getInAsc(List<K> keys) {
		if (keys == null || keys.isEmpty()) {
			return Collections.emptyList();
		}

		List<K> keysCopy = new LinkedList<>(keys);
		keysCopy.sort(Comparator.naturalOrder());
		return GreyGetterMersenneFw.getInAsc((FwGrey<K, T>) grey, keysCopy);
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
		return GreyGetterMersenneFw.getBetweenAsc((FwGrey<K, T>) grey, from, to);
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
		return !GreyGetterMersenneFw.get(grey, k, k.hashCode()).isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object key) {
		K k = (K) key;
		var values = GreyGetterMersenneFw.get(grey, k, k.hashCode());
		if (values.isEmpty()) {
			return null;
		}

		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().get();
	}


	public Set<T> getValues(K key) {
		return GreyGetterMersenneFw.get(grey, key, key.hashCode());
	}

	@Override
	public T put(K key, T value) {
		var oldValue = new AtomicReference<T>();
		grey = GreyInserterMersenneFw.insert((FwGrey<K, T>) grey, key, Set.of(value), key.hashCode(), oldValue, config);
		return oldValue.get();
	}

	@Override
	public T remove(Object key) {
		K k = (K) key;
		var result = GreyRemoverMersenneFw.remove((FwGrey<K, T>) grey, k, k.hashCode());

		var values = result.isEmpty() ? new HashSet<T>() : result.getEntry().getValue();
		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().orElse(null);
	}
}
