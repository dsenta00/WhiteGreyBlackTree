package com.wgbtree.tree.whitegreyblackplus.entries;

import com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class EntriesListNonMergeableAscLargest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListNonMergeableAscLargest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableAscLargest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.SMALLEST ? new EntriesListNonMergeableAscSmallest<>(this) : this;
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
			if (size >= capacityLimit) {
				handleLeakOfLastEntryAsc(entry, leakedEntry);
			} else {
				array[size++] = entry;
			}

			Arrays.sort(array, 0, size, Map.Entry.comparingByKey());
		}
		return true;
	}
}
