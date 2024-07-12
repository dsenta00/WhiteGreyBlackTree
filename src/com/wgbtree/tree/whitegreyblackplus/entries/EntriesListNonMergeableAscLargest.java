package com.wgbtree.tree.whitegreyblackplus.entries;

public class EntriesListNonMergeableAscLargest<K extends Comparable<K>, T> extends EntriesList<K, T> {

	public EntriesListNonMergeableAscLargest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableAscLargest(EntriesList<K, T> entriesList) {
		super(entriesList);
	}

	@Override
	protected boolean allowMergingOnSameKey() {
		return false;
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
		return leakPolicy == LeakPolicy.SMALLEST ? new EntriesListNonMergeableAscSmallest<>(this) : this;
	}
}
