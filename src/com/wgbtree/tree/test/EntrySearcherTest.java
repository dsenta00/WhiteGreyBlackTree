package com.wgbtree.tree.test;

import com.wgbtree.tree.wgb.handler.EntrySearcher;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

class EntrySearcherTest extends Test {

	public static void main(String[] args) {
		testSearchAsc();
		testSearchDesc();
		testBinarySearchAsc();
		testBinarySearchDesc();
	}

	static void testSearchAsc() {
		Map.Entry<Integer, Set<Integer>>[] array = (Map.Entry<Integer, Set<Integer>>[]) new Map.Entry<?, ?>[5];

		array[0] = new AbstractMap.SimpleEntry<>(1, Set.of(1));
		array[1] = new AbstractMap.SimpleEntry<>(2, Set.of(2));
		array[2] = new AbstractMap.SimpleEntry<>(3, Set.of(3));
		array[3] = new AbstractMap.SimpleEntry<>(4, Set.of(4));
		array[4] = new AbstractMap.SimpleEntry<>(5, Set.of(5));

		int index = EntrySearcher.searchAsc(array, 5, 5);

		assertEqual(4, index);
	}

	static void testSearchDesc() {
		Map.Entry<Integer, Set<Integer>>[] array = (Map.Entry<Integer, Set<Integer>>[]) new Map.Entry<?, ?>[5];

		array[0] = new AbstractMap.SimpleEntry<>(5, Set.of(5));
		array[1] = new AbstractMap.SimpleEntry<>(4, Set.of(4));
		array[2] = new AbstractMap.SimpleEntry<>(3, Set.of(3));
		array[3] = new AbstractMap.SimpleEntry<>(2, Set.of(2));
		array[4] = new AbstractMap.SimpleEntry<>(1, Set.of(1));

		int index = EntrySearcher.searchDesc(array, 1, 5);

		assertEqual(4, index);
	}

	static void testBinarySearchAsc() {
		Map.Entry<Integer, Set<Integer>>[] array = (Map.Entry<Integer, Set<Integer>>[]) new Map.Entry<?, ?>[5];

		array[0] = new AbstractMap.SimpleEntry<>(1, Set.of(1));
		array[1] = new AbstractMap.SimpleEntry<>(2, Set.of(2));
		array[2] = new AbstractMap.SimpleEntry<>(3, Set.of(3));
		array[3] = new AbstractMap.SimpleEntry<>(4, Set.of(4));
		array[4] = new AbstractMap.SimpleEntry<>(5, Set.of(5));

		int index = EntrySearcher.binarySearchAsc(array, 5, 5);

		assertEqual(4, index);
	}

	static void testBinarySearchDesc() {
		Map.Entry<Integer, Set<Integer>>[] array = (Map.Entry<Integer, Set<Integer>>[]) new Map.Entry<?, ?>[5];

		array[0] = new AbstractMap.SimpleEntry<>(5, Set.of(5));
		array[1] = new AbstractMap.SimpleEntry<>(4, Set.of(4));
		array[2] = new AbstractMap.SimpleEntry<>(3, Set.of(3));
		array[3] = new AbstractMap.SimpleEntry<>(2, Set.of(2));
		array[4] = new AbstractMap.SimpleEntry<>(1, Set.of(1));

		int index = EntrySearcher.binarySearchDesc(array, 1, 5);

		assertEqual(4, index);
	}
}