package com.wgbtree.tree.test;

import lombok.NoArgsConstructor;

import static com.wgbtree.tree.wgb.calculator.LeakPolicyCalculator.calculate;
import static com.wgbtree.tree.wgb.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.wgb.constants.LeakPolicy.SMALLEST;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class LeakPolicyCalculatorTest extends Test {

	public static void main(String[] args) {
		testCalculate();
	}


	public static void testCalculate() {
		assertEquals(LARGEST, calculate(LARGEST, 0, 0, 5));
		assertEquals(LARGEST, calculate(LARGEST, 0, 1, 5));
		assertEquals(LARGEST, calculate(LARGEST, 0, 2, 5));
		assertEquals(LARGEST, calculate(LARGEST, 0, 3, 5));
		assertEquals(LARGEST, calculate(LARGEST, 0, 4, 5));
		assertEquals(LARGEST, calculate(LARGEST, 0, 5, 5));

		assertEquals(SMALLEST, calculate(LARGEST, 0, 6, 5)); // breakpoint - start to go left

		assertEquals(SMALLEST, calculate(SMALLEST, 1, 5, 5));
		assertEquals(SMALLEST, calculate(SMALLEST, 2, 5, 5));
		assertEquals(SMALLEST, calculate(SMALLEST, 3, 5, 5));
		assertEquals(SMALLEST, calculate(SMALLEST, 4, 5, 5));
		assertEquals(SMALLEST, calculate(SMALLEST, 5, 5, 5));
		assertEquals(SMALLEST, calculate(SMALLEST, 6, 5, 5));
		assertEquals(SMALLEST, calculate(SMALLEST, 7, 5, 5));
		assertEquals(SMALLEST, calculate(SMALLEST, 8, 5, 5));
		assertEquals(SMALLEST, calculate(SMALLEST, 9, 5, 5));
		assertEquals(SMALLEST, calculate(SMALLEST, 10, 5, 5));

		assertEquals(LARGEST, calculate(SMALLEST, 11, 5, 5)); // breakpoint - start to go right

		assertEquals(LARGEST, calculate(LARGEST, 10, 6, 5));
		assertEquals(LARGEST, calculate(LARGEST, 10, 7, 5));
		assertEquals(LARGEST, calculate(LARGEST, 10, 8, 5));
		assertEquals(LARGEST, calculate(LARGEST, 10, 9, 5));
		assertEquals(LARGEST, calculate(LARGEST, 10, 10, 5));
		assertEquals(LARGEST, calculate(LARGEST, 10, 11, 5));
		assertEquals(LARGEST, calculate(LARGEST, 10, 12, 5));
		assertEquals(LARGEST, calculate(LARGEST, 10, 13, 5));
		assertEquals(LARGEST, calculate(LARGEST, 10, 14, 5));
		assertEquals(LARGEST, calculate(LARGEST, 10, 15, 5));

		assertEquals(SMALLEST, calculate(LARGEST, 10, 16, 5)); // breakpoint - start to go left
	}
}