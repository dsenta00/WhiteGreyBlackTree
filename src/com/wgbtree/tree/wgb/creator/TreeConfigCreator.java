package com.wgbtree.tree.wgb.creator;

import com.wgbtree.tree.wgb.model.info.GrowthMode;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import lombok.NoArgsConstructor;

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
}
