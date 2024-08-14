package com.wgbtree.tree.wgb.model.node.white;

import com.wgbtree.tree.wgb.entries.EntriesListMergeableAscLargest;
import com.wgbtree.tree.wgb.entries.EntriesListNonMergeableAscLargest;
import com.wgbtree.tree.wgb.model.node.Node;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Getter
public class White<K extends Comparable<K>, T> extends Node<K, T> implements Serializable {
	protected final int capacity;
	protected Grey<K, T>[] greys;

	public White(int order, int capacity, boolean allowMergingOnSameKey) {
		super(allowMergingOnSameKey ? new EntriesListMergeableAscLargest<>(order) : new EntriesListNonMergeableAscLargest<>(order));
		this.capacity = capacity;
	}

	public Grey<K, T>[] getGreys() {
		if (greys == null) {
			greys = new Grey[capacity];
		}
		return greys;
	}

	@Override
	public Node<K, T> nextNode(K key, int keyHash) {
		if (greys == null) {
			return null;
		}

		int index = Math.abs(keyHash) % greys.length;
		return greys[index];
	}
}
