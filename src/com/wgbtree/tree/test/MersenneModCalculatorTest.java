package com.wgbtree.tree.test;

import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wgbtree.tree.Main.measureTime;
import static com.wgbtree.tree.wgb.calculator.MersenneCalculator.mod;

@NoArgsConstructor
public final class MersenneModCalculatorTest extends Test {

	public static void main(String[] args) {
		testMersenne(3, 2);
		testMersenne(7, 3);
		testMersenne(31, 5);
		testMersenne(127, 7);
		testMersenne(8191, 13);
	}

	private static void testMersenne(int p, int e) {
		long timeMersenne = 0;
		long timeMod = 0;
		AtomicInteger resultMers = new AtomicInteger();
		AtomicInteger resultMod = new AtomicInteger();

		for (int i = 0; i < 1000; i++) {
			int randomHash = UUID.randomUUID().hashCode();
			long time = measureTime(() -> resultMers.set(mod(randomHash, p, e)));
			timeMersenne += time;

			time = measureTime(() -> resultMod.set(Math.abs(randomHash) % p));
			timeMod += time;

			assertEquals(resultMers.get(), resultMod.get());
		}

		System.out.println();
		System.out.println("After " + p);
		System.out.println("Mersenne ... " + timeMersenne);
		System.out.println("Mod ........ " + timeMod);
	}
}
