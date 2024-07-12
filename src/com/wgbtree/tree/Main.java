package com.wgbtree.tree;

import com.wgbtree.tree.bplus.BPlusTreeMap;
import com.wgbtree.tree.redblack.TreeMapAsTree;
import com.wgbtree.tree.whitegreyblackplus.WhiteGreyBlackTreeMap;

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
	private static final int MIN_TOTAL_COUNT = 500_000;
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
				new BPlusTreeMap<String, Boolean>(3),
				new BPlusTreeMap<String, Boolean>(20),
				new BPlusTreeMap<String, Boolean>(50),
				new BPlusTreeMap<String, Boolean>(100),
				new BPlusTreeMap<String, Boolean>(150),
				new BPlusTreeMap<String, Boolean>(200),
				new WhiteGreyBlackTreeMap<String, Boolean>(2, 20, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(2, 50, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(2, 100, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(2, 150, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(2, 200, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(3, 20, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(3, 50, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(3, 100, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(3, 150, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(3, 200, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(5, 20, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(5, 50, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(5, 100, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(5, 150, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(5, 200, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(7, 20, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(7, 50, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(7, 100, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(7, 150, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(7, 200, false),
				new WhiteGreyBlackTreeMap<String, Boolean>(1, false),
				new TreeMapAsTree<String, Boolean>(),
				new WhiteGreyBlackTreeMap<String, Boolean>() // default 10
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
