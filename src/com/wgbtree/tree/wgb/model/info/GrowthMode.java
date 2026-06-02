package com.wgbtree.tree.wgb.model.info;

public enum GrowthMode {

	/**
	 * The tree grows by increasing the rank of the nodes.
	 */
	ACCELERATING,

	/**
	 * The tree grows by increasing the rank of the nodes, but the rank is a Mersenne prime.
	 */
	MERSENNE_ACCELERATING,

	/**
	 * The tree grows by increasing the rank of the nodes, but the rank is a Mersenne prime.
	 */
	FW_MERSENNE_ACCELERATING,

	/**
	 * The tree grows by decreasing the rank of the nodes.
	 */
	DECELERATING,

	/**
	 * The tree grows by decreasing the rank of the nodes, but the rank is a Mersenne prime.
	 */
	MERSENNE_DECELERATING,

	/**
	 * The tree grows by decreasing the rank of the nodes, but the rank is a Mersenne prime.
	 */
	FW_MERSENNE_DECELERATING,

	/**
	 * The tree grows by having no ranks. All nodes are of the grey type.
	 */
	STRAIGHT,

	/**
	 * Same as {@link #ACCELERATING}, but the first rank is a power of 2 instead of a prime number.
	 * The rank is calculated as 2^p, where p is the power.
	 * That helps to avoid modulo operations by using bitwise AND.
	 */
	POWER,

	/**
	 * Tree with exchanging 1 and 2 ranks. Whites and Black can have only one Grey node.
	 * Grey node contains White and Black
	 */
	RANGE
}
