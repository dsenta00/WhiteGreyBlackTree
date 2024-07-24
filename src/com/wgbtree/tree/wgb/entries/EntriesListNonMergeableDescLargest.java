package com.wgbtree.tree.wgb.entries;

import com.wgbtree.tree.wgb.constants.LeakPolicy;
import com.wgbtree.tree.wgb.handler.EntrySearcher;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class EntriesListNonMergeableDescLargest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListNonMergeableDescLargest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableDescLargest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.SMALLEST ? new EntriesListNonMergeableDescSmallest<>(this) : this;
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
				handleLeakOfFirstEntryDesc(entry, leakedEntry);
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
