package com.wgbtree.tree.whitegreyblackplus.node;

import com.wgbtree.tree.whitegreyblackplus.creator.LevelInfoCreator;
import com.wgbtree.tree.whitegreyblackplus.prime.Primes;
import lombok.Builder;
import lombok.Getter;

import static com.wgbtree.tree.whitegreyblackplus.prime.Primes.nextPrime;
import static com.wgbtree.tree.whitegreyblackplus.prime.Primes.prevPrime;

@Builder
@Getter
public class LevelInfo {
	private int order;
	private int rank;
	private boolean duplicatesAllowed;
	private boolean decreasingRank;

	public static LevelInfo of(int order, int rank, boolean allowDuplicates, boolean decreasingPrimes) {
		return LevelInfoCreator.create(order, rank, allowDuplicates, decreasingPrimes);
	}

	public static LevelInfo of(int order, int effectiveCapacity, boolean allowDuplicates) {
		return LevelInfoCreator.create(order, effectiveCapacity, allowDuplicates);
	}

	public LevelInfo nextLevel() {
		int newRank = decreasingRank ? prevPrime(rank) : nextPrime(rank);
		return LevelInfo.of(order, newRank, duplicatesAllowed, decreasingRank);
	}

	public boolean shouldHaveGreyChildren() {
		if (decreasingRank) {
			return rank <= Primes.FIRST_PRIME;
		}

		return false;
	}
}
