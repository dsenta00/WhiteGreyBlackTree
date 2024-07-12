package com.wgbtree.tree.heap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public abstract class HeapTree<K extends Comparable<K>, T> {

	HeapNode<K, T> root;

	public abstract void push(Entry<K, Set<T>> entry);
	public abstract Entry<K, Set<T>> pop();

	public List<Set<T>> popAll() {
		List<Set<T>> list = new LinkedList<>();

		while (true) {
			Entry<K, Set<T>> entry = pop();
			if (entry == null) {
				break;
			}
			list.add(entry.getValue());
		}

		return list;
	}
}
