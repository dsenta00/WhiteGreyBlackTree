package com.wgbtree.tree.wgb.comparator;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class EntryComparator {

	public static <K extends Comparable<K>, T> int compare(@NonNull Map.Entry<K, Set<T>> e1,
														   @NonNull Map.Entry<K, Set<T>> e2) {
		return KeyComparator.compare(e1.getKey(), e2.getKey());
	}
}
