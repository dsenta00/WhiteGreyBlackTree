package com.wgbtree.tree.wgb.calculator;

import com.wgbtree.tree.wgb.prime.Primes;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RankCalculator {

	public static int calculateGreatestCapacity(int order, int rank) {
		int prevCapacity = 1;
		int capacity = 3;
		int depth = 2;
		int prime = Primes.FIRST_PRIME;

		while (prime <= rank) {
			int multiplier;

			if (depth % 2 == 0) {
				multiplier = prime;
			} else {
				multiplier = 2;
				prime = Primes.nextPrime(prime);
			}

			int result = capacity + (capacity - prevCapacity) * multiplier;
			prevCapacity = capacity;
			capacity = result;

			depth++;
		}

		return capacity * order;
	}

	public static int calculateGreatestRank(int order, int effectiveCapacity) {
		for (int rank = Primes.FIRST_PRIME; ; rank = Primes.nextPrime(rank)) {
			if (calculateGreatestCapacity(order, rank) >= effectiveCapacity) {
				return rank;
			}
		}
	}
}
