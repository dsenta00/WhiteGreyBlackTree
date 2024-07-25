package com.wgbtree.tree.test;

public abstract class Test {

	static void assertEquals(Object expected, Object actual) {
		if (expected == null && actual == null) {
		} else if (expected == null || actual == null) {
			throw new AssertionError("Test failed, Expected: " + toString(expected) + ", Actual: " + toString(actual));
		} else if (!expected.equals(actual)) {
			throw new AssertionError("Test failed, Expected: " + expected + ", Actual: " + actual);
		}
	}

	static String toString(Object obj) {
		return obj == null ? "null" : obj.toString();
	}
}
