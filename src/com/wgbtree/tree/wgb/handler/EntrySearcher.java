package com.wgbtree.tree.wgb.handler;

import lombok.NoArgsConstructor;

import java.util.Map.Entry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EntrySearcher {

	public static <K extends Comparable<K>, T>
	int searchAsc(Entry<K, Set<T>>[] array, K key, int size) {
		int index = -1;

		if (key == null) {
			for (int i = 0; i < size; i++) {
				if (array[i].getKey() == null) {
					index = i;
					break;
				}
			}
		} else {
			index = binarySearchAsc(array, key, size);
		}

		return index;
	}

	public static <K extends Comparable<K>, T>
	int searchDesc(Entry<K, Set<T>>[] array, K key, int size) {
		int index = -1;

		if (key == null) {
			for (int i = 0; i < size; i++) {
				if (array[i].getKey() == null) {
					index = i;
					break;
				}
			}
		} else {
			index = binarySearchDesc(array, key, size);
		}

		return index;
	}

	public static <K extends Comparable<K>, T>
	int binarySearchAsc(Entry<K, Set<T>>[] array, K key, int size) {
		int low = 0;
		int high = size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			K midKey = array[mid].getKey();
			int cmp = midKey.compareTo(key);

			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				return mid; // Key found
			}
		}
		return -1;  // Key not found
	}

	public static <K extends Comparable<K>, T>
	int binarySearchDesc(Entry<K, Set<T>>[] array, K key, int size) {
		int low = 0;
		int high = size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			K midKey = array[mid].getKey();
			int cmp = midKey.compareTo(key);

			if (cmp > 0) {
				low = mid + 1;
			} else if (cmp < 0) {
				high = mid - 1;
			} else {
				return mid; // Key found
			}
		}
		return -1;  // Key not found
	}
}
