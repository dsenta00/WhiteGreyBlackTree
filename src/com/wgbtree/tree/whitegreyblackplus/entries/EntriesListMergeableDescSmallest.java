package com.wgbtree.tree.whitegreyblackplus.entries;

public class EntriesListMergeableDescSmallest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListMergeableDescSmallest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListMergeableDescSmallest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	protected boolean allowMergingOnSameKey() {
		return true;
	}

	@Override
	public Order order() {
		return Order.DESCENDING;
	}

	@Override
	public LeakPolicy leakPolicy() {
		return LeakPolicy.SMALLEST;
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.LARGEST ? new EntriesListMergeableDescLargest<>(this) : this;
	}
}
