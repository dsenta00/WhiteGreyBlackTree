package com.wgbtree.tree.whitegreyblackplus.entries;

import com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class EntriesListMergeableDescLargest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListMergeableDescLargest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListMergeableDescLargest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.SMALLEST ? new EntriesListMergeableDescSmallest<>(this) : this;
	}

	public boolean add(Map.Entry<K, Set<T>> entry, AtomicReference<Map.Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

		if (entry == null) {
			throw new NullPointerException();
		}

		Optional<Map.Entry<K, Set<T>>> existingEntryOptional = find(entry.getKey());
		if (existingEntryOptional.isPresent()) {
			existingEntryOptional.get().getValue().addAll(entry.getValue());
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
}
