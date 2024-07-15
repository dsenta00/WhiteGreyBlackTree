package com.wgbtree.tree.whitegreyblackplus.node;

import com.wgbtree.tree.whitegreyblackplus.constants.Direction;
import com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy;
import com.wgbtree.tree.whitegreyblackplus.entries.EntriesListMergeableAscLargest;
import com.wgbtree.tree.whitegreyblackplus.entries.EntriesListNonMergeableAscLargest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class GNode<K extends Comparable<K>, T> extends Node<K, T> implements Serializable {

	private int countLeft = 0;
	private int countRight = 0;
	private WNode<K, T> left;
	private BNode<K, T> right;

	public GNode(int order, boolean allowMergingOnSameKey) {
		super(allowMergingOnSameKey ? new EntriesListMergeableAscLargest<>(order) : new EntriesListNonMergeableAscLargest<>(order));
	}

	/**
	 * Calculate the leak policy of the entries list.
	 */
	public LeakPolicy setLeakPolicy() {
		int countDiff = Math.abs(countLeft - countRight);
		var leakPolicy = (countDiff > entries.getCapacityLimit() && countLeft < countRight) ? LeakPolicy.SMALLEST : LeakPolicy.LARGEST;
		entries = entries.setPolicy(leakPolicy);
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
