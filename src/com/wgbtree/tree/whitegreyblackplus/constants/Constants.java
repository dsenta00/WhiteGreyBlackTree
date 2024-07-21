package com.wgbtree.tree.whitegreyblackplus.constants;

import com.wgbtree.tree.whitegreyblackplus.prime.Primes;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Constants {
	public static final int DEFAULT_RANK = Primes.FIRST_PRIME;
	public static final int DEFAULT_ORDER = 10;
	public static final boolean DEFAULT_ALLOW_DUPLICATES = false;
	public static final boolean DEFAULT_DECREASING_PRIMES = false;
	public static final int LINEAR_SEARCH_THRESHOLD = 16;
}
