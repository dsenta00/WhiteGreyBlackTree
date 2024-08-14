package com.wgbtree.tree.wgb.calculator;

import lombok.NoArgsConstructor;

import static com.wgbtree.tree.wgb.prime.Primes.mersenneExp;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class MersenneCalculator {

	public static int mod(int number, int p) {
		int exp = mersenneExp(p);
		number = Math.abs(number);
		do {
			number = (number & p) + (number >> exp);
		} while (number > p);

		return number == p ? 0 : number;
	}

	public static int mod(int number, int p, int exp) {
		number = Math.abs(number);
		do {
			number = (number & p) + (number >> exp);
		} while (number > p);

		return number == p ? 0 : number;
	}
}
