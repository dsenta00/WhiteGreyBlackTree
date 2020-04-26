package com.wgbtree.tree.whitegreyblack.node.util;

import com.wgbtree.tree.whitegreyblack.node.exception.ShouldNeverHappenException;
import com.wgbtree.tree.whitegreyblack.node.model.WgbData;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class OrderListFlatter {

    private OrderListFlatter() {
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> flatAsc(List<List<WgbData<K, T>>> inOrderLists) {
        List<WgbData<K, T>> result = new LinkedList<>();

        inOrderLists = inOrderLists
                .stream()
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        while (!inOrderLists.isEmpty()) {
            List<WgbData<K, T>> minList = inOrderLists
                    .stream()
                    .min(Comparator.comparing(list -> list.get(0).getKey()))
                    .orElseThrow(ShouldNeverHappenException::new);

            result.add(minList.get(0));
            minList.remove(0);

            inOrderLists = inOrderLists
                    .stream()
                    .filter(list -> !list.isEmpty())
                    .collect(Collectors.toList());
        }

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> flatDesc(List<List<WgbData<K, T>>> inOrderLists) {
        List<WgbData<K, T>> result = new LinkedList<>();

        inOrderLists = inOrderLists
                .stream()
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        while (!inOrderLists.isEmpty()) {
            List<WgbData<K, T>> maxList = inOrderLists
                    .stream()
                    .max(Comparator.comparing(list -> list.get(0).getKey()))
                    .orElseThrow(ShouldNeverHappenException::new);

            result.add(maxList.get(0));
            maxList.remove(0);

            inOrderLists = inOrderLists
                    .stream()
                    .filter(list -> !list.isEmpty())
                    .collect(Collectors.toList());
        }

        return result;
    }
}