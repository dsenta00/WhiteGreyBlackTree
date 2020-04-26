package com.wgbtree.tree.whitegreyblack.node.service;

import com.wgbtree.tree.whitegreyblack.node.exception.NotFoundException;
import com.wgbtree.tree.whitegreyblack.node.exception.UniqueException;
import com.wgbtree.tree.whitegreyblack.node.model.*;
import com.wgbtree.tree.whitegreyblack.node.util.Prime;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wgbtree.tree.whitegreyblack.node.util.OrderListFlatter.flatAsc;
import static com.wgbtree.tree.whitegreyblack.node.util.OrderListFlatter.flatDesc;

public final class BlackNodeHandler {

    private BlackNodeHandler() {
    }

    public static <K extends Comparable<K>, T>
    void printDepth(BlackNode<K, T> blackNode, int depth) {
        if (Objects.isNull(blackNode)) {
            System.out.println("null");
            return;
        }

        System.out.println(blackNode.getKey());

        for (int i = 0; i < blackNode.getCapacity(); i++) {
            if (Objects.nonNull(blackNode.get(i))) {
                for (int j = 0; j < depth + 2; j++) {
                    System.out.print(" ");
                }

                System.out.print(i + "%" + blackNode.getCapacity() + ": ");
                GreyNodeHandler.printDepth(blackNode.get(i), depth + 4);
            }
        }
    }


    public static <K extends Comparable<K>, T>
    BlackNode<K, T> delete(BlackNode<K, T> blackNode, WgbKey<K> key) throws NotFoundException, UniqueException {
        if (Objects.isNull(blackNode)) {
            throw new NotFoundException();
        }

        int cmp = key.getValue().compareTo(blackNode.getKey().getValue());

        if (cmp > 0) {
            throw new NotFoundException();

        } else if (cmp < 0) {
            int index = blackNode.nextIndex(key);
            blackNode.setNode(index, GreyNodeHandler.delete(blackNode.get(index), key));
        } else {
            int biggestIndex = -1;
            WgbData<K, T> nextMax = null;

            for (int i = 0; i < blackNode.getCapacity(); i++) {
                GreyNode<K, T> greyNode = blackNode.get(i);
                if (Objects.isNull(greyNode)) {
                    continue;
                }

                if (Objects.isNull(nextMax)) {
                    nextMax = GreyNodeHandler.getMax(greyNode);
                    biggestIndex = i;
                    continue;
                }

                if (nextMax.getKey().compareTo(greyNode.getKey()) < 0) {
                    nextMax = greyNode.getData();
                    biggestIndex = i;
                }
            }

            if (biggestIndex == -1) {
                return null;
            } else {
                PopResult<K, T, GreyNode<K, T>> popResult = GreyNodeHandler.popMax(blackNode.get(biggestIndex));
                blackNode.setNode(biggestIndex, popResult.getNode());
                blackNode.setData(nextMax);

                blackNode.setDepth(blackNode
                        .getNodes()
                        .stream()
                        .map(WgbNode::getDepth)
                        .max(Integer::compareTo)
                        .orElse(0) + 1
                );
            }
        }

        return blackNode;
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, BlackNode<K, T>> popMax(BlackNode<K, T> blackNode) {
        if (Objects.isNull(blackNode)) {
            return new PopResult<>(null, null);
        }

        int biggestIndex = -1;
        WgbData<K, T> nextMax = null;

        for (int i = 0; i < blackNode.getCapacity(); i++) {
            GreyNode<K, T> greyNode = blackNode.get(i);
            if (Objects.isNull(greyNode)) {
                continue;
            }

            WgbData<K, T> candidate = GreyNodeHandler.getMax(greyNode);

            if (Objects.isNull(candidate)) {
                continue;
            }

            if (Objects.isNull(nextMax)) {
                nextMax = candidate;
                biggestIndex = i;
                continue;
            }

            if (nextMax.getKey().compareTo(candidate.getKey()) < 0) {
                nextMax = candidate;
                biggestIndex = i;
            }
        }

        if (biggestIndex == -1) {
            return new PopResult<>(null, blackNode.getData());
        } else {
            PopResult<K, T, GreyNode<K, T>> popResult = GreyNodeHandler.popMax(blackNode.get(biggestIndex));
            blackNode.setNode(biggestIndex, popResult.getNode());
            blackNode.setData(nextMax);

            blackNode.setDepth(blackNode
                    .getNodes()
                    .stream()
                    .map(WgbNode::getDepth)
                    .max(Integer::compareTo)
                    .orElse(0) + 1
            );

            return new PopResult<>(blackNode, blackNode.getData());
        }
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, BlackNode<K, T>> popMin(BlackNode<K, T> blackNode) {
        if (Objects.isNull(blackNode)) {
            return new PopResult<>(null, null);
        }

        int smallestIndex = -1;
        WgbData<K, T> min = null;

        for (int i = 0; i < blackNode.getCapacity(); i++) {
            WgbData<K, T> newMin = GreyNodeHandler.getMin(blackNode.get(i));
            if (Objects.isNull(newMin)) {
                continue;
            }

            if (Objects.isNull(min) || min.getKey().compareTo(newMin.getKey()) > 0) {
                min = newMin;
                smallestIndex = i;
            }
        }

        if (smallestIndex == -1) {
            return new PopResult<>(null, blackNode.getData());
        } else {
            PopResult<K, T, GreyNode<K, T>> popResult = GreyNodeHandler.popMin(blackNode.get(smallestIndex));
            blackNode.setNode(smallestIndex, popResult.getNode());

            blackNode.setDepth(blackNode
                    .getNodes()
                    .stream()
                    .map(WgbNode::getDepth)
                    .max(Integer::compareTo)
                    .orElse(0) + 1
            );

            return new PopResult<>(blackNode, min);
        }
    }

    public static <K extends Comparable<K>, T>
    WgbData<K, T> getMax(BlackNode<K, T> blackNode) {
        return Objects.nonNull(blackNode) ? blackNode.getData() : null;
    }

    public static <K extends Comparable<K>, T>
    int depth(BlackNode<K, T> node) {
        if (Objects.isNull(node)) {
            return 0;
        }

        return node.getDepth();
    }

    public static <K extends Comparable<K>, T>
    BlackNode<K, T> insert(BlackNode<K, T> blackNode, int capacity, WgbData<K, T> data) throws UniqueException {
        if (Objects.isNull(blackNode)) {
            return new BlackNode<>(data, capacity);
        }

        int cmp = data.getKey().compareTo(blackNode.getKey());

        if (cmp > 0) {
            WgbData<K, T> nodeData = blackNode.getData();

            GreyNode<K, T> greyNode = GreyNodeHandler.insert(
                    blackNode.next(nodeData.getKey()),
                    Prime.nextPrime(capacity),
                    nodeData
            );

            if (greyNode.getDepth() >= blackNode.getDepth()) {
                blackNode.setDepth(greyNode.getDepth() + 1);
            }

            blackNode.setNode(blackNode.nextIndex(greyNode.getKey()), greyNode);
            blackNode.setData(data);
        } else if (cmp < 0) {
            GreyNode<K, T> greyNode = GreyNodeHandler.insert(
                    blackNode.next(data.getKey()),
                    Prime.nextPrime(capacity),
                    data
            );

            if (greyNode.getDepth() >= blackNode.getDepth()) {
                blackNode.setDepth(greyNode.getDepth() + 1);
            }

            blackNode.setNode(blackNode.nextIndex(greyNode.getKey()), greyNode);
        } else {
            throw new UniqueException(String.format("Data %s already exists!", data));
        }

        return blackNode;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getAsc(BlackNode<K, T> blackNode) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode.getNodes()
                .stream()
                .map(o -> (GreyNode<K, T>) o)
                .map(GreyNodeHandler::getAsc)
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = flatAsc(inOrderLists);
        result.add(blackNode.getData());

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getDesc(BlackNode<K, T> blackNode) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode.getNodes()
                .stream()
                .map(o -> (GreyNode<K, T>) o)
                .map(GreyNodeHandler::getDesc)
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = new LinkedList<>();
        result.add(blackNode.getData());
        result.addAll(flatDesc(inOrderLists));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getBiggerThanAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp <= 0) {
            // since black redbms.core.tree.whitegreyblack.node is smaller or equals key
            // all child nodes are also smaller or equals key
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBiggerThanAsc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = flatAsc(inOrderLists);
        result.add(blackNode.getData());

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp <= 0) {
            // since black redbms.core.tree.whitegreyblack.node is smaller or equals key
            // all child nodes are also smaller or equals key
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBiggerThanDesc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = new LinkedList<>();
        result.add(blackNode.getData());
        result.addAll(flatDesc(inOrderLists));

        return result;

    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            return getAsc(blackNode);
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getLessThanAsc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        return flatAsc(inOrderLists);
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getLessThanDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            return getDesc(blackNode);
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getLessThanDesc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        return flatDesc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanEqualsAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            // since black redbms.core.tree.whitegreyblack.node is smaller than key
            // all child nodes are also smaller than key
            return new LinkedList<>();
        } else if (cmp == 0) {
            // only black redbms.core.tree.whitegreyblack.node fits
            List<WgbData<K, T>> result = new LinkedList<>();
            result.add(blackNode.getData());
            return result;
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBiggerThanEqualsAsc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = flatAsc(inOrderLists);
        result.add(blackNode.getData());

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanEqualsDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            // since black redbms.core.tree.whitegreyblack.node is smaller than key
            // all child nodes are also smaller than key
            return new LinkedList<>();
        } else if (cmp == 0) {
            // only black redbms.core.tree.whitegreyblack.node fits
            List<WgbData<K, T>> result = new LinkedList<>();
            result.add(blackNode.getData());
            return result;
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBiggerThanEqualsDesc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = new LinkedList<>();
        result.add(blackNode.getData());
        result.addAll(flatDesc(inOrderLists));

        return result;

    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanEqualsAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp <= 0) {
            return getAsc(blackNode);
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getLessThanEqualsAsc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        return flatAsc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanEqualsDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp <= 0) {
            return getDesc(blackNode);
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getLessThanEqualsDesc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        return flatDesc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBetweenAsc(BlackNode<K, T> blackNode, WgbKey<K> low, WgbKey<K> high) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmpLow = blackNode.getKey().compareTo(low);

        if (cmpLow < 0) {
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBetweenAsc((GreyNode<K, T>) node, low, high))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = flatAsc(inOrderLists);
        int cmpHigh = blackNode.getKey().compareTo(high);
        if (cmpHigh <= 0) {
            result.add(blackNode.getData());
        }

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBetweenDesc(BlackNode<K, T> blackNode, WgbKey<K> low, WgbKey<K> high) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmpLow = blackNode.getKey().compareTo(low);

        if (cmpLow < 0) {
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = blackNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBetweenDesc((GreyNode<K, T>) node, low, high))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = new LinkedList<>();
        int cmpHigh = blackNode.getKey().compareTo(high);
        if (cmpHigh <= 0) {
            result.add(blackNode.getData());
        }
        result.addAll(flatDesc(inOrderLists));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getNotEqualsAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            return getAsc(blackNode);
        }

        int carefulIndex = blackNode.nextIndex(key);

        List<List<WgbData<K, T>>> inOrderLists = new LinkedList<>();

        for (int i = 0; i < blackNode.getCapacity(); i++) {
            if (i == carefulIndex) {
                inOrderLists.add(GreyNodeHandler.getNotEqualsAsc(blackNode.get(i), key));
            } else {
                inOrderLists.add(GreyNodeHandler.getAsc(blackNode.get(i)));
            }
        }

        return flatAsc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getNotEqualsDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (Objects.isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmpLow = blackNode.getKey().compareTo(key);

        if (cmpLow < 0) {
            return getDesc(blackNode);
        }

        int carefulIndex = blackNode.nextIndex(key);

        List<List<WgbData<K, T>>> inOrderLists = new LinkedList<>();

        for (int i = 0; i < blackNode.getCapacity(); i++) {
            if (i == carefulIndex) {
                inOrderLists.add(GreyNodeHandler.getNotEqualsDesc(blackNode.get(i), key));
            } else {
                inOrderLists.add(GreyNodeHandler.getDesc(blackNode.get(i)));
            }
        }

        return flatDesc(inOrderLists);
    }
}