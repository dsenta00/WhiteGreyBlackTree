package com.wgbtree.tree.test;

import com.wgbtree.tree.wgb.calculator.RankCalculator;

public class RankCalculatorTest extends Test {

	public static void main(String[] args) {
		testGreatestRank();
		testGreatestCapacity();
	}

	public static void testGreatestRank() {
		assertEqual(2, RankCalculator.calculateGreatestRank(1, 1));
		assertEqual(2, RankCalculator.calculateGreatestRank(1, 14));
		assertEqual(2, RankCalculator.calculateGreatestRank(1, 15));
		assertEqual(3, RankCalculator.calculateGreatestRank(1, 16));
		assertEqual(3, RankCalculator.calculateGreatestRank(1, 87));
		assertEqual(5, RankCalculator.calculateGreatestRank(1, 88));
		assertEqual(5, RankCalculator.calculateGreatestRank(1, 807));
		assertEqual(7, RankCalculator.calculateGreatestRank(1, 808));
		assertEqual(7, RankCalculator.calculateGreatestRank(1, 10887));
	}

	public static void testGreatestCapacity() {
		assertEqual(15, RankCalculator.calculateGreatestCapacity(1, 2));
		assertEqual(87, RankCalculator.calculateGreatestCapacity(1, 3));
		assertEqual(807, RankCalculator.calculateGreatestCapacity(1, 5));
		assertEqual(10887, RankCalculator.calculateGreatestCapacity(1, 7));
	}
}
