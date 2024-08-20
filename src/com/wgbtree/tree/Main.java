package com.wgbtree.tree;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.wgbtree.tree.bplus.BPlusTreeMap;
import com.wgbtree.tree.redblack.TreeMapAsTree;
import com.wgbtree.tree.wgb.AccWGBTreeMap;
import com.wgbtree.tree.wgb.MersenneAccWgbTreeMap;
import com.wgbtree.tree.wgb.MersenneDecWgbTreeMap;
import com.wgbtree.tree.wgb.WGBPowerTreeMap;

import java.util.*;

public class Main {

	private static final Map<String, List<WriteRequest>> batchWriteItemRequests = new HashMap<>();
	private static final List<UpdateItemRequest> updateItemRequests = new ArrayList<>();
	private static final int MIN_BUFFER_SIZE_LIMIT = 10_000;
	private static final int MAX_BUFFER_SIZE_LIMIT = 100_000;
	private static int bufferLimit = MIN_BUFFER_SIZE_LIMIT;
	private static final int MAX_REPEAT = 13;
	private static final int MIN_TOTAL_COUNT = 1000;
	private static final int MAX_TOTAL_COUNT = 4_196_000;
	private static final int DYNAMO_DB_MAX_BATCH_SIZE = 25;
	private static int batchSize = 0;

	private static final AmazonDynamoDBAsync dynamoDbClient = AmazonDynamoDBAsyncClient.asyncBuilder()
	 		.withEndpointConfiguration(new EndpointConfiguration("http://localhost:8001", "us-east-2"))
	 		.build();

	private static final DynamoDB dynamoDb = new DynamoDB(dynamoDbClient);

	public static void flushBuffer() {
		dynamoDbClient.batchWriteItem(new HashMap<>(batchWriteItemRequests));
		batchWriteItemRequests.clear();
		batchSize = 0;
	}

	public static void writeToTable(String operation, int count, String treeName, long metric) {
		var getItemRequest = new GetItemRequest()
				.withTableName(operation)
				.withKey(Map.of("tree_name", new AttributeValue(treeName), "count", new AttributeValue().withN(String.valueOf(count))));

		var item = dynamoDbClient.getItem(getItemRequest).getItem();

		if (item != null) {
			var existingMetric = Long.parseLong(item.get("metric").getN());
			int hits = Integer.parseInt(item.get("hits").getN());

			int newHits = hits + 1;
			long newMetric = (existingMetric * hits + metric) / newHits;

			var updateItemRequest = new UpdateItemRequest()
					.withTableName(operation)
					.withKey(Map.of("tree_name", new AttributeValue(treeName), "count", new AttributeValue().withN(String.valueOf(count))))
					.withUpdateExpression("set #m = :m, #h = :h")
					.withExpressionAttributeNames(Map.of("#m", "metric", "#h", "hits"))
					.withExpressionAttributeValues(Map.of(":m", new AttributeValue().withN(String.valueOf(newMetric)), ":h", new AttributeValue().withN(String.valueOf(newHits))));

			updateItemRequests.add(updateItemRequest);

			if (updateItemRequests.size() >= DYNAMO_DB_MAX_BATCH_SIZE) {
				updateItemRequests.forEach(dynamoDbClient::updateItem);
				updateItemRequests.clear();
			}
		} else {
			var itemRequest = new HashMap<String, AttributeValue>();
			itemRequest.put("tree_name", new AttributeValue(treeName));
			itemRequest.put("count", new AttributeValue().withN(String.valueOf(count)));
			itemRequest.put("metric", new AttributeValue().withN(String.valueOf(metric)));
			itemRequest.put("hits", new AttributeValue().withN("1"));

            var list = batchWriteItemRequests.computeIfAbsent(operation, k -> new LinkedList<>());
            list.add(new WriteRequest().withPutRequest(new PutRequest().withItem(itemRequest)));

			if (++batchSize >= DYNAMO_DB_MAX_BATCH_SIZE) {
				flushBuffer();
			}
		}
	}

	public static void createTables() {
		createTableIfDoesNotExist("insert");
		createTableIfDoesNotExist("search");
		createTableIfDoesNotExist("searchMin");
		createTableIfDoesNotExist("searchMax");
		createTableIfDoesNotExist("depth");
	}

	public static void createTableIfDoesNotExist(String operation) {
		if (!doesTableExist(operation)) {
			createTable(operation);
		}
	}

	private static boolean doesTableExist(String tableName) {
		try {
			var response = dynamoDbClient.describeTable(tableName);
			return Objects.equals(response.getTable().getTableStatus(), "ACTIVE");
		} catch (ResourceNotFoundException e) {
			// Table does not exist
			return false;
		}
	}

	private static void createTable(String tableName) {

		try {
			ArrayList<KeySchemaElement> keySchema = new ArrayList<>();
			ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();

			keySchema.add(new KeySchemaElement().withAttributeName("tree_name").withKeyType(KeyType.HASH)); // Partition
			attributeDefinitions.add(new AttributeDefinition().withAttributeName("tree_name").withAttributeType("S"));

			keySchema.add(new KeySchemaElement().withAttributeName("count").withKeyType(KeyType.RANGE)); // Sort
			attributeDefinitions.add(new AttributeDefinition().withAttributeName("count").withAttributeType("N"));

			CreateTableRequest request = new CreateTableRequest().withTableName(tableName).withKeySchema(keySchema)
					.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(10L)
							.withWriteCapacityUnits(5L));

			request.setAttributeDefinitions(attributeDefinitions);

			System.out.println("Issuing CreateTable request for " + tableName);
			Table table = dynamoDb.createTable(request);
			System.out.println("Waiting for " + tableName + " to be created...this may take a while...");
			table.waitForActive();
		} catch (Exception e) {
			System.err.println("CreateTable request failed for " + tableName);
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			flushBuffer(); // Ensure any remaining data is written to the file
			System.out.println("Shutdown hook triggered.");
		}));

		List<AsTree<String, Boolean>> trees = List.of(
				new AccWGBTreeMap<>(100, 97, false),
				new AccWGBTreeMap<>(150, 97, false),
				new AccWGBTreeMap<>(200, 97, false),
				new AccWGBTreeMap<>(300, 97, false),
				new AccWGBTreeMap<>(600, 97, false),

				new AccWGBTreeMap<>(100, 199, false),
				new AccWGBTreeMap<>(150, 199, false),
				new AccWGBTreeMap<>(200, 199, false),
				new AccWGBTreeMap<>(300, 199, false),
				new AccWGBTreeMap<>(600, 199, false),

				new AccWGBTreeMap<>(100, 307, false),
				new AccWGBTreeMap<>(150, 307, false),
				new AccWGBTreeMap<>(200, 307, false),
				new AccWGBTreeMap<>(300, 307, false),
				new AccWGBTreeMap<>(600, 307, false),

				new AccWGBTreeMap<>(100, 8191, false),
				new AccWGBTreeMap<>(150, 8191, false),
				new AccWGBTreeMap<>(200, 8191, false),
				new AccWGBTreeMap<>(300, 8191, false),
				new AccWGBTreeMap<>(600, 8191, false),

				new MersenneAccWgbTreeMap<>(100, 2, false),
				new MersenneAccWgbTreeMap<>(150, 2, false),
				new MersenneAccWgbTreeMap<>(200, 2, false),
				new MersenneAccWgbTreeMap<>(300, 2, false),
				new MersenneAccWgbTreeMap<>(600, 2, false),

				new MersenneAccWgbTreeMap<>(100, 3, false),
				new MersenneAccWgbTreeMap<>(150, 3, false),
				new MersenneAccWgbTreeMap<>(200, 3, false),
				new MersenneAccWgbTreeMap<>(300, 3, false),
				new MersenneAccWgbTreeMap<>(600, 3, false),

				new MersenneAccWgbTreeMap<>(100, 5, false),
				new MersenneAccWgbTreeMap<>(150, 5, false),
				new MersenneAccWgbTreeMap<>(200, 5, false),
				new MersenneAccWgbTreeMap<>(300, 5, false),
				new MersenneAccWgbTreeMap<>(600, 5, false),

				new MersenneAccWgbTreeMap<>(100, 7, false),
				new MersenneAccWgbTreeMap<>(150, 7, false),
				new MersenneAccWgbTreeMap<>(200, 7, false),
				new MersenneAccWgbTreeMap<>(300, 7, false),
				new MersenneAccWgbTreeMap<>(600, 7, false),

				new MersenneAccWgbTreeMap<>(100, 13, false),
				new MersenneAccWgbTreeMap<>(150, 13, false),
				new MersenneAccWgbTreeMap<>(200, 13, false),
				new MersenneAccWgbTreeMap<>(300, 13, false),
				new MersenneAccWgbTreeMap<>(600, 13, false),

				new MersenneDecWgbTreeMap<>(100, 13, false),
				new MersenneDecWgbTreeMap<>(150, 13, false),
				new MersenneDecWgbTreeMap<>(200, 13, false),
				new MersenneDecWgbTreeMap<>(300, 13, false),
				new MersenneDecWgbTreeMap<>(600, 13, false),

				new MersenneDecWgbTreeMap<>(100, 7, false),
				new MersenneDecWgbTreeMap<>(150, 7, false),
				new MersenneDecWgbTreeMap<>(200, 7, false),
				new MersenneDecWgbTreeMap<>(300, 7, false),
				new MersenneDecWgbTreeMap<>(600, 7, false),

				new MersenneDecWgbTreeMap<>(100, 5, false),
				new MersenneDecWgbTreeMap<>(150, 5, false),
				new MersenneDecWgbTreeMap<>(200, 5, false),
				new MersenneDecWgbTreeMap<>(300, 5, false),
				new MersenneDecWgbTreeMap<>(600, 5, false),

				new MersenneDecWgbTreeMap<>(100, 3, false),
				new MersenneDecWgbTreeMap<>(150, 3, false),
				new MersenneDecWgbTreeMap<>(200, 3, false),
				new MersenneDecWgbTreeMap<>(300, 3, false),
				new MersenneDecWgbTreeMap<>(600, 3, false),

				new MersenneDecWgbTreeMap<>(100, 2, false),
				new MersenneDecWgbTreeMap<>(150, 2, false),
				new MersenneDecWgbTreeMap<>(200, 2, false),
				new MersenneDecWgbTreeMap<>(300, 2, false),
				new MersenneDecWgbTreeMap<>(600, 2, false),

				new WGBPowerTreeMap<>(100, 2, false),
				new WGBPowerTreeMap<>(150, 2, false),
				new WGBPowerTreeMap<>(200, 2, false),
				new WGBPowerTreeMap<>(300, 2, false),
				new WGBPowerTreeMap<>(600, 2, false),

				new WGBPowerTreeMap<>(100, 3, false),
				new WGBPowerTreeMap<>(150, 3, false),
				new WGBPowerTreeMap<>(200, 3, false),
				new WGBPowerTreeMap<>(300, 3, false),
				new WGBPowerTreeMap<>(600, 3, false),

				new WGBPowerTreeMap<>(100, 5, false),
				new WGBPowerTreeMap<>(150, 5, false),
				new WGBPowerTreeMap<>(200, 5, false),
				new WGBPowerTreeMap<>(300, 5, false),
				new WGBPowerTreeMap<>(600, 5, false),

				new WGBPowerTreeMap<>(100, 4, false),
				new WGBPowerTreeMap<>(150, 4, false),
				new WGBPowerTreeMap<>(200, 4, false),
				new WGBPowerTreeMap<>(300, 4, false),
				new WGBPowerTreeMap<>(600, 4, false),

				new WGBPowerTreeMap<>(100, 6, false),
				new WGBPowerTreeMap<>(150, 6, false),
				new WGBPowerTreeMap<>(200, 6, false),
				new WGBPowerTreeMap<>(300, 6, false),
				new WGBPowerTreeMap<>(600, 6, false),

				new WGBPowerTreeMap<>(100, 7, false),
				new WGBPowerTreeMap<>(150, 7, false),
				new WGBPowerTreeMap<>(200, 7, false),
				new WGBPowerTreeMap<>(300, 7, false),
				new WGBPowerTreeMap<>(600, 7, false),

				new WGBPowerTreeMap<>(100, 8, false),
				new WGBPowerTreeMap<>(150, 8, false),
				new WGBPowerTreeMap<>(200, 8, false),
				new WGBPowerTreeMap<>(300, 8, false),
				new WGBPowerTreeMap<>(600, 8, false),

				new WGBPowerTreeMap<>(100, 9, false),
				new WGBPowerTreeMap<>(150, 9, false),
				new WGBPowerTreeMap<>(200, 9, false),
				new WGBPowerTreeMap<>(300, 9, false),
				new WGBPowerTreeMap<>(600, 9, false),

				new WGBPowerTreeMap<>(100, 10, false),
				new WGBPowerTreeMap<>(150, 10, false),
				new WGBPowerTreeMap<>(200, 10, false),
				new WGBPowerTreeMap<>(300, 10, false),
				new WGBPowerTreeMap<>(600, 10, false),

				new TreeMapAsTree<>(),

				new BPlusTreeMap<>(100),
				new BPlusTreeMap<>(150),
				new BPlusTreeMap<>(200),
				new BPlusTreeMap<>(300)
		);

		createTables();

		System.out.println(" > Dry run");
		trees.forEach(tree -> {
			for (int count = 1; count < 1000; count++) {
				String randomString = UUID.randomUUID().toString();
				tree.put(randomString, true);
				tree.get(randomString);
				tree.getMin();
				tree.getMax();
				tree.depth();
			}
			tree.clear();
		});

		for (int rep = 1; rep <= MAX_REPEAT; rep++) {
			int finalRep = rep;
			int finalTotalCount = (int) Math.min(MIN_TOTAL_COUNT * Math.pow(2, rep - 1), MAX_TOTAL_COUNT);

			trees.forEach(tree -> {
				for (int count = 1; count < finalTotalCount; count++) {
					try {
						if (count % DYNAMO_DB_MAX_BATCH_SIZE == 0) {
							System.out.println(" > Tree " + tree.getName() + " > Rep: " + finalRep + ", Count: " + count);
						}

						String randomString = UUID.randomUUID().toString();

						long insertTime = measureTime(() -> tree.put(randomString, true));
						writeToTable("insert", count, tree.getName(), insertTime);

						long searchTime = measureTime(() -> tree.get(randomString));
						writeToTable("search", count, tree.getName(), searchTime);

						long searchMinTime = measureTime(tree::getMin);
						writeToTable("searchMin", count, tree.getName(), searchMinTime);

						long searchMaxTime = measureTime(tree::getMax);
						writeToTable("searchMax", count, tree.getName(), searchMaxTime);

						long depth = tree.depth();
						writeToTable("depth", count, tree.getName(), depth);

						long totalTime = insertTime + searchTime + searchMinTime + searchMaxTime + depth;
						bufferLimit = totalTime > 1_000_000_000 ? Math.min(bufferLimit * 2, MAX_BUFFER_SIZE_LIMIT) : bufferLimit;

						if (count % 1_000 == 0) {
							System.gc();
						}
					} catch (Exception e) {
						System.out.println(" > Error: " + e.getMessage() + ", stopped on count: " + count);
						break;
					}
				}

				bufferLimit = MIN_BUFFER_SIZE_LIMIT;
				tree.clear();
				System.gc();
			});
		}
	}

	public static long measureTime(Runnable runnable) {
		long startTime = System.nanoTime();
		runnable.run();
		return System.nanoTime() - startTime;
	}
}
