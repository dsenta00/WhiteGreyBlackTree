package com.wgbtree.tree.whitegreyblackplus.entries;

public class EntriesListNonMergeableDescSmallest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListNonMergeableDescSmallest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableDescSmallest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	protected boolean allowMergingOnSameKey() {
		return false;
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
		return leakPolicy == LeakPolicy.LARGEST ? new EntriesListNonMergeableDescLargest<>(this) : this;
	}
}
