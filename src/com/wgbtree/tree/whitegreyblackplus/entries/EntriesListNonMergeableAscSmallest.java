package com.wgbtree.tree.whitegreyblackplus.entries;

public class EntriesListNonMergeableAscSmallest<K extends Comparable<K>, T> extends EntriesList<K, T> {
	public EntriesListNonMergeableAscSmallest(int capacityLimit) {
		super(capacityLimit);
	}

	protected EntriesListNonMergeableAscSmallest(EntriesList<K, T> entriesList) {
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
		return LeakPolicy.SMALLEST;
	}

	@Override
	public EntriesList<K, T> setPolicy(LeakPolicy leakPolicy) {
		return leakPolicy == LeakPolicy.LARGEST ? new EntriesListNonMergeableAscLargest<>(this) : this;
	}
}
