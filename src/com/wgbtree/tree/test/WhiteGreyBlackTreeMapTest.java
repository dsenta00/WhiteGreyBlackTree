package com.wgbtree.tree.test;

import com.wgbtree.tree.wgb.AccWGBTreeMap;
import com.wgbtree.tree.wgb.DecWGBTreeMap;
import com.wgbtree.tree.wgb.StraightWGBTreeMap;
import com.wgbtree.tree.wgb.WGBTreeMap;
import com.wgbtree.tree.wgb.model.node.Black;
import com.wgbtree.tree.wgb.model.node.Grey;
import com.wgbtree.tree.wgb.model.node.White;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import com.wgbtree.tree.wgb.operations.delete.asc.BlackRemoverAsc;
import com.wgbtree.tree.wgb.operations.delete.desc.BlackRemoverDesc;
import com.wgbtree.tree.wgb.operations.delete.desc.GreyRemoverDesc;
import com.wgbtree.tree.wgb.operations.delete.straight.GreyRemoverStraight;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.wgbtree.tree.Main.measureTime;

public class WhiteGreyBlackTreeMapTest extends Test {

	final static int TOTAL_WEIGHT = 1_000_000;
	final static int TEST_CAPACITY = 6000;
	final static int TEST_ORDER = 5;

	public static void main(String[] args) {
		shit();
		testRemoveMinFromBlack(BlackRemoverAsc::removeMin);
		testRemoveMinFromBlack(BlackRemoverDesc::removeMin);
		testRemoveMin(GreyRemoverDesc::removeMin, false);
		testRemoveMin(GreyRemoverStraight::removeMin, true);
		testRemoveMax(GreyRemoverDesc::removeMax, false);
		testRemoveMax(GreyRemoverStraight::removeMax, true);
		testInsert(new AccWGBTreeMap<>(TEST_ORDER));
		testInsert(new DecWGBTreeMap<>(25, TEST_ORDER));
		testInsert(new StraightWGBTreeMap<>(TEST_ORDER));
		testInsertBalance(new AccWGBTreeMap<>(TEST_ORDER));
		testInsertBalance(new DecWGBTreeMap<>(TEST_CAPACITY, TEST_ORDER));
		testInsertBalance(new StraightWGBTreeMap<>(TEST_ORDER));
		testPopulation(new AccWGBTreeMap<>());
		testPopulation(new AccWGBTreeMap<>(3, 2, true, true));
		testPopulation(new DecWGBTreeMap<>(TEST_CAPACITY));
		testPopulation(new StraightWGBTreeMap<>());
	}

	private static void shit() {
		var tree = new AccWGBTreeMap<String, String>(3, 2, true, true);
		tree.put("58be41d9-fc1e-4f91-a363-a3bf62921d62", "58be41d9-fc1e-4f91-a363-a3bf62921d62");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));

		tree.put("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", "0c5c153b-31c5-4fb0-a89c-406cdd207b7f");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));

		tree.put("e745b7f3-7c22-40c5-9911-26a3c63eafee", "e745b7f3-7c22-40c5-9911-26a3c63eafee");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));

		tree.put("46d75575-43e0-4b13-9bf9-bff2c341434e", "46d75575-43e0-4b13-9bf9-bff2c341434e");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));

		tree.put("45be1108-fe75-4ec1-b210-17aca8f37527", "45be1108-fe75-4ec1-b210-17aca8f37527");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));

		tree.put("e96bb27c-4807-4596-965a-525b46e1e172", "e96bb27c-4807-4596-965a-525b46e1e172");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));

		tree.put("67a13857-5d93-4122-9773-0d75a2053a88", "67a13857-5d93-4122-9773-0d75a2053a88");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));

		tree.put("6504ad48-e2df-4cc6-9695-4b38cd5a443b", "6504ad48-e2df-4cc6-9695-4b38cd5a443b");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));

		tree.put("bf285209-baeb-43ee-9d24-88b537f27c03", "bf285209-baeb-43ee-9d24-88b537f27c03");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));

		tree.put("1a932266-b282-4cb7-909d-833a4b677cec", "1a932266-b282-4cb7-909d-833a4b677cec");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));

		tree.put("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", "408d89c6-d2b3-49f3-a080-f1a7a43eeb4c");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));

		tree.put("973a771f-60a2-4d3d-8324-38d204e2b233", "973a771f-60a2-4d3d-8324-38d204e2b233");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));

		tree.put("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", "811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));

		tree.put("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", "eaf308a8-aebc-49a3-9dca-e4ee1f46588e");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));

		tree.put("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", "8e88fe5a-d8c5-42a4-9349-33f71e2d1011");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));

		tree.put("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", "7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));

		tree.put("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", "a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));

		tree.put("ad313ed6-1336-4c10-9846-c02ecda77633", "ad313ed6-1336-4c10-9846-c02ecda77633");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));

		tree.put("46c90f66-d41f-40ae-b662-4c2de6f38215", "46c90f66-d41f-40ae-b662-4c2de6f38215");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));

		tree.put("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", "2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));

		tree.put("125d367f-a36b-4d0d-8c49-c0515280caf5", "125d367f-a36b-4d0d-8c49-c0515280caf5");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));

		tree.put("3eba768e-dc93-4e7e-aa41-6d05b12a3252", "3eba768e-dc93-4e7e-aa41-6d05b12a3252");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));

		tree.put("e6b15390-8957-40a6-9d84-562300058e6d", "e6b15390-8957-40a6-9d84-562300058e6d");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));

		tree.put("c086cf99-8190-4337-861d-edec0d0d2b8b", "c086cf99-8190-4337-861d-edec0d0d2b8b");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));
		assertEquals("c086cf99-8190-4337-861d-edec0d0d2b8b", tree.get("c086cf99-8190-4337-861d-edec0d0d2b8b"));

		tree.put("fae5915e-ad1f-4159-a7be-2abf94638167", "fae5915e-ad1f-4159-a7be-2abf94638167");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));
		assertEquals("c086cf99-8190-4337-861d-edec0d0d2b8b", tree.get("c086cf99-8190-4337-861d-edec0d0d2b8b"));
		assertEquals("fae5915e-ad1f-4159-a7be-2abf94638167", tree.get("fae5915e-ad1f-4159-a7be-2abf94638167"));

		tree.put("e3cf6e20-7be5-4b44-a222-d4107e00b3d5", "e3cf6e20-7be5-4b44-a222-d4107e00b3d5");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));
		assertEquals("c086cf99-8190-4337-861d-edec0d0d2b8b", tree.get("c086cf99-8190-4337-861d-edec0d0d2b8b"));
		assertEquals("fae5915e-ad1f-4159-a7be-2abf94638167", tree.get("fae5915e-ad1f-4159-a7be-2abf94638167"));
		assertEquals("e3cf6e20-7be5-4b44-a222-d4107e00b3d5", tree.get("e3cf6e20-7be5-4b44-a222-d4107e00b3d5"));

		tree.put("29974fe4-31bc-423c-80c2-8ec77dad749a", "29974fe4-31bc-423c-80c2-8ec77dad749a");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));
		assertEquals("c086cf99-8190-4337-861d-edec0d0d2b8b", tree.get("c086cf99-8190-4337-861d-edec0d0d2b8b"));
		assertEquals("fae5915e-ad1f-4159-a7be-2abf94638167", tree.get("fae5915e-ad1f-4159-a7be-2abf94638167"));
		assertEquals("e3cf6e20-7be5-4b44-a222-d4107e00b3d5", tree.get("e3cf6e20-7be5-4b44-a222-d4107e00b3d5"));
		assertEquals("29974fe4-31bc-423c-80c2-8ec77dad749a", tree.get("29974fe4-31bc-423c-80c2-8ec77dad749a"));

		tree.put("05af06a7-0440-4715-a760-f29e01945066", "05af06a7-0440-4715-a760-f29e01945066");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));
		assertEquals("c086cf99-8190-4337-861d-edec0d0d2b8b", tree.get("c086cf99-8190-4337-861d-edec0d0d2b8b"));
		assertEquals("fae5915e-ad1f-4159-a7be-2abf94638167", tree.get("fae5915e-ad1f-4159-a7be-2abf94638167"));
		assertEquals("e3cf6e20-7be5-4b44-a222-d4107e00b3d5", tree.get("e3cf6e20-7be5-4b44-a222-d4107e00b3d5"));
		assertEquals("29974fe4-31bc-423c-80c2-8ec77dad749a", tree.get("29974fe4-31bc-423c-80c2-8ec77dad749a"));
		assertEquals("05af06a7-0440-4715-a760-f29e01945066", tree.get("05af06a7-0440-4715-a760-f29e01945066"));

		tree.put("389868c5-be1f-425c-86a5-4fac7f5d273f", "389868c5-be1f-425c-86a5-4fac7f5d273f");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));
		assertEquals("c086cf99-8190-4337-861d-edec0d0d2b8b", tree.get("c086cf99-8190-4337-861d-edec0d0d2b8b"));
		assertEquals("fae5915e-ad1f-4159-a7be-2abf94638167", tree.get("fae5915e-ad1f-4159-a7be-2abf94638167"));
		assertEquals("e3cf6e20-7be5-4b44-a222-d4107e00b3d5", tree.get("e3cf6e20-7be5-4b44-a222-d4107e00b3d5"));
		assertEquals("29974fe4-31bc-423c-80c2-8ec77dad749a", tree.get("29974fe4-31bc-423c-80c2-8ec77dad749a"));
		assertEquals("05af06a7-0440-4715-a760-f29e01945066", tree.get("05af06a7-0440-4715-a760-f29e01945066"));
		assertEquals("389868c5-be1f-425c-86a5-4fac7f5d273f", tree.get("389868c5-be1f-425c-86a5-4fac7f5d273f"));

		tree.put("898de210-5e7c-4a5b-a63a-33afb3f362cb", "898de210-5e7c-4a5b-a63a-33afb3f362cb");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));
		assertEquals("c086cf99-8190-4337-861d-edec0d0d2b8b", tree.get("c086cf99-8190-4337-861d-edec0d0d2b8b"));
		assertEquals("fae5915e-ad1f-4159-a7be-2abf94638167", tree.get("fae5915e-ad1f-4159-a7be-2abf94638167"));
		assertEquals("e3cf6e20-7be5-4b44-a222-d4107e00b3d5", tree.get("e3cf6e20-7be5-4b44-a222-d4107e00b3d5"));
		assertEquals("29974fe4-31bc-423c-80c2-8ec77dad749a", tree.get("29974fe4-31bc-423c-80c2-8ec77dad749a"));
		assertEquals("05af06a7-0440-4715-a760-f29e01945066", tree.get("05af06a7-0440-4715-a760-f29e01945066"));
		assertEquals("389868c5-be1f-425c-86a5-4fac7f5d273f", tree.get("389868c5-be1f-425c-86a5-4fac7f5d273f"));
		assertEquals("898de210-5e7c-4a5b-a63a-33afb3f362cb", tree.get("898de210-5e7c-4a5b-a63a-33afb3f362cb"));

		tree.put("e9ed6307-3a6c-4e5d-b4b9-3f8c01920695", "e9ed6307-3a6c-4e5d-b4b9-3f8c01920695");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));
		assertEquals("c086cf99-8190-4337-861d-edec0d0d2b8b", tree.get("c086cf99-8190-4337-861d-edec0d0d2b8b"));
		assertEquals("fae5915e-ad1f-4159-a7be-2abf94638167", tree.get("fae5915e-ad1f-4159-a7be-2abf94638167"));
		assertEquals("e3cf6e20-7be5-4b44-a222-d4107e00b3d5", tree.get("e3cf6e20-7be5-4b44-a222-d4107e00b3d5"));
		assertEquals("29974fe4-31bc-423c-80c2-8ec77dad749a", tree.get("29974fe4-31bc-423c-80c2-8ec77dad749a"));
		assertEquals("05af06a7-0440-4715-a760-f29e01945066", tree.get("05af06a7-0440-4715-a760-f29e01945066"));
		assertEquals("389868c5-be1f-425c-86a5-4fac7f5d273f", tree.get("389868c5-be1f-425c-86a5-4fac7f5d273f"));
		assertEquals("898de210-5e7c-4a5b-a63a-33afb3f362cb", tree.get("898de210-5e7c-4a5b-a63a-33afb3f362cb"));
		assertEquals("e9ed6307-3a6c-4e5d-b4b9-3f8c01920695", tree.get("e9ed6307-3a6c-4e5d-b4b9-3f8c01920695"));

		tree.put("d33ce179-076f-4e60-9f2d-768467593ee1", "d33ce179-076f-4e60-9f2d-768467593ee1");
		assertEquals("58be41d9-fc1e-4f91-a363-a3bf62921d62", tree.get("58be41d9-fc1e-4f91-a363-a3bf62921d62"));
		assertEquals("0c5c153b-31c5-4fb0-a89c-406cdd207b7f", tree.get("0c5c153b-31c5-4fb0-a89c-406cdd207b7f"));
		assertEquals("e745b7f3-7c22-40c5-9911-26a3c63eafee", tree.get("e745b7f3-7c22-40c5-9911-26a3c63eafee"));
		assertEquals("46d75575-43e0-4b13-9bf9-bff2c341434e", tree.get("46d75575-43e0-4b13-9bf9-bff2c341434e"));
		assertEquals("45be1108-fe75-4ec1-b210-17aca8f37527", tree.get("45be1108-fe75-4ec1-b210-17aca8f37527"));
		assertEquals("e96bb27c-4807-4596-965a-525b46e1e172", tree.get("e96bb27c-4807-4596-965a-525b46e1e172"));
		assertEquals("67a13857-5d93-4122-9773-0d75a2053a88", tree.get("67a13857-5d93-4122-9773-0d75a2053a88"));
		assertEquals("6504ad48-e2df-4cc6-9695-4b38cd5a443b", tree.get("6504ad48-e2df-4cc6-9695-4b38cd5a443b"));
		assertEquals("bf285209-baeb-43ee-9d24-88b537f27c03", tree.get("bf285209-baeb-43ee-9d24-88b537f27c03"));
		assertEquals("1a932266-b282-4cb7-909d-833a4b677cec", tree.get("1a932266-b282-4cb7-909d-833a4b677cec"));
		assertEquals("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c", tree.get("408d89c6-d2b3-49f3-a080-f1a7a43eeb4c"));
		assertEquals("973a771f-60a2-4d3d-8324-38d204e2b233", tree.get("973a771f-60a2-4d3d-8324-38d204e2b233"));
		assertEquals("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3", tree.get("811b51f5-ab9d-47ca-8e6a-1eaf9d8f10f3"));
		assertEquals("eaf308a8-aebc-49a3-9dca-e4ee1f46588e", tree.get("eaf308a8-aebc-49a3-9dca-e4ee1f46588e"));
		assertEquals("8e88fe5a-d8c5-42a4-9349-33f71e2d1011", tree.get("8e88fe5a-d8c5-42a4-9349-33f71e2d1011"));
		assertEquals("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a", tree.get("7f5d1c50-9a55-49f4-b048-a7e8dedd0f0a"));
		assertEquals("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e", tree.get("a8e6f07b-5c4e-45ad-a9dd-ec0d10fa4d5e"));
		assertEquals("ad313ed6-1336-4c10-9846-c02ecda77633", tree.get("ad313ed6-1336-4c10-9846-c02ecda77633"));
		assertEquals("46c90f66-d41f-40ae-b662-4c2de6f38215", tree.get("46c90f66-d41f-40ae-b662-4c2de6f38215"));
		assertEquals("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce", tree.get("2abb6cf8-e6b9-47b4-80e9-8c1d929fb9ce"));
		assertEquals("125d367f-a36b-4d0d-8c49-c0515280caf5", tree.get("125d367f-a36b-4d0d-8c49-c0515280caf5"));
		assertEquals("3eba768e-dc93-4e7e-aa41-6d05b12a3252", tree.get("3eba768e-dc93-4e7e-aa41-6d05b12a3252"));
		assertEquals("e6b15390-8957-40a6-9d84-562300058e6d", tree.get("e6b15390-8957-40a6-9d84-562300058e6d"));
		assertEquals("c086cf99-8190-4337-861d-edec0d0d2b8b", tree.get("c086cf99-8190-4337-861d-edec0d0d2b8b"));
		assertEquals("fae5915e-ad1f-4159-a7be-2abf94638167", tree.get("fae5915e-ad1f-4159-a7be-2abf94638167"));
		assertEquals("e3cf6e20-7be5-4b44-a222-d4107e00b3d5", tree.get("e3cf6e20-7be5-4b44-a222-d4107e00b3d5"));
		assertEquals("29974fe4-31bc-423c-80c2-8ec77dad749a", tree.get("29974fe4-31bc-423c-80c2-8ec77dad749a"));
		assertEquals("05af06a7-0440-4715-a760-f29e01945066", tree.get("05af06a7-0440-4715-a760-f29e01945066"));
		assertEquals("389868c5-be1f-425c-86a5-4fac7f5d273f", tree.get("389868c5-be1f-425c-86a5-4fac7f5d273f"));
		assertEquals("898de210-5e7c-4a5b-a63a-33afb3f362cb", tree.get("898de210-5e7c-4a5b-a63a-33afb3f362cb"));
		assertEquals("e9ed6307-3a6c-4e5d-b4b9-3f8c01920695", tree.get("e9ed6307-3a6c-4e5d-b4b9-3f8c01920695"));
		assertEquals("d33ce179-076f-4e60-9f2d-768467593ee1", tree.get("d33ce179-076f-4e60-9f2d-768467593ee1"));

		tree.put("42020f55-9bd1-43d6-8c42-bb62f2079227", "42020f55-9bd1-43d6-8c42-bb62f2079227");
		assertEquals("42020f55-9bd1-43d6-8c42-bb62f2079227", tree.get("42020f55-9bd1-43d6-8c42-bb62f2079227"));

		tree.put("6bb72e4e-7ae7-4ea3-96e5-8553c7a5d142", "6bb72e4e-7ae7-4ea3-96e5-8553c7a5d142");
		assertEquals("6bb72e4e-7ae7-4ea3-96e5-8553c7a5d142", tree.get("6bb72e4e-7ae7-4ea3-96e5-8553c7a5d142"));

		tree.put("d2ff9d8e-c8b8-4390-b131-31a803a7b0bf", "d2ff9d8e-c8b8-4390-b131-31a803a7b0bf");
		assertEquals("d2ff9d8e-c8b8-4390-b131-31a803a7b0bf", tree.get("d2ff9d8e-c8b8-4390-b131-31a803a7b0bf"));

		tree.put("a8f046a9-051c-4f73-98f6-5bc199930aa0", "a8f046a9-051c-4f73-98f6-5bc199930aa0");
		assertEquals("a8f046a9-051c-4f73-98f6-5bc199930aa0", tree.get("a8f046a9-051c-4f73-98f6-5bc199930aa0"));

		tree.put("935825b8-d9f9-4fcc-8d2a-c56506ceae93", "935825b8-d9f9-4fcc-8d2a-c56506ceae93");
		assertEquals("935825b8-d9f9-4fcc-8d2a-c56506ceae93", tree.get("935825b8-d9f9-4fcc-8d2a-c56506ceae93"));

		tree.put("2e529e96-ba8d-478d-8a7d-ef27e554d58a", "2e529e96-ba8d-478d-8a7d-ef27e554d58a");
		assertEquals("2e529e96-ba8d-478d-8a7d-ef27e554d58a", tree.get("2e529e96-ba8d-478d-8a7d-ef27e554d58a"));

		tree.put("f7685865-5f29-4c98-b5be-af095412cc9c", "f7685865-5f29-4c98-b5be-af095412cc9c");
		assertEquals("f7685865-5f29-4c98-b5be-af095412cc9c", tree.get("f7685865-5f29-4c98-b5be-af095412cc9c"));

		tree.put("409591b2-0b78-4498-be7e-4058e9fb3c2e", "409591b2-0b78-4498-be7e-4058e9fb3c2e");
		assertEquals("409591b2-0b78-4498-be7e-4058e9fb3c2e", tree.get("409591b2-0b78-4498-be7e-4058e9fb3c2e"));

		tree.put("cd497250-9185-48e3-9f15-c3c75126ab04", "cd497250-9185-48e3-9f15-c3c75126ab04");
		assertEquals("cd497250-9185-48e3-9f15-c3c75126ab04", tree.get("cd497250-9185-48e3-9f15-c3c75126ab04"));

		tree.put("034c461e-8924-46d1-bca3-6a3eb5bf7c88", "034c461e-8924-46d1-bca3-6a3eb5bf7c88");
		assertEquals("034c461e-8924-46d1-bca3-6a3eb5bf7c88", tree.get("034c461e-8924-46d1-bca3-6a3eb5bf7c88"));

		tree.put("8be2e3d1-d8f7-4f36-9692-5faad06ffbb2", "8be2e3d1-d8f7-4f36-9692-5faad06ffbb2");
		assertEquals("8be2e3d1-d8f7-4f36-9692-5faad06ffbb2", tree.get("8be2e3d1-d8f7-4f36-9692-5faad06ffbb2"));

		tree.put("c7c9265d-cb5a-4751-a2ed-539c85888e68", "c7c9265d-cb5a-4751-a2ed-539c85888e68");
		assertEquals("c7c9265d-cb5a-4751-a2ed-539c85888e68", tree.get("c7c9265d-cb5a-4751-a2ed-539c85888e68"));

		tree.put("dd1b3f32-5788-4c5f-9752-2a4299888953", "dd1b3f32-5788-4c5f-9752-2a4299888953");
		assertEquals("dd1b3f32-5788-4c5f-9752-2a4299888953", tree.get("dd1b3f32-5788-4c5f-9752-2a4299888953"));

		tree.put("5b6dedef-ff2f-4a6c-ac80-6f788939710d", "5b6dedef-ff2f-4a6c-ac80-6f788939710d");
		assertEquals("5b6dedef-ff2f-4a6c-ac80-6f788939710d", tree.get("5b6dedef-ff2f-4a6c-ac80-6f788939710d"));

		tree.put("9db748b7-ec4e-4d59-8c58-50887c103d0c", "9db748b7-ec4e-4d59-8c58-50887c103d0c");
		assertEquals("9db748b7-ec4e-4d59-8c58-50887c103d0c", tree.get("9db748b7-ec4e-4d59-8c58-50887c103d0c"));

		tree.put("c30576f8-9586-4b9a-a975-aae8fac7ff4d", "c30576f8-9586-4b9a-a975-aae8fac7ff4d");
		assertEquals("c30576f8-9586-4b9a-a975-aae8fac7ff4d", tree.get("c30576f8-9586-4b9a-a975-aae8fac7ff4d"));

		tree.put("3c652e3f-c577-4317-8c07-c0c2e41119f5", "3c652e3f-c577-4317-8c07-c0c2e41119f5");
		assertEquals("3c652e3f-c577-4317-8c07-c0c2e41119f5", tree.get("3c652e3f-c577-4317-8c07-c0c2e41119f5"));

		tree.put("a534b1b9-1746-43e9-9405-e99e01f41135", "a534b1b9-1746-43e9-9405-e99e01f41135");
		assertEquals("a534b1b9-1746-43e9-9405-e99e01f41135", tree.get("a534b1b9-1746-43e9-9405-e99e01f41135"));

		tree.put("2c74f7a3-d4e6-492c-b97b-e1c5f969a401", "2c74f7a3-d4e6-492c-b97b-e1c5f969a401");
		assertEquals("2c74f7a3-d4e6-492c-b97b-e1c5f969a401", tree.get("2c74f7a3-d4e6-492c-b97b-e1c5f969a401"));

		tree.put("396b1601-21cd-4cfe-8120-5bd129f4ffa1", "396b1601-21cd-4cfe-8120-5bd129f4ffa1");
		assertEquals("396b1601-21cd-4cfe-8120-5bd129f4ffa1", tree.get("396b1601-21cd-4cfe-8120-5bd129f4ffa1"));

		tree.put("794ed0e7-d2ef-494a-ae1b-e9b2af2ce229", "794ed0e7-d2ef-494a-ae1b-e9b2af2ce229");
		assertEquals("794ed0e7-d2ef-494a-ae1b-e9b2af2ce229", tree.get("794ed0e7-d2ef-494a-ae1b-e9b2af2ce229"));

		tree.put("07c24769-e9c2-4c43-bb72-5a15033e6250", "07c24769-e9c2-4c43-bb72-5a15033e6250");
		assertEquals("07c24769-e9c2-4c43-bb72-5a15033e6250", tree.get("07c24769-e9c2-4c43-bb72-5a15033e6250"));

		tree.put("ac282d4e-5747-40a2-b182-55cf39021038", "ac282d4e-5747-40a2-b182-55cf39021038");
		assertEquals("ac282d4e-5747-40a2-b182-55cf39021038", tree.get("ac282d4e-5747-40a2-b182-55cf39021038"));

		tree.put("5911a6ba-192e-46fe-ac8a-72eed88665c2", "5911a6ba-192e-46fe-ac8a-72eed88665c2");
		assertEquals("5911a6ba-192e-46fe-ac8a-72eed88665c2", tree.get("5911a6ba-192e-46fe-ac8a-72eed88665c2"));

		tree.put("ec0d22dc-93ac-4b46-bb43-508a5c2ede68", "ec0d22dc-93ac-4b46-bb43-508a5c2ede68");
		assertEquals("ec0d22dc-93ac-4b46-bb43-508a5c2ede68", tree.get("ec0d22dc-93ac-4b46-bb43-508a5c2ede68"));

		tree.put("09e7d117-ee09-4bca-b387-ae3be0b70298", "09e7d117-ee09-4bca-b387-ae3be0b70298");
		assertEquals("09e7d117-ee09-4bca-b387-ae3be0b70298", tree.get("09e7d117-ee09-4bca-b387-ae3be0b70298"));
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