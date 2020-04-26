package com.wgbtree.tree.whitegreyblack.node.service;

import com.wgbtree.tree.whitegreyblack.node.model.WgbData;
import com.wgbtree.tree.whitegreyblack.node.model.WgbKey;
import com.wgbtree.tree.whitegreyblack.node.model.WgbNode;

import java.util.*;
import java.util.stream.Collectors;

public final class WgbNodeHandler {

    private WgbNodeHandler() {
    }

    public static <K extends Comparable<K>, T>
    int getNumberOfNodes(WgbNode<K, T> wgbNode) {
        if (Objects.isNull(wgbNode)) {
            return 0;
        }

        return wgbNode.getNodes()
                .stream()
                .map(WgbNodeHandler::getNumberOfNodes)
                .reduce(Integer::sum)
                .orElse(0) + 1;
    }

    public static <K extends Comparable<K>, T>
    int getNumberOfEmptyNodes(WgbNode<K, T> wgbNode) {
        if (Objects.isNull(wgbNode)) {
            return 0;
        }

        return wgbNode.getNodes()
                .stream()
                .map(WgbNodeHandler::getNumberOfEmptyNodes)
                .reduce(Integer::sum)
                .orElse(0) + wgbNode.getNumberOfEmptySlots();
    }

    public static <K extends Comparable<K>, T>
    boolean containsValue(WgbNode<K, T> wgbNode, T data) {
        if (Objects.isNull(wgbNode)) {
            return false;
        }

        if (wgbNode.getData().getValue().equals(data)) {
            return true;
        }

        for (WgbNode<K, T> childNode : wgbNode.getNodes()) {
            if (containsValue(childNode, data)) {
                return true;
            }
        }

        return false;
    }

    public static <K extends Comparable<K>, T>
    WgbData<K, T> get(WgbNode<K, T> wgbNode, WgbKey<K> key) {
        if (Objects.isNull(wgbNode)) {
            return null;
        }

        do {
            if (wgbNode.getKey().compareTo(key) == 0) {
                return wgbNode.getData();
            }

            wgbNode = wgbNode.get(wgbNode.nextIndex(key));
        } while (Objects.nonNull(wgbNode));

        return null;
    }

    public static <K extends Comparable<K>, T>
    Set<K> keySet(WgbNode<K, T> wgbNode) {
        if (Objects.isNull(wgbNode)) {
            return new HashSet<>();
        }

        Set<K> result = wgbNode.getNodes()
                .stream()
                .map(WgbNodeHandler::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        result.add(wgbNode.getKey().getValue());

        return result;
    }

    public static <K extends Comparable<K>, T>
    Set<Map.Entry<K, T>> entrySet(WgbNode<K, T> wgbNode) {
        if (Objects.isNull(wgbNode)) {
            return new HashSet<>();
        }

        Set<Map.Entry<K, T>> result = wgbNode.getNodes()
                .stream()
                .map(WgbNodeHandler::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        result.add(Map.entry(wgbNode.getKey().getValue(), wgbNode.getData().getValue()));

        return result;
    }
}