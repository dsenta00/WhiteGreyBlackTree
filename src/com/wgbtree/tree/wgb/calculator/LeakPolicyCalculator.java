package com.wgbtree.tree.wgb.calculator;

import com.wgbtree.tree.wgb.constants.LeakPolicy;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class LeakPolicyCalculator {

	public static LeakPolicy calculate(LeakPolicy oldLeakPolicy, int countLeft, int countRight, int tolerableDiff) {
		int diff = Math.abs(countLeft - countRight);
		return switch (oldLeakPolicy) {
			case SMALLEST -> (diff > tolerableDiff && countLeft > countRight) ? LeakPolicy.LARGEST : LeakPolicy.SMALLEST;
			case LARGEST -> (diff > tolerableDiff && countRight > countLeft) ? LeakPolicy.SMALLEST : LeakPolicy.LARGEST;
		};
	}
}
