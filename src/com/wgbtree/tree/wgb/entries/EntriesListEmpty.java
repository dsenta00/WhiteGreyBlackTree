package com.wgbtree.tree.wgb.entries;

import com.wgbtree.tree.wgb.constants.LeakPolicy;
import com.wgbtree.tree.wgb.handler.EntrySearcher;
import com.wgbtree.tree.wgb.utils.InsertionSort;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.wgb.constants.LeakPolicy.SMALLEST;

public class EntriesListEmpty<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListEmpty() {
		super(0);
	}

	@Override
	public LeakPolicy getPolicy() {
		return LARGEST;
	}

	@Override
	public EntriesList<K, T> convert(LeakPolicy leakPolicy) {
		return this;
	}

	public boolean add(Map.Entry<K, Set<T>> entry, AtomicReference<Map.Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

		return false;
	}

	@Override
	public int search(K key) {
		return -1;
	}

	@Override
	public int searchClosest(K key) {
		return -1;
	}
}
