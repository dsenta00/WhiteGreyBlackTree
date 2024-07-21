package com.wgbtree.tree.whitegreyblackplus.node;

import com.wgbtree.tree.whitegreyblackplus.entries.EntriesListMergeableAscLargest;
import com.wgbtree.tree.whitegreyblackplus.entries.EntriesListNonMergeableAscLargest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class White<K extends Comparable<K>, T> extends Node<K, T> implements Serializable {
	private int capacity;
	private Grey<K, T>[] greys;

	public White(int order, int capacity, boolean allowMergingOnSameKey) {
		super(allowMergingOnSameKey ? new EntriesListMergeableAscLargest<>(order) : new EntriesListNonMergeableAscLargest<>(order));
		setCapacity(capacity);
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
