package com.wgbtree.tree.whitegreyblackplus.prime;

public class Primes {

	public static final int FIRST_PRIME = 2;

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

	public static int nextPrime(int number) {
		switch (number) {
			case 0:
			case 1:
				return 2;
			case 2:
				return 3;
			case 3:
			case 4:
				return 5;
			case 5:
			case 6:
				return 7;
			case 7:
			case 8:
			case 9:
			case 10:
				return 11;
			case 11:
			case 12:
				return 13;
			case 13:
			case 14:
			case 15:
			case 16:
				return 17;
			case 17:
			case 18:
				return 19;
			case 19:
			case 20:
			case 21:
			case 22:
				return 23;
			default:
				for (int i = number + 1; ; i++) {
					if (isPrime(i)) {
						return i;
					}
				}
		}
	}

	public static int prevPrime(int number) {
		if (number < FIRST_PRIME) {
			return FIRST_PRIME;
		}

		switch (number) {
			case 2:
			case 3:
			  return 2;
			case 4:
			case 5:
			  return 3;
			case 6:
			case 7:
			  return 5;
			case 8:
			case 9:
			case 10:
			case 11:
			  return 7;
			case 12:
			case 13:
			  return 11;
			case 14:
			case 15:
			case 16:
			case 17:
			  return 13;
			case 18:
			case 19:
			  return 17;
			case 20:
			case 21:
			case 22:
			case 23:
			  return 19;
			default:
				for (int i = number - 1; ; i--) {
					if (isPrime(i)) {
						return i;
					}
				}
		}
	}
}
