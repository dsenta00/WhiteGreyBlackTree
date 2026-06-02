package com.wgbtree.tree;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.wgbtree.tree.bplus.BPlusTreeMap;
import com.wgbtree.tree.redblack.TreeMapAsTree;
import com.wgbtree.tree.wgb.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.wgbtree.tree.test.Test.measureTime;

public class Main {

    private static final Map<String, List<WriteRequest>> batchWriteItemRequests = new HashMap<>();
    private static final List<UpdateItemRequest> updateItemRequests = new ArrayList<>();
    private static final int MAX_REPEAT = 1;
    private static final int MIN_TOTAL_COUNT = 6_000_000;
    private static final int MAX_TOTAL_COUNT = 6_000_000;
    private static final int MIN_COUNT_TO_WRITE = 0;
    private static final int DYNAMO_DB_MAX_BATCH_SIZE = 25;
    private static final int DYNAMO_DB_FLUSH_SIZE = 1000;
    private static final int MIN_CALCULATING_GROUP_SIZE = 25;
    private static final int MAX_HITS = 3;
    private static final AmazonDynamoDB dynamoDbClient = AmazonDynamoDBAsyncClient.builder()
            .withEndpointConfiguration(new EndpointConfiguration("http://localhost:8001", "us-east-2"))
            .build();
    private static final DynamoDB dynamoDb = new DynamoDB(dynamoDbClient);
    private static int batchSize = 0;

    public static void flushBuffer() {
        if (updateItemRequests.size() >= DYNAMO_DB_FLUSH_SIZE) {
            updateItemRequests.forEach(dynamoDbClient::updateItem);
            updateItemRequests.clear();
        }

        if (batchSize >= DYNAMO_DB_FLUSH_SIZE) {
            batchWriteItemRequests.forEach((tableName, list) -> {
                for (int i = 0; i < list.size(); i += DYNAMO_DB_MAX_BATCH_SIZE) {
                    var request = new BatchWriteItemRequest();
                    request.withRequestItems(Map.of(tableName, list.subList(i, Math.min(i + DYNAMO_DB_MAX_BATCH_SIZE, list.size()))));
                    dynamoDbClient.batchWriteItem(request);
                }
            });

            batchWriteItemRequests.clear();
            batchSize = 0;
        }
    }

    public static void flushForce() {
        updateItemRequests.forEach(dynamoDbClient::updateItem);
        updateItemRequests.clear();

        batchWriteItemRequests.forEach((tableName, list) -> {
            for (int i = 0; i < list.size(); i += DYNAMO_DB_MAX_BATCH_SIZE) {
                var request = new BatchWriteItemRequest();
                request.withRequestItems(Map.of(tableName, list.subList(i, Math.min(i + DYNAMO_DB_MAX_BATCH_SIZE, list.size()))));
                dynamoDbClient.batchWriteItem(request);
            }
        });

        batchWriteItemRequests.clear();
        batchSize = 0;
    }

    public static void writeToTable(String operation, int count, String treeName, long metric) {
        if (count < MIN_COUNT_TO_WRITE) {
            return;
        }

        var getItemRequest = new GetItemRequest()
                .withTableName(operation)
                .withKey(Map.of("tree_name", new AttributeValue(treeName), "count", new AttributeValue().withN(String.valueOf(count))));

        var item = dynamoDbClient.getItem(getItemRequest).getItem();

        if (item != null) {
            var existingMetric = Long.parseLong(item.get("metric").getN());
            int hits = Integer.parseInt(item.get("hits").getN());

            int newHits = hits + 1;

            if (newHits >= MAX_HITS) {
                // Don't update since the metric is already stable
                return;
            }

            long newMetric = (existingMetric * hits + metric) / newHits;

            var updateItemRequest = new UpdateItemRequest()
                    .withTableName(operation)
                    .withKey(Map.of("tree_name", new AttributeValue(treeName), "count", new AttributeValue().withN(String.valueOf(count))))
                    .withUpdateExpression("set #m = :m, #h = :h")
                    .withExpressionAttributeNames(Map.of("#m", "metric", "#h", "hits"))
                    .withExpressionAttributeValues(Map.of(":m", new AttributeValue().withN(String.valueOf(newMetric)), ":h", new AttributeValue().withN(String.valueOf(newHits))));

            updateItemRequests.add(updateItemRequest);
        } else {
            var itemRequest = new HashMap<String, AttributeValue>();
            itemRequest.put("tree_name", new AttributeValue(treeName));
            itemRequest.put("count", new AttributeValue().withN(String.valueOf(count)));
            itemRequest.put("metric", new AttributeValue().withN(String.valueOf(metric)));
            itemRequest.put("hits", new AttributeValue().withN("1"));

            var list = batchWriteItemRequests.computeIfAbsent(operation, k -> new LinkedList<>());
            list.add(new WriteRequest().withPutRequest(new PutRequest().withItem(itemRequest)));
            batchSize++;
        }

        flushBuffer();
    }

    public static void createTables() {
        createTableIfDoesNotExist("insert");
        createTableIfDoesNotExist("search");
        createTableIfDoesNotExist("searchMin");
        createTableIfDoesNotExist("searchMax");
        createTableIfDoesNotExist("depth");
        createTableIfDoesNotExist("searchRange");
    }

    public static void deleteTables() {
    }

    public static void deleteAllByTreeName(String treeName) {
        deleteByTreeName("insert", treeName);
        deleteByTreeName("search", treeName);
        deleteByTreeName("searchMin", treeName);
        deleteByTreeName("searchMax", treeName);
        deleteByTreeName("depth", treeName);
        deleteByTreeName("searchRange", treeName);
    }

    public static void createTableIfDoesNotExist(String operation) {
        if (tableDoesNotExist(operation)) {
            createTable(operation);
        }
    }

    private static boolean tableDoesNotExist(String tableName) {
        try {
            var response = dynamoDbClient.describeTable(tableName);
            return !Objects.equals(response.getTable().getTableStatus(), "ACTIVE");
        } catch (ResourceNotFoundException e) {
            return true;
        }
    }

    private static void deleteByTreeName(String tableName, String treeName) {
        System.out.print(" > Deleting tree " + treeName + " from table " + tableName);

        try {
            var request = new QueryRequest()
                    .withTableName(tableName)
                    .withKeyConditionExpression("tree_name = :tree_name")
                    .withExpressionAttributeValues(Map.of(":tree_name", new AttributeValue(treeName)));

            var items = dynamoDbClient.query(request).getItems();

            if (items.isEmpty()) {
                System.out.println(" - No items found for " + treeName);
                return;
            }

            for (var item : items) {
                String treeNameValue = item.get("tree_name").getS();
                String countValue = item.get("count").getN();

                Map<String, AttributeValue> key = new HashMap<>();
                key.put("tree_name", new AttributeValue().withS(treeNameValue));
                key.put("count", new AttributeValue().withN(countValue));

                var deleteRequest = new DeleteItemRequest()
                        .withTableName(tableName)
                        .withKey(key);

                dynamoDbClient.deleteItem(deleteRequest);
            }

            System.out.println(" - Deleted " + items.size() + " items");
        } catch (Exception e) {
            System.err.println("Delete request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    private static void deleteTable(String tableName) {
        try {
            if (tableDoesNotExist(tableName)) {
                return;
            }

            System.out.println("Issuing DeleteTable request for " + tableName);
            Table table = dynamoDb.getTable(tableName);
            table.delete();
            System.out.println("Waiting for " + tableName + " to be deleted...this may take a while...");
            table.waitForDelete();
        } catch (Exception e) {
            System.err.println("DeleteTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    private static void createTable(String tableName) {

        try {
            var keySchema = new ArrayList<KeySchemaElement>();
            var attributeDefinitions = new ArrayList<AttributeDefinition>();

            keySchema.add(new KeySchemaElement().withAttributeName("tree_name").withKeyType(KeyType.HASH)); // Partition
            attributeDefinitions.add(new AttributeDefinition().withAttributeName("tree_name").withAttributeType("S"));

            keySchema.add(new KeySchemaElement().withAttributeName("count").withKeyType(KeyType.RANGE)); // Sort
            attributeDefinitions.add(new AttributeDefinition().withAttributeName("count").withAttributeType("N"));

            var request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput(
                            new ProvisionedThroughput()
                                    .withReadCapacityUnits(100L)
                                    .withWriteCapacityUnits(100L)
                    );

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
            flushForce(); // Ensure any remaining data is written to the file
            System.out.println("Shutdown hook triggered.");
        }));

        List<AsTree<String, Boolean>> trees = List.<AsTree<String, Boolean>>of(

                new TreeMapAsTree<>(),

                new BPlusTreeMap<>(600),
                new StraightWGBTreeMap<>(600, false),
                new RangeWGBTreeMap<>(600, false),


                new BPlusTreeMap<>(100),
                new BPlusTreeMap<>(150),
                new BPlusTreeMap<>(200),
                new BPlusTreeMap<>(300),

                new StraightWGBTreeMap<>(100, false),
                new StraightWGBTreeMap<>(150, false),
                new StraightWGBTreeMap<>(200, false),
                new StraightWGBTreeMap<>(300, false),

                new RangeWGBTreeMap<>(100, false),
                new RangeWGBTreeMap<>(150, false),
                new RangeWGBTreeMap<>(200, false),
                new RangeWGBTreeMap<>(300, false),

                new RangeWGBTreeMap<>(600, true),
                new RangeWGBTreeMap<>(300, true),
                new RangeWGBTreeMap<>(200, true),
                new RangeWGBTreeMap<>(150, true),
                new RangeWGBTreeMap<>(100, true),

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

                new DecWGBTreeMap<>(100, 8191, false),
                new DecWGBTreeMap<>(150, 8191, false),
                new DecWGBTreeMap<>(200, 8191, false),
                new DecWGBTreeMap<>(300, 8191, false),
                new DecWGBTreeMap<>(600, 8191, false),

                new FwMersenneAccWgbTreeMap<>(100, 7, false),
                new FwMersenneAccWgbTreeMap<>(150, 7, false),
                new FwMersenneAccWgbTreeMap<>(200, 7, false),
                new FwMersenneAccWgbTreeMap<>(300, 7, false),
                new FwMersenneAccWgbTreeMap<>(600, 7, false),

                new FwMersenneDecWgbTreeMap<>(100, 7, false),
                new FwMersenneDecWgbTreeMap<>(150, 7, false),
                new FwMersenneDecWgbTreeMap<>(200, 7, false),
                new FwMersenneDecWgbTreeMap<>(300, 7, false),
                new FwMersenneDecWgbTreeMap<>(600, 7, false),
                new FwMersenneDecWgbTreeMap<>(600, 13, false),
                new MersenneDecWgbTreeMap<>(600, 13, false),
                new FwMersenneAccWgbTreeMap<>(600, 13, false),
                new MersenneAccWgbTreeMap<>(600, 13, false),
                new WGBPowerTreeMap<>(600, 13, false),

                new FwMersenneAccWgbTreeMap<>(100, 13, false),
                new FwMersenneAccWgbTreeMap<>(150, 13, false),
                new FwMersenneAccWgbTreeMap<>(200, 13, false),
                new FwMersenneAccWgbTreeMap<>(300, 13, false),

                new FwMersenneDecWgbTreeMap<>(100, 13, false),
                new FwMersenneDecWgbTreeMap<>(150, 13, false),
                new FwMersenneDecWgbTreeMap<>(200, 13, false),
                new FwMersenneDecWgbTreeMap<>(300, 13, false),

                new MersenneAccWgbTreeMap<>(100, 7, false),
                new MersenneAccWgbTreeMap<>(150, 7, false),
                new MersenneAccWgbTreeMap<>(200, 7, false),
                new MersenneAccWgbTreeMap<>(300, 7, false),
                new MersenneAccWgbTreeMap<>(600, 7, false),

                new MersenneDecWgbTreeMap<>(100, 7, false),
                new MersenneDecWgbTreeMap<>(150, 7, false),
                new MersenneDecWgbTreeMap<>(200, 7, false),
                new MersenneDecWgbTreeMap<>(300, 7, false),
                new MersenneDecWgbTreeMap<>(600, 7, false),

                new MersenneAccWgbTreeMap<>(100, 13, false),
                new MersenneAccWgbTreeMap<>(150, 13, false),
                new MersenneAccWgbTreeMap<>(200, 13, false),
                new MersenneAccWgbTreeMap<>(300, 13, false),

                new MersenneDecWgbTreeMap<>(100, 13, false),
                new MersenneDecWgbTreeMap<>(150, 13, false),
                new MersenneDecWgbTreeMap<>(200, 13, false),
                new MersenneDecWgbTreeMap<>(300, 13, false),

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

                new WGBPowerTreeMap<>(100, 11, false),
                new WGBPowerTreeMap<>(150, 11, false),
                new WGBPowerTreeMap<>(200, 11, false),
                new WGBPowerTreeMap<>(300, 11, false),
                new WGBPowerTreeMap<>(600, 11, false),

                new WGBPowerTreeMap<>(100, 12, false),
                new WGBPowerTreeMap<>(150, 12, false),
                new WGBPowerTreeMap<>(200, 12, false),
                new WGBPowerTreeMap<>(300, 12, false),
                new WGBPowerTreeMap<>(600, 12, false),

                new WGBPowerTreeMap<>(100, 13, false),
                new WGBPowerTreeMap<>(150, 13, false),
                new WGBPowerTreeMap<>(200, 13, false),
                new WGBPowerTreeMap<>(300, 13, false)
        );

        deleteTables();
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
                tree.getBetweenAsc("30000000-0000-0000-0000-000000000000", "40000000-0000-0000-0000-000000000000");
            }
            tree.clear();
        });

        for (int rep = 1; rep <= MAX_REPEAT; rep++) {
            int finalRep = rep;
            int finalTotalCount = (int) Math.min(MIN_TOTAL_COUNT * Math.pow(2, rep - 1), MAX_TOTAL_COUNT);

            trees.forEach(tree -> {
                int calculatingGroupSize;

                for (int count = 0; count <= finalTotalCount; count += calculatingGroupSize) {

                    if (count >= 4_000_000) {
                        calculatingGroupSize = 10_000;
                    } else if (count >= 2_000_000) {
                        calculatingGroupSize = 5000;
                    } else if (count >= 1_000_000) {
                        calculatingGroupSize = 3000;
                    } else if (count >= 500_000) {
                        calculatingGroupSize = 2000;
                    } else if (count >= 250_000) {
                        calculatingGroupSize = 1000;
                    } else if (count >= 100_000) {
                        calculatingGroupSize = 500;
                    } else if (count >= 50_000) {
                        calculatingGroupSize = 250;
                    } else if (count >= 10_000) {
                        calculatingGroupSize = 100;
                    } else {
                        calculatingGroupSize = MIN_CALCULATING_GROUP_SIZE;
                    }

                    try {
                        System.out.println(" > Tree " + tree.getName() + " > Rep: " + finalRep + ", Count: " + count);

                        var randomUuids = IntStream.range(0, calculatingGroupSize).mapToObj(i -> UUID.randomUUID())
                                .map(UUID::toString)
                                .collect(Collectors.toSet());

                        long insertTime = randomUuids.stream()
                                .map(uuid -> measureTime(() -> tree.put(uuid, true)))
                                .reduce(0L, Long::sum) / calculatingGroupSize;

                        if (count < MIN_COUNT_TO_WRITE) {
                            continue;
                        }

                        writeToTable("insert", count + calculatingGroupSize, tree.getName(), insertTime);

                        long searchTime = randomUuids.stream()
                                .map(uuid -> measureTime(() -> tree.get(uuid)))
                                .reduce(0L, Long::sum) / calculatingGroupSize;

                        writeToTable("search", count + calculatingGroupSize, tree.getName(), searchTime);

                        long searchMinTime = measureTime(tree::getMin);

                        writeToTable("searchMin", count + calculatingGroupSize, tree.getName(), searchMinTime);

                        long searchMaxTime = measureTime(tree::getMax);

                        writeToTable("searchMax", count + calculatingGroupSize, tree.getName(), searchMaxTime);

                        long depth = tree.depth();

                        writeToTable("depth", count + calculatingGroupSize, tree.getName(), depth);

                        long searchRangeTime = measureTime(() -> tree.getBetweenAsc("30000000-0000-0000-0000-000000000000", "40000000-0000-0000-0000-000000000000"));

                        writeToTable("searchRange", count + calculatingGroupSize, tree.getName(), searchRangeTime);

                        if (count % 1_000 == 0) {
                            System.gc();
                        }
                    } catch (Exception e) {
                        System.out.println(" > Error: " + e.getMessage() + ", stopped on count: " + count);
                        break;
                    }
                }

                flushForce();

                tree.clear();
                System.gc();
            });
        }
    }
}
