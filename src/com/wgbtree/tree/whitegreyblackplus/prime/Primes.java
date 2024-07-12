package com.wgbtree.tree.whitegreyblackplus.prime;

public class Primes {

	public static boolean isPrime(int number) {
		if (number < 2) {
			return false;
		}

		if (number == 2 || number == 3) {
			return true;
		}

		if (number % 2 == 0 || number % 3 == 0) {
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
}
