package com.wgbtree.tree.wgb.constants;

import com.wgbtree.tree.wgb.prime.Primes;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Constants {
	public static final int DEFAULT_RANK = Primes.FIRST_PRIME;
	public static final int DEFAULT_ORDER = 100;
	public static final int DEFAULT_POWER = 2;
	public static final boolean DEFAULT_ARE_DUPLICATES_ALLOWED = true;
	public static final boolean DEFAULT_IS_BALANCED = false;
}
