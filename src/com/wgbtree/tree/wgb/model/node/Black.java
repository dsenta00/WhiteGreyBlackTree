package com.wgbtree.tree.wgb.model.node;

import com.wgbtree.tree.wgb.entries.EntriesListMergeableDescSmallest;
import com.wgbtree.tree.wgb.entries.EntriesListNonMergeableDescSmallest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class Black<K extends Comparable<K>, T> extends Node<K, T> implements Serializable {
	private int capacity;
	private Grey<K, T>[] greys;

	public Black(int order, int capacity, boolean allowMergingOnSameKey) {
		super(allowMergingOnSameKey ? new EntriesListMergeableDescSmallest<>(order) : new EntriesListNonMergeableDescSmallest<>(order));
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
