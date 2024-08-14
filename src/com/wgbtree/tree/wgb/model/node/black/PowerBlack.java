package com.wgbtree.tree.wgb.model.node.black;

import com.wgbtree.tree.wgb.model.node.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

import static com.wgbtree.tree.wgb.assertion.AssertionTreeConfig.assertPower;

@EqualsAndHashCode(callSuper = false)
@Getter
public class PowerBlack<K extends Comparable<K>, T> extends Black<K, T> implements Serializable {

	public PowerBlack(int order, int power, boolean allowMergingOnSameKey) {
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
