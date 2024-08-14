package com.wgbtree.tree.wgb.model.node.black;

import com.wgbtree.tree.wgb.calculator.MersenneCalculator;
import com.wgbtree.tree.wgb.model.node.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

import static com.wgbtree.tree.wgb.prime.Primes.mersenneFromExp;

@EqualsAndHashCode(callSuper = false)
@Getter
public class MersenneBlack<K extends Comparable<K>, T> extends Black<K, T> implements Serializable {
	private final int power;

	public MersenneBlack(int order, int power, boolean allowMergingOnSameKey) {
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
