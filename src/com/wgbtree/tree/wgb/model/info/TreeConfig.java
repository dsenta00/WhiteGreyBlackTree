package com.wgbtree.tree.wgb.model.info;

import com.wgbtree.tree.wgb.creator.TreeConfigCreator;
import com.wgbtree.tree.wgb.prime.Primes;
import lombok.Builder;
import lombok.Getter;

import static com.wgbtree.tree.wgb.prime.Primes.nextPrime;
import static com.wgbtree.tree.wgb.prime.Primes.prevPrime;

@Builder
@Getter
public class TreeConfig {
	private Integer order;
	private Integer rank;
	private Boolean duplicatesAllowed;
	private GrowthMode growthMode;
	private Boolean balanced;

	public static TreeConfig of(int order, int rank, boolean duplicatesAllowed, GrowthMode growthMode, boolean balanced) {
		return TreeConfigCreator.create(order, rank, duplicatesAllowed, growthMode, balanced);
	}

	public TreeConfig nextRank() {
		return switch (growthMode) {
			case ACCELERATING -> TreeConfig.of(order, nextPrime(rank), duplicatesAllowed, growthMode, balanced);
			case DECELERATING -> TreeConfig.of(order, prevPrime(rank), duplicatesAllowed, growthMode, balanced);
			case STRAIGHT -> this;
		};
	}

	public boolean shouldHaveGreyChildren() {
		return switch (growthMode) {
			case ACCELERATING -> false;
			case DECELERATING -> rank <= Primes.FIRST_PRIME;
			case STRAIGHT -> true;
		};
	}

	public String name() {
		String balanced = this.balanced ? "balanced" : "unbalanced";
		String duplicatesAllowed = this.duplicatesAllowed ? "duplicatesAllowed" : "noDuplicatesAllowed";
		String growthMode = this.growthMode.name().toLowerCase();
		String rank = this.rank.toString();
		String order = this.order.toString();
		return "wgb[" + growthMode + "][" + order + "][" + rank + "][" + duplicatesAllowed + "][" + balanced + "]";
	}
}
