package com.wgbtree.tree.wgb.prime;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Primes {

	public static final int FIRST_PRIME = 2;
	public static final int FIRST_MERSENNE_EXP = 2;
	public static final int FIRST_MERSENNE_PRIME = 3;
	public static final int MAX_MERSENNE_PRIME_FOR_NODE = 8191;

	/**
	 * Returns whether the given number is a Mersenne prime number.
	 *
	 * @param number the number
	 * @return whether the given number is a Mersenne prime number
	 */
	public static boolean isNotMersennePrime(int number) {
		return !switch (number) {
			case 3, 7, 31, 127, 8191, 131071, 524287, 2147483647 -> true;
			default -> false;
		};
	}

	/**
	 * Returns the Mersenne prime number for the given exponent.
	 *
	 * @param exp the exponent
	 * @return the Mersenne prime number for the given exponent
	 */
	public static int mersenneFromExp(int exp) {
		return switch (exp) {
			case 2 -> 3;
			case 3 -> 7;
			case 5 -> 31;
			case 7 -> 127;
			case 13 -> 8191;
			case 17 -> 131071;
			case 19 -> 524287;
			case 31 -> 2147483647;
			default -> throw new IllegalArgumentException("No Mersenne prime found for " + exp);
		};
	}

	/**
	 * Returns the exponent of the Mersenne prime number for the given number.
	 *
	 * @param number the number
	 * @return the exponent of the Mersenne prime number for the given number
	 */
	public static int mersenneExp(int number) {
		return switch (number) {
			case 3 -> 2;
			case 7 -> 3;
			case 31 -> 5;
			case 127 -> 7;
			case 8191 -> 13;
			case 131071 -> 17;
			case 524287 -> 19;
			case 2147483647 -> 31;
			default -> throw new IllegalArgumentException("No Mersenne prime found for " + number);
		};
	}

	/**
	 * Returns the next Mersenne exponent after the given exponent.
	 *
	 * @param exp the exponent
	 * @return the next Mersenne exponent after the given exponent
	 */
	public static int nextMersenneExp(int exp) {
		return switch (exp) {
			case 2 -> 3;
			case 3 -> 5;
			case 5 -> 7;
			case 7 -> 13;
			case 13 -> 17;
			case 17 -> 19;
			case 19 -> 31;
			default -> throw new IllegalArgumentException("No Mersenne prime found for " + exp);
		};
	}

	/**
	 * Returns the previous Mersenne exponent before the given exponent.
	 *
	 * @param exp the exponent
	 * @return the previous Mersenne exponent before the given exponent
	 */
	public static int prevMersenneExp(int exp) {
		return switch (exp) {
			case 3 -> 2;
			case 5 -> 3;
			case 7 -> 5;
			case 13 -> 7;
			case 17 -> 13;
			case 19 -> 17;
			case 31 -> 19;
			default -> throw new IllegalArgumentException("No Mersenne prime found for " + exp);
		};
	}

	/**
	 * Returns whether the given number is a prime number.
	 *
	 * @param number the number
	 * @return whether the given number is a prime number
	 */
	public static boolean isPrime(int number) {
		if (number < FIRST_PRIME) {
			return false;
		}

		if (number == FIRST_PRIME || number == 3) {
			return true;
		}

		if (number % FIRST_PRIME == 0 || number % 3 == 0) {
			return false;
		}

		for (int i = 5; i * i <= number; i += 6) {
			if (number % i == 0 || number % (i + 2) == 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns the next prime number after the given number.
	 *
	 * @param number the number
	 * @return the next prime number after the given number
	 */
	public static int nextPrime(int number) {
		return switch (number) {
			case 0, 1 -> 2;
			case 2 -> 3;
			case 3, 4 -> 5;
			case 5, 6 -> 7;
			case 7, 8, 9, 10 -> 11;
			case 11, 12 -> 13;
			case 13, 14, 15, 16 -> 17;
			case 17, 18 -> 19;
			case 19, 20, 21, 22 -> 23;
			case 23, 24, 25, 26, 27, 28 -> 29;
			case 29, 30 -> 31;
			default -> {
				for (int i = number + 1; ; i++) {
					if (isPrime(i)) {
						yield i;
					}
				}
			}
		};
	}

	/**
	 * Returns the previous prime number before the given number.
	 *
	 * @param number the number
	 * @return the previous prime number before the given number
	 */
	public static int prevPrime(int number) {
		if (number < FIRST_PRIME) {
			return FIRST_PRIME;
		}

		return switch (number) {
			case 2, 3 -> 2;
			case 4, 5 -> 3;
			case 6, 7 -> 5;
			case 8, 9, 10, 11 -> 7;
			case 12, 13 -> 11;
			case 14, 15, 16, 17 -> 13;
			case 18, 19 -> 17;
			case 20, 21, 22, 23 -> 19;
			default -> {
				for (int i = number - 1; ; i--) {
					if (isPrime(i)) {
						yield i;
					}
				}
			}
		};
	}
}
