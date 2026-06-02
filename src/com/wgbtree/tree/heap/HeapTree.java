package com.wgbtree.tree.heap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public abstract class HeapTree<K extends Comparable<K>, T> {

	HeapNode<K, T> root;

	public abstract void push(Entry<K, Set<T>> entry);
	public abstract Entry<K, Set<T>> pop();

	public List<Entry<K, Set<T>>> popAll() {
		List<Entry<K, Set<T>>> list = new LinkedList<>();

		for (Entry<K, Set<T>> entry = pop(); entry != null; entry = pop()) {
			list.add(entry);
		}

		return list;
	}
}
