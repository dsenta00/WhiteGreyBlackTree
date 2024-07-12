package com.wgbtree.tree.whitegreyblackplus.node;

import com.wgbtree.tree.whitegreyblackplus.entries.EntriesListMergeableAscLargest;
import com.wgbtree.tree.whitegreyblackplus.entries.EntriesListNonMergeableAscLargest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class WNode<K extends Comparable<K>, T> extends Node<K, T> implements Serializable {
	private int capacity;
	private GNode<K, T>[] gNodes;

	public WNode(int order, int capacity, boolean allowMergingOnSameKey) {
		super(allowMergingOnSameKey ? new EntriesListMergeableAscLargest<>(order) : new EntriesListNonMergeableAscLargest<>(order));
		setCapacity(capacity);
	}

	public GNode<K, T>[] getGNodes() {
		if (gNodes == null) {
			gNodes = new GNode[capacity];
		}
		return gNodes;
	}

	@Override
	public Node<K, T> nextNode(K key, int keyHash) {
		if (gNodes == null) {
			return null;
		}

		int index = Math.abs(keyHash) % gNodes.length;
		return gNodes[index];
	}
}
