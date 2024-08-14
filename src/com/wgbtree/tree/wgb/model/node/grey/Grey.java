package com.wgbtree.tree.wgb.model.node.grey;

import com.wgbtree.tree.wgb.calculator.LeakPolicyCalculator;
import com.wgbtree.tree.wgb.constants.Direction;
import com.wgbtree.tree.wgb.constants.LeakPolicy;
import com.wgbtree.tree.wgb.entries.EntriesListMergeableAscLargest;
import com.wgbtree.tree.wgb.entries.EntriesListNonMergeableAscLargest;
import com.wgbtree.tree.wgb.model.node.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class Grey<K extends Comparable<K>, T> extends Node<K, T> implements Serializable {

	private int countLeft = 0;
	private int countRight = 0;
	private Node<K, T> left;
	private Node<K, T> right;

	public Grey(int order, boolean allowMergingOnSameKey) {
		super(allowMergingOnSameKey ? new EntriesListMergeableAscLargest<>(order) : new EntriesListNonMergeableAscLargest<>(order));
	}

	/**
	 * Calculate the leak policy of the entries list.
	 */
	public LeakPolicy setLeakPolicy() {
		var leakPolicy = LeakPolicyCalculator.calculate(entries.getPolicy(), countLeft, countRight, entries.getCapacity());
		entries = entries.convert(leakPolicy);
		return leakPolicy;
	}

	public Direction getMorePopulatedDirection() {
		if (countLeft == countRight) {
			return Direction.NONE;
		}

		return countLeft > countRight ? Direction.LEFT : Direction.RIGHT;
	}

	public void incCountLeft() {
		countLeft++;
	}

	public void incCountRight() {
		countRight++;
	}

	public void decCountLeft() {
		countLeft--;
	}

	public void decCountRight() {
		countRight--;
	}

	@Override
	public Node<K, T> nextNode(K key, int keyHash) {
		if (key == null || key.compareTo(entries.firstEntry().getKey()) < 0) {
			return left;
		} else {
			return right;
		}
	}
}
