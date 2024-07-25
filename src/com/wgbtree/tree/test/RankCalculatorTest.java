package com.wgbtree.tree.test;

import com.wgbtree.tree.wgb.calculator.RankCalculator;

public class RankCalculatorTest extends Test {

	public static void main(String[] args) {
		testGreatestRank();
		testGreatestCapacity();
	}

	public static void testGreatestRank() {
		assertEquals(2, RankCalculator.calculateGreatestRank(1, 1));
		assertEquals(2, RankCalculator.calculateGreatestRank(1, 14));
		assertEquals(2, RankCalculator.calculateGreatestRank(1, 15));
		assertEquals(3, RankCalculator.calculateGreatestRank(1, 16));
		assertEquals(3, RankCalculator.calculateGreatestRank(1, 87));
		assertEquals(5, RankCalculator.calculateGreatestRank(1, 88));
		assertEquals(5, RankCalculator.calculateGreatestRank(1, 807));
		assertEquals(7, RankCalculator.calculateGreatestRank(1, 808));
		assertEquals(7, RankCalculator.calculateGreatestRank(1, 10887));
	}

	public static void testGreatestCapacity() {
		assertEquals(15, RankCalculator.calculateGreatestCapacity(1, 2));
		assertEquals(87, RankCalculator.calculateGreatestCapacity(1, 3));
		assertEquals(807, RankCalculator.calculateGreatestCapacity(1, 5));
		assertEquals(10887, RankCalculator.calculateGreatestCapacity(1, 7));
	}
}
