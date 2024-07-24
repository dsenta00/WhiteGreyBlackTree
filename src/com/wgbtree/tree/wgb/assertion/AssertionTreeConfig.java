package com.wgbtree.tree.wgb.assertion;

import lombok.NoArgsConstructor;

import static com.wgbtree.tree.wgb.prime.Primes.FIRST_PRIME;
import static com.wgbtree.tree.wgb.prime.Primes.isPrime;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AssertionTreeConfig {

	public static int assertOrder(int order) {
		if (order <= 0) {
			throw new IllegalArgumentException("Order must be greater than 0, got " + order + " instead.");
		}

		return order;
	}

	public static int assertRank(int rank) {
		if (rank < FIRST_PRIME) {
			throw new IllegalArgumentException("Rank must be greater at least " + FIRST_PRIME + ", got " + rank + " instead.");
		}

		if (!isPrime(rank)) {
			throw new IllegalArgumentException("Rank must be a prime number, got " + rank + " instead.");
		}

		return rank;
	}


}
