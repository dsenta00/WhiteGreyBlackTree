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

	public static void testUuidInsert() {
		var tree = new WhiteGreyBlackTreeMap<String, String>(5);

		tree.put("97d25990-f187-49f0-9c63-d2a8cca8df5c", "97d25990-f187-49f0-9c63-d2a8cca8df5c");
		assertEqual("97d25990-f187-49f0-9c63-d2a8cca8df5c", tree.get("97d25990-f187-49f0-9c63-d2a8cca8df5c"));
		tree.put("c922febb-3f9e-4b8c-bb54-c72be8f2c1b5", "c922febb-3f9e-4b8c-bb54-c72be8f2c1b5");
		assertEqual("c922febb-3f9e-4b8c-bb54-c72be8f2c1b5", tree.get("c922febb-3f9e-4b8c-bb54-c72be8f2c1b5"));
		tree.put("c8853a0e-8ddb-4087-b7e7-2a40b9dce205", "c8853a0e-8ddb-4087-b7e7-2a40b9dce205");
		assertEqual("c8853a0e-8ddb-4087-b7e7-2a40b9dce205", tree.get("c8853a0e-8ddb-4087-b7e7-2a40b9dce205"));
		tree.put("7c8176a1-eab3-4eaf-a585-cfbcb24fb092", "7c8176a1-eab3-4eaf-a585-cfbcb24fb092");
		assertEqual("7c8176a1-eab3-4eaf-a585-cfbcb24fb092", tree.get("7c8176a1-eab3-4eaf-a585-cfbcb24fb092"));
		tree.put("e5b5a05e-de22-467d-8a76-cb2ffda97c82", "e5b5a05e-de22-467d-8a76-cb2ffda97c82");
		assertEqual("e5b5a05e-de22-467d-8a76-cb2ffda97c82", tree.get("e5b5a05e-de22-467d-8a76-cb2ffda97c82"));
		tree.put("333e343a-28f1-494f-ae68-856087d3c310", "333e343a-28f1-494f-ae68-856087d3c310");
		assertEqual("333e343a-28f1-494f-ae68-856087d3c310", tree.get("333e343a-28f1-494f-ae68-856087d3c310"));
		tree.put("8c498fa1-8602-4685-8080-56e95e93cf51", "8c498fa1-8602-4685-8080-56e95e93cf51");
		assertEqual("8c498fa1-8602-4685-8080-56e95e93cf51", tree.get("8c498fa1-8602-4685-8080-56e95e93cf51"));
		tree.put("fec9d528-df3f-4a23-a595-e86485699613", "fec9d528-df3f-4a23-a595-e86485699613");
		assertEqual("fec9d528-df3f-4a23-a595-e86485699613", tree.get("fec9d528-df3f-4a23-a595-e86485699613"));
		tree.put("3cc69b04-fdc0-42a7-8dca-3ee92122943b", "3cc69b04-fdc0-42a7-8dca-3ee92122943b");
		assertEqual("3cc69b04-fdc0-42a7-8dca-3ee92122943b", tree.get("3cc69b04-fdc0-42a7-8dca-3ee92122943b"));
		tree.put("b9dea8b8-ab92-4e67-ad2b-802b349a4551", "b9dea8b8-ab92-4e67-ad2b-802b349a4551");
		assertEqual("b9dea8b8-ab92-4e67-ad2b-802b349a4551", tree.get("b9dea8b8-ab92-4e67-ad2b-802b349a4551"));
		tree.put("0e9fbf5a-1703-4d06-b21d-3874b064fd31", "0e9fbf5a-1703-4d06-b21d-3874b064fd31");
		assertEqual("0e9fbf5a-1703-4d06-b21d-3874b064fd31", tree.get("0e9fbf5a-1703-4d06-b21d-3874b064fd31"));
		tree.put("8624317e-0491-4f4e-ba3f-38591de0b6e0", "8624317e-0491-4f4e-ba3f-38591de0b6e0");
		assertEqual("8624317e-0491-4f4e-ba3f-38591de0b6e0", tree.get("8624317e-0491-4f4e-ba3f-38591de0b6e0"));
		tree.put("bedadb50-fac8-472d-bd00-94af4af948f9", "bedadb50-fac8-472d-bd00-94af4af948f9");
		assertEqual("bedadb50-fac8-472d-bd00-94af4af948f9", tree.get("bedadb50-fac8-472d-bd00-94af4af948f9"));
		tree.put("9ed5e3a5-e78c-4a40-bbc4-c8027d977157", "9ed5e3a5-e78c-4a40-bbc4-c8027d977157");
		assertEqual("9ed5e3a5-e78c-4a40-bbc4-c8027d977157", tree.get("9ed5e3a5-e78c-4a40-bbc4-c8027d977157"));
		tree.put("9006459b-ca61-4b1d-bd5d-4302cfa60c16", "9006459b-ca61-4b1d-bd5d-4302cfa60c16");
		assertEqual("9006459b-ca61-4b1d-bd5d-4302cfa60c16", tree.get("9006459b-ca61-4b1d-bd5d-4302cfa60c16"));
		tree.put("b1fc1c7e-dbcf-4991-b2ef-70910ff86493", "b1fc1c7e-dbcf-4991-b2ef-70910ff86493");
		assertEqual("b1fc1c7e-dbcf-4991-b2ef-70910ff86493", tree.get("b1fc1c7e-dbcf-4991-b2ef-70910ff86493"));
		tree.put("fcc2f5cc-98af-4a9d-90e3-eb7bfa9999bc", "fcc2f5cc-98af-4a9d-90e3-eb7bfa9999bc");
		assertEqual("fcc2f5cc-98af-4a9d-90e3-eb7bfa9999bc", tree.get("fcc2f5cc-98af-4a9d-90e3-eb7bfa9999bc"));
		tree.put("51808e89-de69-4d49-a8d6-4d6ad6742cfe", "51808e89-de69-4d49-a8d6-4d6ad6742cfe");
		assertEqual("51808e89-de69-4d49-a8d6-4d6ad6742cfe", tree.get("51808e89-de69-4d49-a8d6-4d6ad6742cfe"));
		tree.put("7140134a-eca2-4fb7-9f81-63ffe87a2954", "7140134a-eca2-4fb7-9f81-63ffe87a2954");
		assertEqual("7140134a-eca2-4fb7-9f81-63ffe87a2954", tree.get("7140134a-eca2-4fb7-9f81-63ffe87a2954"));
		tree.put("fb6926f5-6d60-4102-87c0-fb866467932c", "fb6926f5-6d60-4102-87c0-fb866467932c");
		assertEqual("fb6926f5-6d60-4102-87c0-fb866467932c", tree.get("fb6926f5-6d60-4102-87c0-fb866467932c"));
		tree.put("cd4cfad2-ee42-48a6-a340-15439a8ce8e7", "cd4cfad2-ee42-48a6-a340-15439a8ce8e7");
		assertEqual("cd4cfad2-ee42-48a6-a340-15439a8ce8e7", tree.get("cd4cfad2-ee42-48a6-a340-15439a8ce8e7"));
		tree.put("ba6f3659-e0c6-4ffb-aa1c-5ea57edddf89", "ba6f3659-e0c6-4ffb-aa1c-5ea57edddf89");
		assertEqual("ba6f3659-e0c6-4ffb-aa1c-5ea57edddf89", tree.get("ba6f3659-e0c6-4ffb-aa1c-5ea57edddf89"));
		tree.put("5ef48527-de21-4ff1-9467-5ad946b5f826", "5ef48527-de21-4ff1-9467-5ad946b5f826");
		assertEqual("5ef48527-de21-4ff1-9467-5ad946b5f826", tree.get("5ef48527-de21-4ff1-9467-5ad946b5f826"));
		tree.put("ad1f4ac4-6360-4050-8d3d-83bc94998860", "ad1f4ac4-6360-4050-8d3d-83bc94998860");
		assertEqual("ad1f4ac4-6360-4050-8d3d-83bc94998860", tree.get("ad1f4ac4-6360-4050-8d3d-83bc94998860"));
		tree.put("c6996691-61f0-49ac-be51-ca100d389b64", "c6996691-61f0-49ac-be51-ca100d389b64");
		assertEqual("c6996691-61f0-49ac-be51-ca100d389b64", tree.get("c6996691-61f0-49ac-be51-ca100d389b64"));
		tree.put("29eed7b1-01b0-4220-9426-97a1c3b4e166", "29eed7b1-01b0-4220-9426-97a1c3b4e166");
		assertEqual("29eed7b1-01b0-4220-9426-97a1c3b4e166", tree.get("29eed7b1-01b0-4220-9426-97a1c3b4e166"));
		tree.put("dba69f37-9f1e-4d8c-8142-7833593c8c06", "dba69f37-9f1e-4d8c-8142-7833593c8c06");
		assertEqual("dba69f37-9f1e-4d8c-8142-7833593c8c06", tree.get("dba69f37-9f1e-4d8c-8142-7833593c8c06"));
		tree.put("54ba11fc-ea78-4afd-abe4-f01be7a700da", "54ba11fc-ea78-4afd-abe4-f01be7a700da");
		assertEqual("54ba11fc-ea78-4afd-abe4-f01be7a700da", tree.get("54ba11fc-ea78-4afd-abe4-f01be7a700da"));
		tree.put("e0330e53-39b5-47a5-a7b6-3751e962b9be", "e0330e53-39b5-47a5-a7b6-3751e962b9be");
		assertEqual("e0330e53-39b5-47a5-a7b6-3751e962b9be", tree.get("e0330e53-39b5-47a5-a7b6-3751e962b9be"));
		tree.put("067dd359-8684-4574-a7ec-c30b50fa9a69", "067dd359-8684-4574-a7ec-c30b50fa9a69");
		assertEqual("067dd359-8684-4574-a7ec-c30b50fa9a69", tree.get("067dd359-8684-4574-a7ec-c30b50fa9a69"));
		tree.put("3225820c-0a51-4c6a-aaff-55616f957429", "3225820c-0a51-4c6a-aaff-55616f957429");
		assertEqual("3225820c-0a51-4c6a-aaff-55616f957429", tree.get("3225820c-0a51-4c6a-aaff-55616f957429"));
		tree.put("5ffac3b6-4585-4a8c-821f-ca5f1ddde832", "5ffac3b6-4585-4a8c-821f-ca5f1ddde832");
		assertEqual("5ffac3b6-4585-4a8c-821f-ca5f1ddde832", tree.get("5ffac3b6-4585-4a8c-821f-ca5f1ddde832"));
		tree.put("826a35c9-c950-40ac-9683-238d90846bbc", "826a35c9-c950-40ac-9683-238d90846bbc");
		assertEqual("826a35c9-c950-40ac-9683-238d90846bbc", tree.get("826a35c9-c950-40ac-9683-238d90846bbc"));
		tree.put("f3e6e5f4-72f4-4bd5-b13f-8d3c7022ad26", "f3e6e5f4-72f4-4bd5-b13f-8d3c7022ad26");
		assertEqual("f3e6e5f4-72f4-4bd5-b13f-8d3c7022ad26", tree.get("f3e6e5f4-72f4-4bd5-b13f-8d3c7022ad26"));
		tree.put("09f47805-9a2b-47b9-a286-81f0066fee7b", "09f47805-9a2b-47b9-a286-81f0066fee7b");
		assertEqual("09f47805-9a2b-47b9-a286-81f0066fee7b", tree.get("09f47805-9a2b-47b9-a286-81f0066fee7b"));
		tree.put("150f717a-af77-4721-be4d-3ae1a4934ffd", "150f717a-af77-4721-be4d-3ae1a4934ffd");
		assertEqual("150f717a-af77-4721-be4d-3ae1a4934ffd", tree.get("150f717a-af77-4721-be4d-3ae1a4934ffd"));
		tree.put("35d76d95-49dd-464d-a22e-0c397bff84e5", "35d76d95-49dd-464d-a22e-0c397bff84e5");
		assertEqual("35d76d95-49dd-464d-a22e-0c397bff84e5", tree.get("35d76d95-49dd-464d-a22e-0c397bff84e5"));
		tree.put("6747046a-a1cf-4d1f-9df8-c0a05bf1a4b6", "6747046a-a1cf-4d1f-9df8-c0a05bf1a4b6");
		assertEqual("6747046a-a1cf-4d1f-9df8-c0a05bf1a4b6", tree.get("6747046a-a1cf-4d1f-9df8-c0a05bf1a4b6"));
		tree.put("b51b9ceb-9a00-47f1-b2e8-0bec0f12e2fb", "b51b9ceb-9a00-47f1-b2e8-0bec0f12e2fb");
		assertEqual("b51b9ceb-9a00-47f1-b2e8-0bec0f12e2fb", tree.get("b51b9ceb-9a00-47f1-b2e8-0bec0f12e2fb"));
		tree.put("80108540-0564-43eb-84a9-be17d10259cc", "80108540-0564-43eb-84a9-be17d10259cc");
		assertEqual("80108540-0564-43eb-84a9-be17d10259cc", tree.get("80108540-0564-43eb-84a9-be17d10259cc"));
		tree.put("56a34c65-5a87-4ffc-ab0f-15d51d32810a", "56a34c65-5a87-4ffc-ab0f-15d51d32810a");
		assertEqual("56a34c65-5a87-4ffc-ab0f-15d51d32810a", tree.get("56a34c65-5a87-4ffc-ab0f-15d51d32810a"));
		tree.put("d10d6be1-24de-4ff3-9a05-d89ca1f97e05", "d10d6be1-24de-4ff3-9a05-d89ca1f97e05");
		assertEqual("d10d6be1-24de-4ff3-9a05-d89ca1f97e05", tree.get("d10d6be1-24de-4ff3-9a05-d89ca1f97e05"));
		tree.put("a54cc360-b0ac-4c32-8fae-891288dee767", "a54cc360-b0ac-4c32-8fae-891288dee767");
		assertEqual("a54cc360-b0ac-4c32-8fae-891288dee767", tree.get("a54cc360-b0ac-4c32-8fae-891288dee767"));
		tree.put("39431dca-7b9b-4548-9130-c6a96f5a8e5e", "39431dca-7b9b-4548-9130-c6a96f5a8e5e");
		assertEqual("39431dca-7b9b-4548-9130-c6a96f5a8e5e", tree.get("39431dca-7b9b-4548-9130-c6a96f5a8e5e"));

		assertEqual(44, tree.size());
	}

	public static List<String> testRandomInsert() {
		var tree = new WhiteGreyBlackTreeMap<String, Integer>(5);
		var list = new LinkedList<String>();

		for (int i = 0; i < 1000; i++) {
			String key = UUID.randomUUID().toString();
			list.add(key);
			try {
				tree.put(key, i);
			} catch (Exception e) {
				return list;
			}
		}
		assertEqual(1000, tree.size());
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