package com.wgbtree.tree.wgb.model.node.white;

import com.wgbtree.tree.wgb.calculator.MersenneCalculator;
import com.wgbtree.tree.wgb.model.node.Node;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import static com.wgbtree.tree.wgb.prime.Primes.mersenneFromExp;

@EqualsAndHashCode(callSuper = true)
public class MersenneWhite<K extends Comparable<K>, T> extends White<K, T> implements Serializable {
	private final int power;

	public MersenneWhite(int order, int power, boolean allowMergingOnSameKey) {
		super(order, mersenneFromExp(power), allowMergingOnSameKey);
		this.power = power;
	}

	@Override
	public Node<K, T> nextNode(K key, int keyHash) {
		if (greys == null) {
			return null;
		}

		int index = MersenneCalculator.mod(keyHash, capacity, power);
		return greys[index];
	}
}
