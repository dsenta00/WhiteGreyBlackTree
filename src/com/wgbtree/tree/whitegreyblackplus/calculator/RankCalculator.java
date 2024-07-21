package com.wgbtree.tree.whitegreyblackplus.calculator;

import com.wgbtree.tree.whitegreyblackplus.prime.Primes;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RankCalculator {

	public static int calculateGreatestRank(int order, int effectiveCapacity) {
		int prevCapacity = 1;
		int capacity = 3;
		int depth = 2;
		int rank = Primes.FIRST_PRIME;

		while (capacity * order < effectiveCapacity) {
			int multiplier;

			if (depth % 2 == 0) {
				multiplier = rank;
			} else {
				multiplier = 2;
				rank = Primes.nextPrime(rank);
			}

			int result = capacity + (capacity - prevCapacity) * multiplier;
			prevCapacity = capacity;
			capacity = result;

			depth++;
		}

		return rank;
	}
}
