package com.wgbtree.tree.wgb.entries;

import com.wgbtree.tree.wgb.constants.LeakPolicy;
import com.wgbtree.tree.wgb.handler.EntrySearcher;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.wgb.constants.LeakPolicy.SMALLEST;

public class EntriesListNonMergeableAscSmallest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListNonMergeableAscSmallest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableAscSmallest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	public LeakPolicy getPolicy() {
		return SMALLEST;
	}

	@Override
	public EntriesList<K, T> convert(LeakPolicy leakPolicy) {
		return leakPolicy == LARGEST ? new EntriesListNonMergeableAscLargest<>(this) : this;
	}

	public boolean add(Entry<K, Set<T>> entry, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

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

	@Override
	public int search(K key) {
		return EntrySearcher.searchAsc(array, key, size);
	}
}
