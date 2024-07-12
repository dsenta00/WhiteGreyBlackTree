package com.wgbtree.tree.whitegreyblackplus.entries;

public class EntriesListNonMergeableDescLargest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListNonMergeableDescLargest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableDescLargest(EntriesList<K, T> entriesList) {
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
		return LeakPolicy.LARGEST;
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.SMALLEST ? new EntriesListNonMergeableDescSmallest<>(this) : this;
	}
}
