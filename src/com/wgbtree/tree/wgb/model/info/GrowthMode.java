package com.wgbtree.tree.wgb.model.info;

public enum GrowthMode {

	/**
	 * The tree grows by increasing the rank of the nodes.
	 */
	ACCELERATING,

	/**
	 * The tree grows by decreasing the rank of the nodes.
	 */
	DECELERATING,

	/**
	 * The tree grows by having no ranks. All nodes are of the grey type.
	 */
	STRAIGHT,
}
