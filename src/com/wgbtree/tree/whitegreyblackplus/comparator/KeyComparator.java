package com.wgbtree.tree.whitegreyblackplus.comparator;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class KeyComparator {

	public static <K extends Comparable<K>> int compare(K k1, K k2) {
		if (k1 == null) {
			return -1;
		}

		if (k2 == null) {
			return 1;
		}

		return k1.compareTo(k2);
	}
}
