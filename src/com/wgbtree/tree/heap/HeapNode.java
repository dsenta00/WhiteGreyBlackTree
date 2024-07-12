package com.wgbtree.tree.heap;

import lombok.Data;

import java.util.Map.Entry;
import java.util.Set;

@Data
public class HeapNode<K extends Comparable<K>, T> {
	public Entry<K, Set<T>> entry;
	public HeapNode<K, T> left;
	public HeapNode<K, T> right;

	public HeapNode(Entry<K, Set<T>> entry) {
		this.entry = entry;
	}
}
