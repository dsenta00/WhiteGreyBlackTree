package com.wgbtree.tree.test;

import com.wgbtree.tree.AsTree;
import com.wgbtree.tree.bplus.BPlusTreeMap;
import com.wgbtree.tree.redblack.TreeMapAsTree;
import com.wgbtree.tree.wgb.*;
import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.White;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import com.wgbtree.tree.wgb.operations.delete.acc.BlackRemoverAcc;
import com.wgbtree.tree.wgb.operations.delete.dec.BlackRemoverDec;
import com.wgbtree.tree.wgb.operations.delete.dec.GreyRemoverDec;
import com.wgbtree.tree.wgb.operations.delete.mersenne.BlackRemoverMersenne;
import com.wgbtree.tree.wgb.operations.delete.mersenne.GreyRemoverMersenne;
import com.wgbtree.tree.wgb.operations.delete.power.BlackRemoverPower;
import com.wgbtree.tree.wgb.operations.delete.straight.GreyRemoverStraight;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.IntStream;

public class WhiteGreyBlackTreeMapTest extends Test {

	final static int TOTAL_WEIGHT = 1_000_000;
	final static int TEST_CAPACITY = 6000;
	final static int TEST_ORDER = 5;

	public static void main(String[] args) {

		testGetBetweenAsc(new TreeMapAsTree<>());
		testGetBetweenAsc(new BPlusTreeMap<>(5));
		testGetBetweenAsc(new MersenneAccWgbTreeMap<>(TEST_ORDER, 5, true));
		testGetBetweenAsc(new MersenneDecWgbTreeMap<>(TEST_ORDER, 5, true));
		testGetBetweenAsc(new AccWGBTreeMap<>(TEST_ORDER, 2, true));
		testGetBetweenAsc(new StraightWGBTreeMap<>(TEST_ORDER, true));
		testGetBetweenAsc(new WGBPowerTreeMap<>(TEST_ORDER, 8, true));
		testGetBetweenAsc(new DecWGBTreeMap<>(25, TEST_ORDER, false, true));

		testRemoveMinFromBlack(BlackRemoverPower::removeMin);
		testRemoveMinFromBlack(BlackRemoverAcc::removeMin);
		testRemoveMinFromBlack(BlackRemoverDec::removeMin);
		testRemoveMinFromBlack(BlackRemoverMersenne::removeMin);

		testRemoveMin(GreyRemoverDec::removeMin, false);
		testRemoveMin(GreyRemoverDec::removeMin, true);
		testRemoveMin(GreyRemoverStraight::removeMin, true);
		testRemoveMin(GreyRemoverMersenne::removeMin, false);
		testRemoveMin(GreyRemoverMersenne::removeMin, true);

		testRemoveMax(GreyRemoverDec::removeMax, false);
		testRemoveMax(GreyRemoverDec::removeMax, true);
		testRemoveMax(GreyRemoverStraight::removeMax, true);
		testRemoveMax(GreyRemoverMersenne::removeMax, false);
		testRemoveMax(GreyRemoverMersenne::removeMax, true);

		testInsert(new MersenneAccWgbTreeMap<>(TEST_ORDER, 5, true));
		testInsert(new MersenneDecWgbTreeMap<>(TEST_ORDER, 5, true));
		testInsert(new AccWGBTreeMap<>(TEST_ORDER, 2, true));
		testInsert(new StraightWGBTreeMap<>(TEST_ORDER, true));
		testInsert(new WGBPowerTreeMap<>(TEST_ORDER, 8, true));
		testInsert(new DecWGBTreeMap<>(25, TEST_ORDER, false, true));

		testInsertBalance(new AccWGBTreeMap<>(TEST_ORDER, 2, true));
		testInsertBalance(new DecWGBTreeMap<>(TEST_CAPACITY, TEST_ORDER, false, true));
		testInsertBalance(new StraightWGBTreeMap<>(TEST_ORDER, true));
		testInsertBalance(new WGBPowerTreeMap<>(TEST_ORDER, 8, true));

		testPopulation(new MersenneAccWgbTreeMap<>());
		testPopulation(new MersenneDecWgbTreeMap<>());
		testPopulation(new AccWGBTreeMap<>());
		testPopulation(new AccWGBTreeMap<>(3, 2, true, true));
		testPopulation(new DecWGBTreeMap<>(TEST_CAPACITY));
		testPopulation(new StraightWGBTreeMap<>());
		testPopulation(new WGBPowerTreeMap<>(TEST_ORDER, 8));
	}

	private static void testGetBetweenAsc(AsTree<String, Integer> tree) {
		tree.put("a", 1);
		tree.put("b", 2);
		tree.put("c", 3);
		tree.put("d", 4);
		tree.put("e", 5);
		tree.put("f", 6);
		tree.put("g", 7);
		tree.put("h", 8);
		tree.put("i", 9);
		tree.put("j", 10);
		tree.put("k", 11);
		tree.put("l", 12);
		tree.put("m", 13);
		tree.put("n", 14);
		tree.put("o", 15);
		tree.put("p", 16);
		tree.put("q", 17);
		tree.put("r", 18);
		tree.put("s", 19);
		tree.put("t", 20);
		tree.put("u", 21);
		tree.put("v", 22);
		tree.put("w", 23);
		tree.put("x", 24);
		tree.put("y", 25);
		tree.put("z", 26);

		final List<Set<Integer>> list = tree.getBetweenAsc("d", "j");
		System.out.println(tree.getName() + " getBetweenAsc(\"d\", \"j\") -> " + list);
		assertEquals(6, list.size());
		IntStream.range(0, 6).forEach(i -> assertEquals(i + 4, list.get(i).stream().findFirst().orElse(null)));

		final List<Set<Integer>> list2 = tree.getBetweenAsc("a", "z");
		System.out.println(tree.getName() + " getBetweenAsc(\"a\", \"z\") -> " + list2);
		assertEquals(25, list2.size());
		IntStream.range(0, 25).forEach(i -> assertEquals(i + 1, list2.get(i).stream().findFirst().orElse(null)));
	}

	private static void testPopulation(WGBTreeMap<String, Integer> tree) {
		IntStream.range(0, TOTAL_WEIGHT / TEST_CAPACITY)
				.mapToObj(i -> testRandomInsert(tree, TEST_CAPACITY))
				.min(Comparator.comparingInt(e -> e.getKey().size()))
				.ifPresent(e -> {
					for (var uuid : e.getKey()) {
						System.out.println("tree.put(\"" + uuid + "\", \"" + uuid + "\");");
						System.out.println("assertEqual(\"" + uuid + "\", tree.get(\"" + uuid + "\"));");
					}
					System.out.println("size: " + e.getKey().size());
					System.out.println("average time: " + e.getValue());
				});
	}

	public static void testRemoveMinFromBlack(Function<Black<String, Boolean>, RemoveResult<String, Boolean>> removeMax) {
		var black = new Black<String, Boolean>(3, 17, false);
		var leakEntry = new AtomicReference<Entry<String, Set<Boolean>>>();

		black.getEntries().add(new SimpleEntry<>(UUID.randomUUID().toString(), Set.of(true)), leakEntry);
		black.getEntries().add(new SimpleEntry<>(UUID.randomUUID().toString(), Set.of(true)), leakEntry);
		black.getEntries().add(new SimpleEntry<>(UUID.randomUUID().toString(), Set.of(true)), leakEntry);

		var result = removeMax.apply(black);
		assertEquals(true, result.getEntry() != null);

		result = removeMax.apply(black);
		assertEquals(true, result.getEntry() != null);

		result = removeMax.apply(black);
		assertEquals(true, result.getEntry() != null);

		result = removeMax.apply(black);
		assertEquals(true, result.getEntry() == null);
	}

	public static void testRemoveMax(Function<Grey<Integer, Integer>, RemoveResult<Integer, Integer>> removeMax, boolean greys) {
		var leakEntry = new AtomicReference<Entry<Integer, Set<Integer>>>();

		var grey = new Grey<Integer, Integer>(3, false);
		grey.getEntries().add(new SimpleEntry<>(4, Set.of(4)), leakEntry);
		grey.getEntries().add(new SimpleEntry<>(5, Set.of(5)), leakEntry);
		grey.getEntries().add(new SimpleEntry<>(6, Set.of(6)), leakEntry);

		var white = greys ? new Grey<Integer, Integer>(3, false) : new White<Integer, Integer>(3, 2, false);
		white.getEntries().add(new SimpleEntry<>(1, Set.of(1)), leakEntry);
		white.getEntries().add(new SimpleEntry<>(2, Set.of(2)), leakEntry);
		white.getEntries().add(new SimpleEntry<>(3, Set.of(3)), leakEntry);

		var black = greys ? new Grey<Integer, Integer>(3, false) : new Black<Integer, Integer>(3, 2, false);
		black.getEntries().add(new SimpleEntry<>(7, Set.of(7)), leakEntry);
		black.getEntries().add(new SimpleEntry<>(8, Set.of(8)), leakEntry);
		black.getEntries().add(new SimpleEntry<>(9, Set.of(9)), leakEntry);

		grey.setLeft(white);
		grey.setCountLeft(3);
		grey.setRight(black);
		grey.setCountRight(3);

		for (int i = 9; i > 0; i--) {
			var result = removeMax.apply(grey);
			assertEquals(i, result.getEntry().getKey());
			grey = (Grey<Integer, Integer>) result.getNode();
		}
	}

	public static void testRemoveMin(Function<Grey<Integer, Integer>, RemoveResult<Integer, Integer>> removeMin, boolean greys) {
		var leakEntry = new AtomicReference<Entry<Integer, Set<Integer>>>();

		var grey = new Grey<Integer, Integer>(3, false);
		grey.getEntries().add(new SimpleEntry<>(4, Set.of(4)), leakEntry);
		grey.getEntries().add(new SimpleEntry<>(5, Set.of(5)), leakEntry);
		grey.getEntries().add(new SimpleEntry<>(6, Set.of(6)), leakEntry);

		var left = greys ? new Grey<Integer, Integer>(3, false) : new White<Integer, Integer>(3, 2, false);
		left.getEntries().add(new SimpleEntry<>(1, Set.of(1)), leakEntry);
		left.getEntries().add(new SimpleEntry<>(2, Set.of(2)), leakEntry);
		left.getEntries().add(new SimpleEntry<>(3, Set.of(3)), leakEntry);

		var right = greys ? new Grey<Integer, Integer>(3, false) : new Black<Integer, Integer>(3, 2, false);
		right.getEntries().add(new SimpleEntry<>(7, Set.of(7)), leakEntry);
		right.getEntries().add(new SimpleEntry<>(8, Set.of(8)), leakEntry);
		right.getEntries().add(new SimpleEntry<>(9, Set.of(9)), leakEntry);

		grey.setLeft(left);
		grey.setCountLeft(3);
		grey.setRight(right);
		grey.setCountRight(3);

		for (int i = 1; i < 10; i++) {
			var result = removeMin.apply(grey);
			assertEquals(i, result.getEntry().getKey());
			grey = (Grey<Integer, Integer>) result.getNode();
		}
	}

	public static Entry<List<String>, Double> testRandomInsert(WGBTreeMap<String, Integer> tree, int size) {
		var list = new LinkedList<String>();
		long time = 0;

		for (int i = 0; i < size; i++) {
			String key = UUID.randomUUID().toString();
			list.add(key);
			try {
				int finalI = i;
				time += measureTime(() -> tree.put(key, finalI));
			} catch (Exception e) {
				tree.clear();
				return Map.entry(list, i > 0 ? (double) time / i : 0.0);
			}
		}

		assertEquals(size, tree.size());
		tree.clear();
		return Map.entry(list, size > 0 ? (double) time / size : 0.0);
	}

	public static void testInsertBalance(WGBTreeMap<Integer, Integer> tree) {
		IntStream.range(0, 1000).forEach(i -> tree.put(i, i));

		var grey = tree.getGrey();
		assertEquals(498, grey.getCountLeft());
		assertEquals(497, grey.getCountRight());
	}

	public static void testInsert(WGBTreeMap<Integer, Integer> tree) {

		tree.put(1, 1);
		tree.put(2, 2);
		tree.put(3, 3);
		tree.put(4, 4);
		tree.put(5, 5);

		assertEquals(5, tree.size());
		IntStream.range(1, 6).forEach(i -> assertEquals(i, tree.get(i)));
		assertEquals(null, tree.get(6));
		assertEquals(1, tree.getMin());
		assertEquals(5, tree.getMax());

		tree.put(6, 6);
		tree.put(7, 7);
		tree.put(8, 8);
		tree.put(9, 9);
		tree.put(10, 10);

		assertEquals(10, tree.size());
		IntStream.range(1, 11).forEach(i -> assertEquals(i, tree.get(i)));
		assertEquals(null, tree.get(11));
		assertEquals(1, tree.getMin());
		assertEquals(10, tree.getMax());

		tree.put(11, 11);

		var grey = tree.getGrey();
		assertEquals(1, grey.getCountLeft());
		assertEquals(5, grey.getCountRight());

		assertEquals(11, tree.size());
		IntStream.range(1, 12).forEach(i -> assertEquals(i, tree.get(i)));
		assertEquals(null, tree.get(12));
		assertEquals(1, tree.getMin());
		assertEquals(11, tree.getMax());

		tree.put(12, 12);
		tree.put(13, 13);
		tree.put(14, 14);
		tree.put(15, 15);
		tree.put(16, 16);

		grey = tree.getGrey();
		assertEquals(6, grey.getCountLeft());
		assertEquals(5, grey.getCountRight());

		assertEquals(16, tree.size());
		IntStream.range(1, 17).forEach(i -> assertEquals(i, tree.get(i)));
		assertEquals(null, tree.get(17));
		assertEquals(1, tree.getMin());
		assertEquals(16, tree.getMax());

		tree.put(17, 17);
		tree.put(18, 18);
		tree.put(19, 19);
		tree.put(20, 20);
		tree.put(21, 21);
		tree.put(22, 22);
		tree.put(23, 23);
		tree.put(24, 24);
		tree.put(25, 25);

		assertEquals(25, tree.size());
		IntStream.range(1, 26).forEach(i -> assertEquals(i, tree.get(i)));
		assertEquals(null, tree.get(26));
		assertEquals(1, tree.getMin());
		assertEquals(25, tree.getMax());
	}
}