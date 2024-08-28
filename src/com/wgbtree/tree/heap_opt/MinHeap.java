package com.wgbtree.tree.heap_opt;

import static java.util.Map.Entry.comparingByKey;

public class MinHeap<K extends Comparable<K>, T> extends Heap<K, T> {

	public MinHeap() {
		super(comparingByKey());
	}
}
