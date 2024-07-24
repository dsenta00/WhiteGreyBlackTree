package com.wgbtree.tree.wgb.entries;

import com.wgbtree.tree.wgb.constants.LeakPolicy;
import com.wgbtree.tree.wgb.handler.EntrySearcher;
import lombok.NonNull;

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

	public boolean add(@NonNull Entry<K, Set<T>> entry, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

		Optional<Entry<K, Set<T>>> existingEntryOptional = find(entry.getKey());
		if (existingEntryOptional.isPresent()) {
			existingEntryOptional.get().getValue().addAll(entry.getValue());
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

	@Override
	public int search(K key) {
		return EntrySearcher.searchAsc(array, key, size);
	}
}
