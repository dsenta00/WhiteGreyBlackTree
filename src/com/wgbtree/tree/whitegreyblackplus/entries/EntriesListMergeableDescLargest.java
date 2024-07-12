package com.wgbtree.tree.whitegreyblackplus.entries;

public class EntriesListMergeableDescLargest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListMergeableDescLargest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListMergeableDescLargest(EntriesList<K, T> entriesList) {
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
		return LeakPolicy.LARGEST;
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.SMALLEST ? new EntriesListMergeableDescSmallest<>(this) : this;
	}
}
