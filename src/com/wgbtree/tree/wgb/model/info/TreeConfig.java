package com.wgbtree.tree.wgb.model.info;

import com.wgbtree.tree.wgb.creator.TreeConfigCreator;
import lombok.Builder;
import lombok.Getter;

import static com.wgbtree.tree.wgb.prime.Primes.*;

@Builder
@Getter
public class TreeConfig {
	private Integer order;
	private Integer power;
	private Integer rank;
	private Boolean duplicatesAllowed;
	private GrowthMode growthMode;
	private Boolean balanced;

	public static void main(String[] args) {
		for (int prime = 200; prime > 2; prime = prevPrime(prime)) {
			System.out.println(prime);
		}
	}

	public TreeConfig nextRank() {
		return switch (growthMode) {
			case ACCELERATING, POWER -> TreeConfigCreator.create(order, nextPrime(rank), duplicatesAllowed, growthMode, balanced);
			case MERSENNE_ACCELERATING -> TreeConfigCreator.createMersenne(order, nextMersenneExp(power), duplicatesAllowed, growthMode, balanced);
			case DECELERATING -> TreeConfigCreator.create(order, prevPrime(rank), duplicatesAllowed, growthMode, balanced);
			case MERSENNE_DECELERATING -> TreeConfigCreator.createMersenne(order, prevMersenneExp(power), duplicatesAllowed, growthMode, balanced);
			case STRAIGHT -> this;
		};
	}

	public boolean shouldHaveGreyChildren() {
		return switch (growthMode) {
			case ACCELERATING, POWER -> false;
			case MERSENNE_ACCELERATING -> rank > MAX_MERSENNE_PRIME_FOR_NODE;
			case DECELERATING -> rank <= FIRST_PRIME;
			case MERSENNE_DECELERATING -> rank <= FIRST_MERSENNE_PRIME;
			case STRAIGHT -> true;
		};
	}

	public String name() {
		String balanced = this.balanced ? "bal" : "unbal";
		String growthMode = switch (this.growthMode) {
			case ACCELERATING -> "acc";
			case MERSENNE_ACCELERATING -> "mers_acc";
			case DECELERATING -> "dec";
			case MERSENNE_DECELERATING -> "mers_dec";
			case POWER -> "pow";
			case STRAIGHT -> "str";
		};
		String rank = this.rank.toString();
		String order = this.order.toString();
		return "wgb[g-" + growthMode + "][o-" + order + "][r-" + rank + "][b-" + balanced + "]";
	}
}
