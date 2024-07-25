package com.wgbtree.tree.test;

import com.wgbtree.tree.wgb.entries.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class EntriesTest extends Test {

	public static void main(String[] args) {
		testAscSmallest(new EntriesListMergeableAscSmallest<>(5));
		testAscSmallest(new EntriesListNonMergeableAscSmallest<>(5));

		testAscLargest(new EntriesListMergeableAscLargest<>(5));
		testAscLargest(new EntriesListNonMergeableAscLargest<>(5));

		testDescLargest(new EntriesListMergeableDescLargest<>(5));
		testDescLargest(new EntriesListNonMergeableDescLargest<>(5));

		testDescSmallest(new EntriesListMergeableDescSmallest<>(5));
		testDescSmallest(new EntriesListNonMergeableDescSmallest<>(5));
	}

	private static void testAscSmallest(EntriesList<Integer, Integer> entries) {
		var leakEntry =  new AtomicReference<Entry<Integer, Set<Integer>>>();
		assertEquals(0, entries.size());
		assertEquals(null, entries.get(0));

		entries.add(new SimpleEntry<>(5, Set.of(5)), leakEntry);
		entries.add(new SimpleEntry<>(4, Set.of(4)), leakEntry);
		entries.add(new SimpleEntry<>(3, Set.of(3)), leakEntry);
		entries.add(new SimpleEntry<>(2, Set.of(2)), leakEntry);
		entries.add(new SimpleEntry<>(1, Set.of(1)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(1, entries.get(0).getKey());
		assertEquals(2, entries.get(1).getKey());
		assertEquals(3, entries.get(2).getKey());
		assertEquals(4, entries.get(3).getKey());
		assertEquals(5, entries.get(4).getKey());
		assertEquals(null, leakEntry.get());

		entries.add(new SimpleEntry<>(0, Set.of(0)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(1, entries.get(0).getKey());
		assertEquals(2, entries.get(1).getKey());
		assertEquals(3, entries.get(2).getKey());
		assertEquals(4, entries.get(3).getKey());
		assertEquals(5, entries.get(4).getKey());
		assertEquals(0, leakEntry.get().getKey());

		entries.add(new SimpleEntry<>(6, Set.of(6)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(2, entries.get(0).getKey());
		assertEquals(3, entries.get(1).getKey());
		assertEquals(4, entries.get(2).getKey());
		assertEquals(5, entries.get(3).getKey());
		assertEquals(6, entries.get(4).getKey());
		assertEquals(1, leakEntry.get().getKey());
	}

	private static void testDescLargest(EntriesList<Integer, Integer> entries) {
		var leakEntry =  new AtomicReference<Entry<Integer, Set<Integer>>>();
		assertEquals(0, entries.size());
		assertEquals(null, entries.get(0));

		entries.add(new SimpleEntry<>(5, Set.of(5)), leakEntry);
		entries.add(new SimpleEntry<>(4, Set.of(4)), leakEntry);
		entries.add(new SimpleEntry<>(3, Set.of(3)), leakEntry);
		entries.add(new SimpleEntry<>(2, Set.of(2)), leakEntry);
		entries.add(new SimpleEntry<>(1, Set.of(1)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(5, entries.get(0).getKey());
		assertEquals(4, entries.get(1).getKey());
		assertEquals(3, entries.get(2).getKey());
		assertEquals(2, entries.get(3).getKey());
		assertEquals(1, entries.get(4).getKey());
		assertEquals(null, leakEntry.get());

		entries.add(new SimpleEntry<>(6, Set.of(6)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(5, entries.get(0).getKey());
		assertEquals(4, entries.get(1).getKey());
		assertEquals(3, entries.get(2).getKey());
		assertEquals(2, entries.get(3).getKey());
		assertEquals(1, entries.get(4).getKey());
		assertEquals(6, leakEntry.get().getKey());

		entries.add(new SimpleEntry<>(0, Set.of(0)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(4, entries.get(0).getKey());
		assertEquals(3, entries.get(1).getKey());
		assertEquals(2, entries.get(2).getKey());
		assertEquals(1, entries.get(3).getKey());
		assertEquals(0, entries.get(4).getKey());
		assertEquals(5, leakEntry.get().getKey());
	}

	private static void testAscLargest(EntriesList<Integer, Integer> entries) {
		var leakEntry =  new AtomicReference<Entry<Integer, Set<Integer>>>();
		assertEquals(0, entries.size());
		assertEquals(null, entries.get(0));

		entries.add(new SimpleEntry<>(1, Set.of(1)), leakEntry);
		entries.add(new SimpleEntry<>(2, Set.of(2)), leakEntry);
		entries.add(new SimpleEntry<>(3, Set.of(3)), leakEntry);
		entries.add(new SimpleEntry<>(4, Set.of(4)), leakEntry);
		entries.add(new SimpleEntry<>(5, Set.of(5)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(1, entries.get(0).getKey());
		assertEquals(2, entries.get(1).getKey());
		assertEquals(3, entries.get(2).getKey());
		assertEquals(4, entries.get(3).getKey());
		assertEquals(5, entries.get(4).getKey());
		assertEquals(null, leakEntry.get());

		entries.add(new SimpleEntry<>(6, Set.of(6)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(1, entries.get(0).getKey());
		assertEquals(2, entries.get(1).getKey());
		assertEquals(3, entries.get(2).getKey());
		assertEquals(4, entries.get(3).getKey());
		assertEquals(5, entries.get(4).getKey());
		assertEquals(6, leakEntry.get().getKey());

		entries.add(new SimpleEntry<>(0, Set.of(0)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(0, entries.get(0).getKey());
		assertEquals(1, entries.get(1).getKey());
		assertEquals(2, entries.get(2).getKey());
		assertEquals(3, entries.get(3).getKey());
		assertEquals(4, entries.get(4).getKey());
		assertEquals(5, leakEntry.get().getKey());
	}

	private static void testDescSmallest(EntriesList<Integer, Integer> entries) {
		var leakEntry =  new AtomicReference<Entry<Integer, Set<Integer>>>();
		assertEquals(0, entries.size());
		assertEquals(null, entries.get(0));

		entries.add(new SimpleEntry<>(1, Set.of(1)), leakEntry);
		entries.add(new SimpleEntry<>(2, Set.of(2)), leakEntry);
		entries.add(new SimpleEntry<>(3, Set.of(3)), leakEntry);
		entries.add(new SimpleEntry<>(4, Set.of(4)), leakEntry);
		entries.add(new SimpleEntry<>(5, Set.of(5)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(5, entries.get(0).getKey());
		assertEquals(4, entries.get(1).getKey());
		assertEquals(3, entries.get(2).getKey());

		assertEquals(2, entries.get(3).getKey());
		assertEquals(1, entries.get(4).getKey());
		assertEquals(null, leakEntry.get());

		entries.add(new SimpleEntry<>(0, Set.of(0)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(5, entries.get(0).getKey());
		assertEquals(4, entries.get(1).getKey());
		assertEquals(3, entries.get(2).getKey());
		assertEquals(2, entries.get(3).getKey());
		assertEquals(1, entries.get(4).getKey());
		assertEquals(0, leakEntry.get().getKey());

		entries.add(new SimpleEntry<>(6, Set.of(6)), leakEntry);

		assertEquals(5, entries.size());
		assertEquals(6, entries.get(0).getKey());
		assertEquals(5, entries.get(1).getKey());
		assertEquals(4, entries.get(2).getKey());
		assertEquals(3, entries.get(3).getKey());
		assertEquals(2, entries.get(4).getKey());
		assertEquals(1, leakEntry.get().getKey());
	}
}
