package com.wgbtree.tree.wgb.creator;

import com.wgbtree.tree.wgb.model.info.GrowthMode;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.prime.Primes;
import lombok.NoArgsConstructor;

import static com.wgbtree.tree.wgb.model.info.GrowthMode.POWER;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class TreeConfigCreator {

	public static TreeConfig create(int order, int rank, boolean duplicatesAllowed, GrowthMode growthMode, boolean balanced) {
		return TreeConfig.builder()
				.order(order)
				.rank(rank)
				.duplicatesAllowed(duplicatesAllowed)
				.growthMode(growthMode)
				.balanced(balanced)
				.build();
	}

	public static TreeConfig createMersenne(int order, int power, boolean duplicatesAllowed, GrowthMode growthMode, boolean balanced) {
		return TreeConfig.builder()
				.order(order)
				.power(power)
				.rank(Primes.mersenneFromExp(power))
				.duplicatesAllowed(duplicatesAllowed)
				.growthMode(growthMode)
				.balanced(balanced)
				.build();
	}

	public static TreeConfig createPower(int order, int power, boolean duplicatesAllowed, boolean balanced) {
		return TreeConfig.builder()
				.order(order)
				.power(power)
				.rank((int) Math.pow(2, power))
				.duplicatesAllowed(duplicatesAllowed)
				.growthMode(POWER)
				.balanced(balanced)
				.build();
	}
}
