package com.wgbtree.tree.wgb.model.node;

import com.wgbtree.tree.wgb.entries.EntriesList;
import lombok.Data;

@Data
public abstract class Node<K extends Comparable<K>, T> {

	protected EntriesList<K, T> entries;

	protected Node(EntriesList<K, T> entries) {
		this.entries = entries;
	}

	public abstract Node<K, T> nextNode(K key, int keyHash);
}
