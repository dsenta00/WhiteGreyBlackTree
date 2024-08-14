package com.wgbtree.tree.wgb;

import com.wgbtree.tree.AsTree;
import com.wgbtree.tree.wgb.creator.TreeConfigCreator;
import com.wgbtree.tree.wgb.operations.delete.mersenne.GreyRemoverMersenne;
import com.wgbtree.tree.wgb.operations.get.dec.GreyGetterDec;
import com.wgbtree.tree.wgb.operations.get.mersenne.GreyGetterMersenne;
import com.wgbtree.tree.wgb.operations.insert.mersenne.GreyInserterMersenne;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertMersenneExp;
import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertOrder;
import static com.wgbtree.tree.wgb.constants.Constants.*;
import static com.wgbtree.tree.wgb.model.info.GrowthMode.MERSENNE_DECELERATING;
import static com.wgbtree.tree.wgb.prime.Primes.FIRST_MERSENNE_EXP;
import static java.util.Objects.isNull;

public class MersenneDecWgbTreeMap<K extends Comparable<K>, T> extends WGBTreeMap<K, T> implements Serializable, AsTree<K, T> {

	public MersenneDecWgbTreeMap() {
		super(TreeConfigCreator.createMersenne(
				DEFAULT_ORDER,
				FIRST_MERSENNE_EXP,
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				MERSENNE_DECELERATING,
				DEFAULT_IS_BALANCED
		));
	}

	public MersenneDecWgbTreeMap(int order) {
		super(TreeConfigCreator.createMersenne(
				assertOrder(order),
				FIRST_MERSENNE_EXP,
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				MERSENNE_DECELERATING,
				DEFAULT_IS_BALANCED
		));
	}

	public MersenneDecWgbTreeMap(int order, int power) {
		super(TreeConfigCreator.createMersenne(
				assertOrder(order),
				assertMersenneExp(power),
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				MERSENNE_DECELERATING,
				DEFAULT_IS_BALANCED
		));
	}

	public MersenneDecWgbTreeMap(int order, int power, boolean balanced) {
		super(TreeConfigCreator.createMersenne(
				assertOrder(order),
				assertMersenneExp(power),
				DEFAULT_ARE_DUPLICATES_ALLOWED,
				MERSENNE_DECELERATING,
				balanced
		));
	}

	public MersenneDecWgbTreeMap(int order, int power, boolean balanced, boolean duplicatesAllowed) {
		super(TreeConfigCreator.createMersenne(
				assertOrder(order),
				assertMersenneExp(power),
				duplicatesAllowed,
				MERSENNE_DECELERATING,
				balanced
		));
	}

	@Override
	public WGBTreeMap<K, T> cloneEmpty() {
		return new MersenneDecWgbTreeMap<>(config.getOrder(), config.getPower(), config.getBalanced(), config.getDuplicatesAllowed());
	}


	@Override
	public K getMin() {
		var min = GreyGetterMersenne.getMin(grey);
		return isNull(min) ? null : min.getKey();
	}

	@Override
	public K getMax() {
		var max = GreyGetterMersenne.getMax(grey);
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
		return GreyGetterMersenne.getInAsc(grey, keysCopy);
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
	@SuppressWarnings("unchecked")
	public boolean containsKey(Object key) {
		K k = (K) key;
		return !GreyGetterMersenne.get(grey, k, k.hashCode()).isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object key) {
		K k = (K) key;
		var values = GreyGetterMersenne.get(grey, k, k.hashCode());
		if (values.isEmpty()) {
			return null;
		}

		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().get();
	}


	public Set<T> getValues(K key) {
		return GreyGetterMersenne.get(grey, key, key.hashCode());
	}

	@Override
	public T put(K key, T value) {
		var oldValue = new AtomicReference<T>();
		grey = GreyInserterMersenne.insert(grey, key, Set.of(value), key.hashCode(), oldValue, config);
		return oldValue.get();
	}

	@Override
	public T remove(Object key) {
		K k = (K) key;
		var result = GreyRemoverMersenne.remove(grey, k, k.hashCode());

		var values = result.isEmpty() ? new HashSet<T>() : result.getEntry().getValue();
		if (values.size() > 1) {
			throw new IllegalStateException("More than one value found for key " + key);
		}

		return values.stream().findFirst().orElse(null);
	}
}
