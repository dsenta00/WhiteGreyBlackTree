package com.wgbtree.tree.whitegreyblack.node.util;

public final class Prime {

    private Prime() {
    }

    public static boolean isPrime(int n) {
        // Corner cases
        if (n <= 1) return false;
        if (n <= 3) return true;

        if (n % 2 == 0 || n % 3 == 0) return false;

        for (int i = 5; i * i <= n; i = i + 6)
            if (n % i == 0 || n % (i + 2) == 0)
                return false;

        return true;
    }

    public static int nextPrime(int n) {
        if (n <= 1)
            return 2;

        if (PrimeConstants.NEXT_PRIME.containsKey(n)) {
            return PrimeConstants.NEXT_PRIME.get(n);
        }

        int prime = n;

        do {
            prime++;
        } while (!isPrime(prime));

        return prime;
    }
}