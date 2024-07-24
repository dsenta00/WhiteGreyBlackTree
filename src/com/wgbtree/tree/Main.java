package com.wgbtree.tree;

import com.wgbtree.tree.bplus.BPlusTreeMap;
import com.wgbtree.tree.redblack.TreeMapAsTree;
import com.wgbtree.tree.wgb.AccWGBTreeMap;
import com.wgbtree.tree.wgb.DecWGBTreeMap;
import com.wgbtree.tree.wgb.StraightWGBTreeMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main {

	private static final Map<String, Map<String, StringBuilder>> buffers = new HashMap<>();
	private static final int MIN_BUFFER_SIZE_LIMIT = 10_000;
	private static final int MAX_BUFFER_SIZE_LIMIT = 100_000;
	private static int bufferLimit = MIN_BUFFER_SIZE_LIMIT;
	private static final int MAX_REPEAT = 5;
	private static final int MIN_TOTAL_COUNT = 2000;
	private static final int MAX_TOTAL_COUNT = 8_000_000;

	public static void flushBuffer() {
		buffers.forEach((operation, map) -> map.forEach((treeName, buffer) -> flushBuffer(operation, treeName, buffer)));
	}

	public static void writeToCsvFile(String operation, int count, String treeName, long metric) {
		var buffer = buffers.computeIfAbsent(operation, k -> new HashMap<>())
				.computeIfAbsent(treeName, k -> new StringBuilder())
				.append(count)
				.append(",")
				.append(metric)
				.append("\n");

		flushBuffer(operation, treeName, buffer);
	}

	public static void flushBuffer(String operation, String treeName, StringBuilder buffer) {
		if (buffer.length() >= bufferLimit) {
			try (var writer = new FileWriter(operation + "_" + treeName + ".csv", true)) {
				writer.write(buffer.toString());
			} catch (Exception e) {
				System.out.println(" > Error writing to file: " + e.getMessage());
			} finally {
				buffer.setLength(0);
			}
		}
	}

	public static void createCsvFilesForTree(String treeName) {
		createCsvFileIfDoesNotExist("insert", treeName);
		createCsvFileIfDoesNotExist("search", treeName);
		createCsvFileIfDoesNotExist("searchMin", treeName);
		createCsvFileIfDoesNotExist("searchMax", treeName);
		createCsvFileIfDoesNotExist("depth", treeName);
	}

	public static void createCsvFileIfDoesNotExist(String operation, String treeName) {
		String filename = operation + "_" + treeName + ".csv";
		if (!new File(filename).exists())
			try (var writer = new FileWriter(filename)) {
				writer.write("count,metric\n");
			} catch (IOException e) {
				System.out.println("Error writing to file: " + e.getMessage());
			}
	}

	public static void createCsvFiles(List<AsTree<String, Boolean>> trees) {
		trees.forEach(tree -> createCsvFilesForTree(tree.getName()));
	}

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			flushBuffer(); // Ensure any remaining data is written to the file
			System.out.println("Shutdown hook triggered.");
		}));

		var trees = List.of(
				new TreeMapAsTree<String, Boolean>(),

				new BPlusTreeMap<String, Boolean>(3),
				new BPlusTreeMap<String, Boolean>(20),
				new BPlusTreeMap<String, Boolean>(50),
				new BPlusTreeMap<String, Boolean>(100),
				new BPlusTreeMap<String, Boolean>(150),
				new BPlusTreeMap<String, Boolean>(200),

				new StraightWGBTreeMap<String, Boolean>(),

				new StraightWGBTreeMap<String, Boolean>(3, false, false),
				new StraightWGBTreeMap<String, Boolean>(3, false, true),
				new StraightWGBTreeMap<String, Boolean>(3, true, false),
				new StraightWGBTreeMap<String, Boolean>(3, true, true),
				new StraightWGBTreeMap<String, Boolean>(20, false, false),
				new StraightWGBTreeMap<String, Boolean>(20, false, true),
				new StraightWGBTreeMap<String, Boolean>(20, true, false),
				new StraightWGBTreeMap<String, Boolean>(20, true, true),
				new StraightWGBTreeMap<String, Boolean>(50, false, false),
				new StraightWGBTreeMap<String, Boolean>(50, false, true),
				new StraightWGBTreeMap<String, Boolean>(50, true, false),
				new StraightWGBTreeMap<String, Boolean>(50, true, true),
				new StraightWGBTreeMap<String, Boolean>(100, false, false),
				new StraightWGBTreeMap<String, Boolean>(100, false, true),
				new StraightWGBTreeMap<String, Boolean>(100, true, false),
				new StraightWGBTreeMap<String, Boolean>(100, true, true),
				new StraightWGBTreeMap<String, Boolean>(150, false, false),
				new StraightWGBTreeMap<String, Boolean>(150, false, true),
				new StraightWGBTreeMap<String, Boolean>(150, true, false),
				new StraightWGBTreeMap<String, Boolean>(150, true, true),
				new StraightWGBTreeMap<String, Boolean>(200, false, false),
				new StraightWGBTreeMap<String, Boolean>(200, false, true),
				new StraightWGBTreeMap<String, Boolean>(200, true, false),
				new StraightWGBTreeMap<String, Boolean>(200, true, true),

				new AccWGBTreeMap<String, Boolean>(),

				new AccWGBTreeMap<String, Boolean>(3, 2, false, false),
				new AccWGBTreeMap<String, Boolean>(3, 2, false, true),
				new AccWGBTreeMap<String, Boolean>(3, 2, true, false),
				new AccWGBTreeMap<String, Boolean>(3, 2, true, true),
				new AccWGBTreeMap<String, Boolean>(20, 2, false, false),
				new AccWGBTreeMap<String, Boolean>(20, 2, false, true),
				new AccWGBTreeMap<String, Boolean>(20, 2, true, false),
				new AccWGBTreeMap<String, Boolean>(20, 2, true, true),
				new AccWGBTreeMap<String, Boolean>(50, 2, false, false),
				new AccWGBTreeMap<String, Boolean>(50, 2, false, true),
				new AccWGBTreeMap<String, Boolean>(50, 2, true, false),
				new AccWGBTreeMap<String, Boolean>(50, 2, true, true),
				new AccWGBTreeMap<String, Boolean>(100, 2, false, false),
				new AccWGBTreeMap<String, Boolean>(100, 2, false, true),
				new AccWGBTreeMap<String, Boolean>(100, 2, true, false),
				new AccWGBTreeMap<String, Boolean>(100, 2, true, true),
				new AccWGBTreeMap<String, Boolean>(150, 2, false, false),
				new AccWGBTreeMap<String, Boolean>(150, 2, false, true),
				new AccWGBTreeMap<String, Boolean>(150, 2, true, false),
				new AccWGBTreeMap<String, Boolean>(150, 2, true, true),
				new AccWGBTreeMap<String, Boolean>(200, 2, false, false),
				new AccWGBTreeMap<String, Boolean>(200, 2, false, true),
				new AccWGBTreeMap<String, Boolean>(200, 2, true, false),
				new AccWGBTreeMap<String, Boolean>(200, 2, true, true),

				new AccWGBTreeMap<String, Boolean>(3, 3, false, false),
				new AccWGBTreeMap<String, Boolean>(3, 3, false, true),
				new AccWGBTreeMap<String, Boolean>(3, 3, true, false),
				new AccWGBTreeMap<String, Boolean>(3, 3, true, true),
				new AccWGBTreeMap<String, Boolean>(20, 3, false, false),
				new AccWGBTreeMap<String, Boolean>(20, 3, false, true),
				new AccWGBTreeMap<String, Boolean>(20, 3, true, false),
				new AccWGBTreeMap<String, Boolean>(20, 3, true, true),
				new AccWGBTreeMap<String, Boolean>(50, 3, false, false),
				new AccWGBTreeMap<String, Boolean>(50, 3, false, true),
				new AccWGBTreeMap<String, Boolean>(50, 3, true, false),
				new AccWGBTreeMap<String, Boolean>(50, 3, true, true),
				new AccWGBTreeMap<String, Boolean>(100, 3, false, false),
				new AccWGBTreeMap<String, Boolean>(100, 3, false, true),
				new AccWGBTreeMap<String, Boolean>(100, 3, true, false),
				new AccWGBTreeMap<String, Boolean>(100, 3, true, true),
				new AccWGBTreeMap<String, Boolean>(150, 3, false, false),
				new AccWGBTreeMap<String, Boolean>(150, 3, false, true),
				new AccWGBTreeMap<String, Boolean>(150, 3, true, false),
				new AccWGBTreeMap<String, Boolean>(150, 3, true, true),
				new AccWGBTreeMap<String, Boolean>(200, 3, false, false),
				new AccWGBTreeMap<String, Boolean>(200, 3, false, true),
				new AccWGBTreeMap<String, Boolean>(200, 3, true, false),
				new AccWGBTreeMap<String, Boolean>(200, 3, true, true),

				new AccWGBTreeMap<String, Boolean>(3, 5, false, false),
				new AccWGBTreeMap<String, Boolean>(3, 5, false, true),
				new AccWGBTreeMap<String, Boolean>(3, 5, true, false),
				new AccWGBTreeMap<String, Boolean>(3, 5, true, true),
				new AccWGBTreeMap<String, Boolean>(20, 5, false, false),
				new AccWGBTreeMap<String, Boolean>(20, 5, false, true),
				new AccWGBTreeMap<String, Boolean>(20, 5, true, false),
				new AccWGBTreeMap<String, Boolean>(20, 5, true, true),
				new AccWGBTreeMap<String, Boolean>(50, 5, false, false),
				new AccWGBTreeMap<String, Boolean>(50, 5, false, true),
				new AccWGBTreeMap<String, Boolean>(50, 5, true, false),
				new AccWGBTreeMap<String, Boolean>(50, 5, true, true),
				new AccWGBTreeMap<String, Boolean>(100, 5, false, false),
				new AccWGBTreeMap<String, Boolean>(100, 5, false, true),
				new AccWGBTreeMap<String, Boolean>(100, 5, true, false),
				new AccWGBTreeMap<String, Boolean>(100, 5, true, true),
				new AccWGBTreeMap<String, Boolean>(150, 5, false, false),
				new AccWGBTreeMap<String, Boolean>(150, 5, false, true),
				new AccWGBTreeMap<String, Boolean>(150, 5, true, false),
				new AccWGBTreeMap<String, Boolean>(150, 5, true, true),
				new AccWGBTreeMap<String, Boolean>(200, 5, false, false),
				new AccWGBTreeMap<String, Boolean>(200, 5, false, true),
				new AccWGBTreeMap<String, Boolean>(200, 5, true, false),
				new AccWGBTreeMap<String, Boolean>(200, 5, true, true),

				new AccWGBTreeMap<String, Boolean>(3, 7, false, false),
				new AccWGBTreeMap<String, Boolean>(3, 7, false, true),
				new AccWGBTreeMap<String, Boolean>(3, 7, true, false),
				new AccWGBTreeMap<String, Boolean>(3, 7, true, true),
				new AccWGBTreeMap<String, Boolean>(20, 7, false, false),
				new AccWGBTreeMap<String, Boolean>(20, 7, false, true),
				new AccWGBTreeMap<String, Boolean>(20, 7, true, false),
				new AccWGBTreeMap<String, Boolean>(20, 7, true, true),
				new AccWGBTreeMap<String, Boolean>(50, 7, false, false),
				new AccWGBTreeMap<String, Boolean>(50, 7, false, true),
				new AccWGBTreeMap<String, Boolean>(50, 7, true, false),
				new AccWGBTreeMap<String, Boolean>(50, 7, true, true),
				new AccWGBTreeMap<String, Boolean>(100, 7, false, false),
				new AccWGBTreeMap<String, Boolean>(100, 7, false, true),
				new AccWGBTreeMap<String, Boolean>(100, 7, true, false),
				new AccWGBTreeMap<String, Boolean>(100, 7, true, true),
				new AccWGBTreeMap<String, Boolean>(150, 7, false, false),
				new AccWGBTreeMap<String, Boolean>(150, 7, false, true),
				new AccWGBTreeMap<String, Boolean>(150, 7, true, false),
				new AccWGBTreeMap<String, Boolean>(150, 7, true, true),
				new AccWGBTreeMap<String, Boolean>(200, 7, false, false),
				new AccWGBTreeMap<String, Boolean>(200, 7, false, true),
				new AccWGBTreeMap<String, Boolean>(200, 7, true, false),
				new AccWGBTreeMap<String, Boolean>(200, 7, true, true),

				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT),

				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 3, false, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 3, false, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 3, true, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 3, true, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 20, false, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 20, false, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 20, true, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 20, true, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 50, false, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 50, false, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 50, true, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 50, true, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 100, false, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 100, false, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 100, true, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 100, true, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 150, false, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 150, false, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 150, true, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 150, true, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 200, false, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 200, false, true),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 200, true, false),
				new DecWGBTreeMap<String, Boolean>(MIN_TOTAL_COUNT, 200, true, true),

				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 3, false, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 3, false, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 3, true, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 3, true, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 20, false, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 20, false, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 20, true, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 20, true, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 50, false, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 50, false, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 50, true, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 50, true, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 100, false, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 100, false, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 100, true, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 100, true, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 150, false, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 150, false, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 150, true, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 150, true, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 200, false, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 200, false, true),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 200, true, false),
				new DecWGBTreeMap<String, Boolean>(MAX_TOTAL_COUNT, 200, true, true)
		);

		createCsvFiles(trees);

		for (int rep = 1; rep <= MAX_REPEAT; rep++) {
			int finalRep = rep;
			int finalTotalCount = (int) Math.min(MIN_TOTAL_COUNT * Math.pow(2, rep-1), MAX_TOTAL_COUNT);

			trees.forEach(tree -> {
				for (int count = 1; count < finalTotalCount; count++) {
					try {
						if (count % 1_000 == 0) {
							System.out.println(" > Tree " + tree.getName() + " > Rep: " + finalRep + ", Count: " + count);
						}

						String randomString = UUID.randomUUID().toString();

						long insertTime = measureTime(() -> tree.put(randomString, true));
						writeToCsvFile("insert", count, tree.getName(), insertTime);

						long searchTime = measureTime(() -> tree.get(randomString));
						writeToCsvFile("search", count, tree.getName(), searchTime);

						long searchMinTime = measureTime(tree::getMin);
						writeToCsvFile("searchMin", count, tree.getName(), searchMinTime);

						long searchMaxTime = measureTime(tree::getMax);
						writeToCsvFile("searchMax", count, tree.getName(), searchMaxTime);

						long depth = tree.depth();
						writeToCsvFile("depth", count, tree.getName(), depth);

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
