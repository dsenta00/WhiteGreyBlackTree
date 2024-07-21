package com.wgbtree.tree.whitegreyblackplus.entries;

import com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class EntriesListNonMergeableDescSmallest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListNonMergeableDescSmallest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableDescSmallest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.LARGEST ? new EntriesListNonMergeableDescLargest<>(this) : this;
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
}
