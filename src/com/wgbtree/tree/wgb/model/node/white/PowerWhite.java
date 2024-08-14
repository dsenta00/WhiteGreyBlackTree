package com.wgbtree.tree.wgb.model.node.white;

import com.wgbtree.tree.wgb.model.node.Node;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertPower;

@EqualsAndHashCode(callSuper = true)
public class PowerWhite<K extends Comparable<K>, T> extends White<K, T> implements Serializable {

	public PowerWhite(int order, int power, boolean allowMergingOnSameKey) {
		super(order, (int) Math.pow(2, assertPower(power)), allowMergingOnSameKey);
	}

	@Override
	public Node<K, T> nextNode(K key, int keyHash) {
		if (greys == null) {
			return null;
		}

		int index = keyHash & (capacity - 1);
		return greys[index];
	}
}
