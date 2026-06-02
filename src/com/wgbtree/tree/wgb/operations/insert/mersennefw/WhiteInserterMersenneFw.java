package com.wgbtree.tree.wgb.operations.insert.mersennefw;

import com.wgbtree.tree.wgb.calculator.MersenneCalculator;
import com.wgbtree.tree.wgb.creator.WhiteCreator;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.grey.FwGrey;
import com.wgbtree.tree.wgb.model.node.white.FwMersenneWhite;
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
public final class WhiteInserterMersenneFw {

	public static <K extends Comparable<K>, T>
	FwMersenneWhite<K, T> insert(FwMersenneWhite<K, T> white, K key, Set<T> value, int keyHash, AtomicReference<T> oldValue, TreeConfig config) {
		if (isNull(white)) {
			return WhiteCreator.createMersenneFw(key, value, config);
		}

		var entry = new SimpleEntry<>(key, value);
		var entries = white.getEntries();

		var leakEntry = new AtomicReference<Entry<K, Set<T>>>();
		entries.add(entry, leakEntry);

		var leakedEntry = leakEntry.get();
		if (isNull(leakedEntry)) {
			return white;
		}

		if (leakedEntry != entry) {
			key = leakedEntry.getKey();
			value = leakedEntry.getValue();
			keyHash = key.hashCode();
		}

		var greys = white.getGreys();
		int i = MersenneCalculator.mod(keyHash, config.getRank(), config.getPower());

		if (config.reachedRankLimit()) {
			greys[i] = GreyInserterRange.insert(greys[i], key, value, oldValue, config);
		} else {
			boolean shouldLinkGrey = greys[i] == null;
			greys[i] = GreyInserterMersenneFw.insert((FwGrey<K, T>) greys[i], key, value, keyHash, oldValue, config.nextRank());

			if (shouldLinkGrey) {
				linkGrey(white, (FwGrey<K, T>) greys[i]);
			}
		}

		return white;
	}

	private static <K extends Comparable<K>, T>
	void linkGrey(@NonNull FwMersenneWhite<K, T> white, @NonNull FwGrey<K, T> grey) {
		grey.fwRight = white.greyRoot;

		if (white.greyRoot != null) {
			white.greyRoot.fwLeft = grey;
		}

		white.greyRoot = grey;
	}
}
