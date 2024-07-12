package com.wgbtree.tree.whitegreyblackplus.entries;

public class EntriesListMergeableAscSmallest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListMergeableAscSmallest(int capacityLimit) {
		super(capacityLimit);
	}

	public EntriesListMergeableAscSmallest(EntriesList<K, T> entries) {
		super(entries);
	}

	@Override
	protected boolean allowMergingOnSameKey() {
		return true;
	}

	@Override
	public Order order() {
		return Order.ASCENDING;
	}

	@Override
	public LeakPolicy leakPolicy() {
		return LeakPolicy.SMALLEST;
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.LARGEST ? new EntriesListMergeableAscLargest<>(this) : this;
	}
}
