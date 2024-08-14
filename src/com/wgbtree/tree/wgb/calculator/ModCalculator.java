package com.wgbtree.tree.wgb.calculator;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ModCalculator {

	public static int modPow2(int number, int n) {
		return number & (n - 1);
	}
}
