package com.wgbtree.tree.wgb.model.node.white;

import com.wgbtree.tree.wgb.model.node.Node;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class RangeWhite<K extends Comparable<K>, T> extends White<K, T> implements Serializable {

	public RangeWhite(int order, boolean allowMergingOnSameKey) {
		super(order, 1, allowMergingOnSameKey);
	}

	@Override
	public Node<K, T> nextNode(K key, int keyHash) {
		return (greys == null) ? null : greys[0];
	}
}
