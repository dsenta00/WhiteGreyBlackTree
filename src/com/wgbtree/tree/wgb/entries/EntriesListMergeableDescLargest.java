package com.wgbtree.tree.wgb.entries;

import com.wgbtree.tree.wgb.constants.LeakPolicy;
import com.wgbtree.tree.wgb.handler.EntrySearcher;
import com.wgbtree.tree.wgb.utils.InsertionSort;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.wgb.constants.LeakPolicy.SMALLEST;

public class EntriesListMergeableDescLargest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListMergeableDescLargest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListMergeableDescLargest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	public LeakPolicy getPolicy() {
		return LARGEST;
	}

	@Override
	public EntriesList<K, T> convert(LeakPolicy leakPolicy) {
		return leakPolicy == SMALLEST ? new EntriesListMergeableDescSmallest<>(this) : this;
	}

	public boolean add(Map.Entry<K, Set<T>> entry, AtomicReference<Map.Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

		Optional<Map.Entry<K, Set<T>>> existingEntryOptional = find(entry.getKey());
		if (existingEntryOptional.isPresent()) {
			existingEntryOptional.get().getValue().addAll(entry.getValue());
		} else {
			if (size >= array.length) {
				handleLeakOfFirstEntryDesc(entry, leakedEntry);
				InsertionSort.sortDescFromFront(array, size);
			} else {
				array[size++] = entry;
				InsertionSort.sortDescFromBack(array, size);
			}
		}
		return true;
	}

	@Override
	public int search(K key) {
		return EntrySearcher.searchDesc(array, key, size);
	}

	@Override
	public int searchClosest(K key) {
		return EntrySearcher.searchClosestDesc(array, key, size);
	}
}
