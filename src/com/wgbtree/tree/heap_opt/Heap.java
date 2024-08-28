package com.wgbtree.tree.heap_opt;

import lombok.SneakyThrows;

import java.util.*;
import java.util.Map.Entry;

public abstract class Heap<K extends Comparable<K>, T> extends PriorityQueue<Entry<K, Object>> {

	protected Heap(Comparator<Entry<K, Object>> comparator) {
		super(comparator);
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
    public List<Set<T>> popAll() {
		List<Set<T>> list = new LinkedList<>();

		for (var entry = poll(); entry != null; entry = poll()) {
			list.add((Set<T>) entry.getValue());
		}

		return list;
	}
}
