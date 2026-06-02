package com.wgbtree.tree.wgb.operations.rotate.range;

import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.operations.delete.range.GreyRemoverRange;
import com.wgbtree.tree.wgb.operations.insert.range.GreyInserterRange;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.wgb.constants.LeakPolicy.SMALLEST;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyRotatorRange {

    public static <K extends Comparable<K>, T>
    Grey<K, T> tryRotateLeft(Grey<K, T> grey, TreeConfig config) {
        if (!config.getBalanced()) {
            /*
             * The tree is not set to be balanced,
             * We don't need and cannot rotate
             */
            return grey;
        }

        if (grey.setLeakPolicy() == LARGEST) {
            /*
             * Since keys are sorted ascending, the greatest key is set to leak right
             * Hence, we won't do any rotation since we don't need it
             */
            return grey;
        }

        if (grey.getCountRight() == 0) {
            /*
             * If the right node is empty, we don't need and cannot rotate
             */
            return grey;
        }

        var entry = GreyRemoverRange.removeMinFromRight(grey);
        var leakedEntry = GreyInserterRange.insertHere(grey, entry);

        K leakedKey = leakedEntry.getKey();
        Set<T> leakedValue = leakedEntry.getValue();

        return GreyInserterRange.insertLeft(grey, leakedKey, leakedValue, new AtomicReference<>(), config);
    }

    public static <K extends Comparable<K>, T>
    Grey<K, T> tryRotateRight(Grey<K, T> grey, TreeConfig config) {
        if (!config.getBalanced()) {
            /*
             * The tree is not set to be balanced,
             * We don't need and cannot rotate
             */
            return grey;
        }

        if (grey.setLeakPolicy() == SMALLEST) {
            /*
             * Since keys are sorted ascending, the smallest key is set to leak left
             * Hence, we won't do any rotation since we don't need it
             */
            return grey;
        }

        if (grey.getCountLeft() == 0) {
            /*
             * If the left node is empty, we don't need and cannot rotate
             */
            return grey;
        }

        var entry = GreyRemoverRange.removeMaxFromLeft(grey);
        var leakedEntry = GreyInserterRange.insertHere(grey, entry);

        K leakedKey = leakedEntry.getKey();
        Set<T> leakedValue = leakedEntry.getValue();

        return GreyInserterRange.insertRight(grey, leakedKey, leakedValue, new AtomicReference<>(), config);
    }
}
