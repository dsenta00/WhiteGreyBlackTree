package com.wgbtree.tree.wgb.operations.insert.mersennefw;

import com.wgbtree.tree.wgb.calculator.MersenneCalculator;
import com.wgbtree.tree.wgb.creator.BlackCreator;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.black.FwMersenneBlack;
import com.wgbtree.tree.wgb.model.node.grey.FwGrey;
import com.wgbtree.tree.wgb.operations.insert.range.GreyInserterRange;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackInserterMersenneFw {

	public static <K extends Comparable<K>, T>
	FwMersenneBlack<K, T> insert(FwMersenneBlack<K, T> black, K key, Set<T> value, int keyHash, AtomicReference<T> oldValue, TreeConfig config) {
		if (isNull(black)) {
			return BlackCreator.createMersenneFw(key, value, config);
		}

		var entry = new SimpleEntry<>(key, value);
		var entries = black.getEntries();

		var leakEntry = new AtomicReference<Entry<K, Set<T>>>();
		entries.add(entry, leakEntry);

		var leakedEntry = leakEntry.get();
		if (isNull(leakedEntry)) {
			return black;
		}

		if (leakedEntry != entry) {
			key = leakedEntry.getKey();
			value = leakedEntry.getValue();
			keyHash = key.hashCode();
		}

		var greys = black.getGreys();
		int i = MersenneCalculator.mod(keyHash, config.getRank(), config.getPower());

		if (config.reachedRankLimit()) {
			greys[i] = GreyInserterRange.insert(greys[i], key, value, oldValue, config);
		} else {
			boolean shouldLinkGrey = greys[i] == null;
			greys[i] = GreyInserterMersenneFw.insert((FwGrey<K, T>) greys[i], key, value, keyHash, oldValue, config.nextRank());

			if (shouldLinkGrey) {
				linkGrey(black, (FwGrey<K, T>) greys[i]);
			}
		}

		return black;
	}

	private static <K extends Comparable<K>, T>
	void linkGrey(@NonNull FwMersenneBlack<K, T> black, @NonNull FwGrey<K, T> grey) {
		grey.fwRight = black.greyRoot;

		if (black.greyRoot != null) {
			black.greyRoot.fwLeft = grey;
		}

		black.greyRoot = grey;
	}
}
