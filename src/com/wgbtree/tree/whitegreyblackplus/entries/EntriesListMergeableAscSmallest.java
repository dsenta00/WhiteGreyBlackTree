package com.wgbtree.tree.whitegreyblackplus.entries;

import com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class EntriesListMergeableAscSmallest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListMergeableAscSmallest(int capacityLimit) {
		super(capacityLimit);
	}

	public EntriesListMergeableAscSmallest(EntriesList<K, T> entries) {
		super(entries);
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.LARGEST ? new EntriesListMergeableAscLargest<>(this) : this;
	}

	public boolean add(Entry<K, Set<T>> entry, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

		if (entry == null) {
			throw new NullPointerException();
		}

		Optional<Entry<K, Set<T>>> existingEntryOptional = find(entry.getKey());
		if (existingEntryOptional.isPresent()) {
			existingEntryOptional.get().getValue().addAll(entry.getValue());
		} else {
			if (size >= capacityLimit) {
				handleLeakOfFirstEntryAsc(entry, leakedEntry);
			} else {
				array[size++] = entry;
			}

			Arrays.sort(array, 0, size, Entry.comparingByKey());
		}
		return true;
	}
}
