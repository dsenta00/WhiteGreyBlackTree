package com.wgbtree.tree.whitegreyblackplus.entries;

import com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class EntriesListNonMergeableAscSmallest<K extends Comparable<K>, T> extends EntriesList<K, T> {
	public EntriesListNonMergeableAscSmallest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableAscSmallest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.LARGEST ? new EntriesListNonMergeableAscLargest<>(this) : this;
	}

	public boolean add(Entry<K, Set<T>> entry, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

		if (entry == null) {
			throw new NullPointerException();
		}

		Optional<Entry<K, Set<T>>> existingEntryOptional = find(entry.getKey());
		if (existingEntryOptional.isPresent()) {
			replaceValue(entry, leakedEntry, existingEntryOptional.get());
		} else {
			if (size >= array.length) {
				handleLeakOfFirstEntryAsc(entry, leakedEntry);
			} else {
				array[size++] = entry;
			}

			Arrays.sort(array, 0, size, Entry.comparingByKey());
		}
		return true;
	}
}
