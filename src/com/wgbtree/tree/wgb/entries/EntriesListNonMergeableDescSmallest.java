package com.wgbtree.tree.wgb.entries;

import com.wgbtree.tree.wgb.constants.LeakPolicy;
import com.wgbtree.tree.wgb.handler.EntrySearcher;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.wgb.constants.LeakPolicy.SMALLEST;

public class EntriesListNonMergeableDescSmallest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListNonMergeableDescSmallest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableDescSmallest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	public LeakPolicy getPolicy() {
		return SMALLEST;
	}

	@Override
	public EntriesList<K, T> convert(LeakPolicy leakPolicy) {
		return leakPolicy == LARGEST ? new EntriesListNonMergeableDescLargest<>(this) : this;
	}

	public boolean add(Map.Entry<K, Set<T>> entry, AtomicReference<Map.Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

		if (entry == null) {
			throw new NullPointerException();
		}

		Optional<Map.Entry<K, Set<T>>> existingEntryOptional = find(entry.getKey());
		if (existingEntryOptional.isPresent()) {
			replaceValue(entry, leakedEntry, existingEntryOptional.get());
		} else {
			if (size >= array.length) {
				handleLeakOfLastEntryDesc(entry, leakedEntry);
			} else {
				array[size++] = entry;
			}

			Arrays.sort(array, 0, size, Map.Entry.comparingByKey(Comparator.reverseOrder()));
		}
		return true;
	}

	@Override
	public int search(K key) {
		return EntrySearcher.searchDesc(array, key, size);
	}
}
