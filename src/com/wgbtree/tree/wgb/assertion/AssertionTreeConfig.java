package com.wgbtree.tree.wgb.assertion;

import lombok.NoArgsConstructor;

import static com.wgbtree.tree.wgb.prime.Primes.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AssertionTreeConfig {

	public static int assertOrder(int order) {
		if (order <= 0) {
			throw new IllegalArgumentException("Order must be greater than 0, got " + order + " instead.");
		}

		return order;
	}

	public static int assertPower(int power) {
		if (power <= 0) {
			throw new IllegalArgumentException("Power must be greater than 0, got " + power + " instead.");
		}

		//
		// Let the number of nodes be limited by
		//
		//     n = 2^p - 1
		//
		// where p is the power.
		//
		if (power > 13) {
			throw new IllegalArgumentException("Power must be less than or equal to 10, got " + power + " instead.");
		}

		return power;
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

	public static int assertMersenneExp(int exp) {
		if (exp < FIRST_MERSENNE_EXP) {
			throw new IllegalArgumentException("Exponent must be greater at least " + FIRST_MERSENNE_EXP + ", got " + exp + " instead.");
		}

		if (isNotMersennePrime(mersenneFromExp(exp))) {
			throw new IllegalArgumentException("Exponent must be a Mersenne prime number, got " + exp + " instead.");
		}

		return exp;
	}
}
