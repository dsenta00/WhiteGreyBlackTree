package com.wgbtree.tree.whitegreyblackplus.creator;

import com.wgbtree.tree.whitegreyblackplus.node.LevelInfo;
import lombok.NoArgsConstructor;

import static com.wgbtree.tree.whitegreyblackplus.calculator.RankCalculator.calculateGreatestRank;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class LevelInfoCreator {

	public static LevelInfo create(int order, int rank, boolean allowDuplicates, boolean decreasingPrimes) {
		return LevelInfo.builder()
				.order(order)
				.rank(rank)
				.duplicatesAllowed(allowDuplicates)
				.decreasingRank(decreasingPrimes)
				.build();
	}

	public static LevelInfo create(int order, int effectiveCapacity, boolean allowDuplicates) {
		return LevelInfo.builder()
				.order(order)
				.rank(calculateGreatestRank(order, effectiveCapacity))
				.duplicatesAllowed(allowDuplicates)
				.decreasingRank(true)
				.build();
	}
}
