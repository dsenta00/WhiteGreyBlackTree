package com.wgbtree.tree.whitegreyblackplus.entries;

public class EntriesListMergeableAscLargest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListMergeableAscLargest(int capacityLimit) {
		super(capacityLimit);
	}

	public EntriesListMergeableAscLargest(EntriesList<K, T> entriesList) {
		super(entriesList);
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
		return LeakPolicy.LARGEST;
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.SMALLEST ? new EntriesListMergeableAscSmallest<>(this) : this;
	}
}
