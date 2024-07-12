package com.wgbtree.tree.whitegreyblackplus.node;

import com.wgbtree.tree.whitegreyblackplus.entries.EntriesList;
import lombok.Data;

@Data
public abstract class Node<K extends Comparable<K>, T> {

	protected EntriesList<K, T> entries;

	protected Node(EntriesList<K, T> entries) {
		this.entries = entries;
	}

	protected Node() {
		this.entries = null;
	}

	public abstract Node<K, T> nextNode(K key, int keyHash);
}
