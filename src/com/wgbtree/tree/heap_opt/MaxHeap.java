package com.wgbtree.tree.heap_opt;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;

public class MaxHeap<K extends Comparable<K>, T> extends Heap<K, T> {

	public MaxHeap() {
		super(comparingByKey(reverseOrder()));
	}
}
