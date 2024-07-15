package com.wgbtree.tree.test;

import com.wgbtree.tree.whitegreyblackplus.WhiteGreyBlackTreeMap;
import com.wgbtree.tree.whitegreyblackplus.node.BNode;
import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import com.wgbtree.tree.whitegreyblackplus.node.WNode;
import com.wgbtree.tree.whitegreyblackplus.operations.delete.GNodeRemover;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class WhiteGreyBlackTreeMapTest extends Test {

	public static void main(String[] args) {
		testInsert();
		testRemoveMin();
		testRemoveMax();
		testUuidInsert();
		IntStream.range(0, 100_000)
				.mapToObj(i -> testRandomInsert())
				.min(Comparator.comparingInt(List::size))
				.ifPresent(list -> {
					for (var uuid : list) {
						System.out.println("tree.put(\"" + uuid + "\", \"" + uuid + "\");");
						System.out.println("assertEqual(\"" + uuid + "\", tree.get(\"" + uuid + "\"));");
					}
					System.out.println("size: " + list.size());
				});
	}

	public static void testRemoveMax() {
		var leakEntry = new AtomicReference<Entry<Integer, Set<Integer>>>();

		var gNode = new GNode<Integer, Integer>(3, false);
		gNode.getEntries().add(new SimpleEntry<>(4, Set.of(4)), leakEntry);
		gNode.getEntries().add(new SimpleEntry<>(5, Set.of(5)), leakEntry);
		gNode.getEntries().add(new SimpleEntry<>(6, Set.of(6)), leakEntry);

		var wNode = new WNode<Integer, Integer>(3, 2, false);
		wNode.getEntries().add(new SimpleEntry<>(1, Set.of(1)), leakEntry);
		wNode.getEntries().add(new SimpleEntry<>(2, Set.of(2)), leakEntry);
		wNode.getEntries().add(new SimpleEntry<>(3, Set.of(3)), leakEntry);

		var bNode = new BNode<Integer, Integer>(3, 2, false);
		bNode.getEntries().add(new SimpleEntry<>(7, Set.of(7)), leakEntry);
		bNode.getEntries().add(new SimpleEntry<>(8, Set.of(8)), leakEntry);
		bNode.getEntries().add(new SimpleEntry<>(9, Set.of(9)), leakEntry);

		gNode.setLeft(wNode);
		gNode.setCountLeft(3);
		gNode.setRight(bNode);
		gNode.setCountRight(3);

		for (int i = 9; i > 0; i--) {
			var result = GNodeRemover.removeMax(gNode);
			assertEqual(i, result.getEntry().getKey());
			gNode = (GNode<Integer, Integer>) result.getNode();
		}
	}

	public static void testRemoveMin() {
		var leakEntry = new AtomicReference<Entry<Integer, Set<Integer>>>();

		var gNode = new GNode<Integer, Integer>(3, false);
		gNode.getEntries().add(new SimpleEntry<>(4, Set.of(4)), leakEntry);
		gNode.getEntries().add(new SimpleEntry<>(5, Set.of(5)), leakEntry);
		gNode.getEntries().add(new SimpleEntry<>(6, Set.of(6)), leakEntry);

		var wNode = new WNode<Integer, Integer>(3, 2, false);
		wNode.getEntries().add(new SimpleEntry<>(1, Set.of(1)), leakEntry);
		wNode.getEntries().add(new SimpleEntry<>(2, Set.of(2)), leakEntry);
		wNode.getEntries().add(new SimpleEntry<>(3, Set.of(3)), leakEntry);

		var bNode = new BNode<Integer, Integer>(3, 2, false);
		bNode.getEntries().add(new SimpleEntry<>(7, Set.of(7)), leakEntry);
		bNode.getEntries().add(new SimpleEntry<>(8, Set.of(8)), leakEntry);
		bNode.getEntries().add(new SimpleEntry<>(9, Set.of(9)), leakEntry);

		gNode.setLeft(wNode);
		gNode.setCountLeft(3);
		gNode.setRight(bNode);
		gNode.setCountRight(3);

		for (int i = 1; i < 10; i++) {
			var result = GNodeRemover.removeMin(gNode);
			assertEqual(i, result.getEntry().getKey());
			gNode = (GNode<Integer, Integer>) result.getNode();
		}
	}

	public static void testUuidInsert() {
		var tree = new WhiteGreyBlackTreeMap<String, String>(5);

		tree.put("17200146-fc75-4fc7-bbdb-904ad88aa96e", "17200146-fc75-4fc7-bbdb-904ad88aa96e");
		assertEqual("17200146-fc75-4fc7-bbdb-904ad88aa96e", tree.get("17200146-fc75-4fc7-bbdb-904ad88aa96e"));
		tree.put("cc037d5a-6637-4337-86ea-95043a9159ea", "cc037d5a-6637-4337-86ea-95043a9159ea");
		assertEqual("cc037d5a-6637-4337-86ea-95043a9159ea", tree.get("cc037d5a-6637-4337-86ea-95043a9159ea"));
		tree.put("3aba115d-5a68-4d5c-80db-5ecc0226020e", "3aba115d-5a68-4d5c-80db-5ecc0226020e");
		assertEqual("3aba115d-5a68-4d5c-80db-5ecc0226020e", tree.get("3aba115d-5a68-4d5c-80db-5ecc0226020e"));
		tree.put("78ab40c6-1aa4-4026-9114-fdafcc82891e", "78ab40c6-1aa4-4026-9114-fdafcc82891e");
		assertEqual("78ab40c6-1aa4-4026-9114-fdafcc82891e", tree.get("78ab40c6-1aa4-4026-9114-fdafcc82891e"));
		tree.put("d9c90462-a699-4ebc-b5b6-3a334b7dbf49", "d9c90462-a699-4ebc-b5b6-3a334b7dbf49");
		assertEqual("d9c90462-a699-4ebc-b5b6-3a334b7dbf49", tree.get("d9c90462-a699-4ebc-b5b6-3a334b7dbf49"));
		tree.put("8bf7cc0f-7c53-4fe0-9109-eb6bd7e58525", "8bf7cc0f-7c53-4fe0-9109-eb6bd7e58525");
		assertEqual("8bf7cc0f-7c53-4fe0-9109-eb6bd7e58525", tree.get("8bf7cc0f-7c53-4fe0-9109-eb6bd7e58525"));
		tree.put("12916ada-3d90-4861-b33f-033663ccb7c9", "12916ada-3d90-4861-b33f-033663ccb7c9");
		assertEqual("12916ada-3d90-4861-b33f-033663ccb7c9", tree.get("12916ada-3d90-4861-b33f-033663ccb7c9"));
		tree.put("6d8d3485-1b15-4d97-be3e-6799555f9142", "6d8d3485-1b15-4d97-be3e-6799555f9142");
		assertEqual("6d8d3485-1b15-4d97-be3e-6799555f9142", tree.get("6d8d3485-1b15-4d97-be3e-6799555f9142"));
		tree.put("b383caf6-4f8c-44b6-833b-f1caec399b44", "b383caf6-4f8c-44b6-833b-f1caec399b44");
		assertEqual("b383caf6-4f8c-44b6-833b-f1caec399b44", tree.get("b383caf6-4f8c-44b6-833b-f1caec399b44"));
		tree.put("a0482e09-c1d6-4ef0-a770-2f4ca25dd270", "a0482e09-c1d6-4ef0-a770-2f4ca25dd270");
		assertEqual("a0482e09-c1d6-4ef0-a770-2f4ca25dd270", tree.get("a0482e09-c1d6-4ef0-a770-2f4ca25dd270"));
		tree.put("3d154d5b-108b-464c-8637-a97bb429f50c", "3d154d5b-108b-464c-8637-a97bb429f50c");
		assertEqual("3d154d5b-108b-464c-8637-a97bb429f50c", tree.get("3d154d5b-108b-464c-8637-a97bb429f50c"));
		tree.put("ae3e2f71-4f98-407a-9ee1-be003c93ffc0", "ae3e2f71-4f98-407a-9ee1-be003c93ffc0");
		assertEqual("ae3e2f71-4f98-407a-9ee1-be003c93ffc0", tree.get("ae3e2f71-4f98-407a-9ee1-be003c93ffc0"));
		tree.put("f5977a81-a3e1-485e-a92e-6080ad06c7cd", "f5977a81-a3e1-485e-a92e-6080ad06c7cd");
		assertEqual("f5977a81-a3e1-485e-a92e-6080ad06c7cd", tree.get("f5977a81-a3e1-485e-a92e-6080ad06c7cd"));
		tree.put("d551b69f-3607-4baa-a3a5-fdf80808b9dd", "d551b69f-3607-4baa-a3a5-fdf80808b9dd");
		assertEqual("d551b69f-3607-4baa-a3a5-fdf80808b9dd", tree.get("d551b69f-3607-4baa-a3a5-fdf80808b9dd"));
		tree.put("8510cce1-4815-47e5-8063-f87f49e316e5", "8510cce1-4815-47e5-8063-f87f49e316e5");
		assertEqual("8510cce1-4815-47e5-8063-f87f49e316e5", tree.get("8510cce1-4815-47e5-8063-f87f49e316e5"));
		tree.put("7e1864cd-da2f-417d-809b-cdb2e51f3df1", "7e1864cd-da2f-417d-809b-cdb2e51f3df1");
		assertEqual("7e1864cd-da2f-417d-809b-cdb2e51f3df1", tree.get("7e1864cd-da2f-417d-809b-cdb2e51f3df1"));
		tree.put("9eefac71-c95d-4acc-ad67-d9c8decbeaf0", "9eefac71-c95d-4acc-ad67-d9c8decbeaf0");
		assertEqual("9eefac71-c95d-4acc-ad67-d9c8decbeaf0", tree.get("9eefac71-c95d-4acc-ad67-d9c8decbeaf0"));
		tree.put("ecb89fc8-5a0a-436a-821b-2fc001802633", "ecb89fc8-5a0a-436a-821b-2fc001802633");
		assertEqual("ecb89fc8-5a0a-436a-821b-2fc001802633", tree.get("ecb89fc8-5a0a-436a-821b-2fc001802633"));
		tree.put("63a9461d-0879-4f4f-9be7-d7041fa123da", "63a9461d-0879-4f4f-9be7-d7041fa123da");
		assertEqual("63a9461d-0879-4f4f-9be7-d7041fa123da", tree.get("63a9461d-0879-4f4f-9be7-d7041fa123da"));
		tree.put("12c82b78-7b49-414d-8d44-9a51df5cfc54", "12c82b78-7b49-414d-8d44-9a51df5cfc54");
		assertEqual("12c82b78-7b49-414d-8d44-9a51df5cfc54", tree.get("12c82b78-7b49-414d-8d44-9a51df5cfc54"));
		tree.put("d757c87c-9405-450f-85ce-0f691b01fda9", "d757c87c-9405-450f-85ce-0f691b01fda9");
		assertEqual("d757c87c-9405-450f-85ce-0f691b01fda9", tree.get("d757c87c-9405-450f-85ce-0f691b01fda9"));
		tree.put("d997cffd-3ba3-4115-a5e3-15963c8ed51a", "d997cffd-3ba3-4115-a5e3-15963c8ed51a");
		assertEqual("d997cffd-3ba3-4115-a5e3-15963c8ed51a", tree.get("d997cffd-3ba3-4115-a5e3-15963c8ed51a"));
		tree.put("b5f04c76-d7db-4a2d-a855-9d5163a9274b", "b5f04c76-d7db-4a2d-a855-9d5163a9274b");
		assertEqual("b5f04c76-d7db-4a2d-a855-9d5163a9274b", tree.get("b5f04c76-d7db-4a2d-a855-9d5163a9274b"));
		tree.put("850faaf6-bc33-4f39-8660-fb6608f3252e", "850faaf6-bc33-4f39-8660-fb6608f3252e");
		assertEqual("850faaf6-bc33-4f39-8660-fb6608f3252e", tree.get("850faaf6-bc33-4f39-8660-fb6608f3252e"));
		tree.put("b861d2cc-a11e-43b9-a075-50a98ca392d2", "b861d2cc-a11e-43b9-a075-50a98ca392d2");
		assertEqual("b861d2cc-a11e-43b9-a075-50a98ca392d2", tree.get("b861d2cc-a11e-43b9-a075-50a98ca392d2"));
		tree.put("6a6e6aea-3103-441f-b337-1135a5dea06c", "6a6e6aea-3103-441f-b337-1135a5dea06c");
		assertEqual("6a6e6aea-3103-441f-b337-1135a5dea06c", tree.get("6a6e6aea-3103-441f-b337-1135a5dea06c"));
		tree.put("4abc588b-bb56-40f4-9e3c-dc1c6bf0dc75", "4abc588b-bb56-40f4-9e3c-dc1c6bf0dc75");
		assertEqual("4abc588b-bb56-40f4-9e3c-dc1c6bf0dc75", tree.get("4abc588b-bb56-40f4-9e3c-dc1c6bf0dc75"));
		tree.put("d5fdaade-8273-47ce-9bd3-b59c8b49583b", "d5fdaade-8273-47ce-9bd3-b59c8b49583b");
		assertEqual("d5fdaade-8273-47ce-9bd3-b59c8b49583b", tree.get("d5fdaade-8273-47ce-9bd3-b59c8b49583b"));
		tree.put("6392d413-9b1b-4f38-a6c9-458aa64a6645", "6392d413-9b1b-4f38-a6c9-458aa64a6645");
		assertEqual("6392d413-9b1b-4f38-a6c9-458aa64a6645", tree.get("6392d413-9b1b-4f38-a6c9-458aa64a6645"));
		tree.put("4a291dc8-d969-46e9-b182-d59268c26eef", "4a291dc8-d969-46e9-b182-d59268c26eef");
		assertEqual("4a291dc8-d969-46e9-b182-d59268c26eef", tree.get("4a291dc8-d969-46e9-b182-d59268c26eef"));
		tree.put("98d3ef9d-4d5a-4df9-98d6-e72ee68b4a46", "98d3ef9d-4d5a-4df9-98d6-e72ee68b4a46");
		assertEqual("98d3ef9d-4d5a-4df9-98d6-e72ee68b4a46", tree.get("98d3ef9d-4d5a-4df9-98d6-e72ee68b4a46"));

		assertEqual(31, tree.size());
	}

	public static List<String> testRandomInsert() {
		var tree = new WhiteGreyBlackTreeMap<String, Integer>(5);
		var list = new LinkedList<String>();

		for (int i = 0; i < 100; i++) {
			String key = UUID.randomUUID().toString();
			list.add(key);
			try {
				tree.put(key, i);
			} catch (Exception e) {
				return list;
			}
		}
		assertEqual(100, tree.size());
		return list;
	}

	public static void testInsert() {
		var tree = new WhiteGreyBlackTreeMap<Integer, Integer>(5);

		tree.put(1, 1);
		tree.put(2, 2);
		tree.put(3, 3);
		tree.put(4, 4);
		tree.put(5, 5);

		assertEqual(5, tree.size());
		IntStream.range(1, 6).forEach(i -> assertEqual(i, tree.get(i)));
		assertEqual(null, tree.get(6));
		assertEqual(1, tree.getMin());
		assertEqual(5, tree.getMax());

		tree.put(6, 6);
		tree.put(7, 7);
		tree.put(8, 8);
		tree.put(9, 9);
		tree.put(10, 10);

		assertEqual(10, tree.size());
		IntStream.range(1, 11).forEach(i -> assertEqual(i, tree.get(i)));
		assertEqual(null, tree.get(11));
		assertEqual(1, tree.getMin());
		assertEqual(10, tree.getMax());

		tree.put(11, 11);

		assertEqual(11, tree.size());
		IntStream.range(1, 12).forEach(i -> assertEqual(i, tree.get(i)));
		assertEqual(null, tree.get(12));
		assertEqual(1, tree.getMin());
		assertEqual(11, tree.getMax());

		tree.put(12, 12);
		tree.put(13, 13);
		tree.put(14, 14);
		tree.put(15, 15);
		tree.put(16, 16);

		assertEqual(16, tree.size());
		IntStream.range(1, 17).forEach(i -> assertEqual(i, tree.get(i)));
		assertEqual(null, tree.get(17));
		assertEqual(1, tree.getMin());
		assertEqual(16, tree.getMax());

		tree.put(17, 17);
		tree.put(18, 18);
		tree.put(19, 19);
		tree.put(20, 20);
		tree.put(21, 21);
		tree.put(22, 22);
		tree.put(23, 23);
		tree.put(24, 24);
		tree.put(25, 25);

		assertEqual(25, tree.size());
		IntStream.range(1, 26).forEach(i -> assertEqual(i, tree.get(i)));
		assertEqual(null, tree.get(26));
		assertEqual(1, tree.getMin());
		assertEqual(25, tree.getMax());
	}
}