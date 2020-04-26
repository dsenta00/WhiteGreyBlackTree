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
import static java.util.Objects.isNull;

public final class WhiteNodeHandler {

    private WhiteNodeHandler() {
    }

    public static <K extends Comparable<K>, T>
    void printDepth(WhiteNode<K, T> whiteNode, int depth) {
        if (isNull(whiteNode)) {
            System.out.println("null");
            return;
        }

        System.out.println(whiteNode.getKey());

        for (int i = 0; i < whiteNode.getCapacity(); i++) {
            if (Objects.nonNull(whiteNode.get(i))) {
                for (int j = 0; j < depth + 2; j++) {
                    System.out.print(" ");
                }

                System.out.print(i + "%" + whiteNode.getCapacity() + ": ");
                GreyNodeHandler.printDepth(whiteNode.get(i), depth + 4);
            }
        }
    }

    public static <K extends Comparable<K>, T>
    WgbData<K, T> getMin(WhiteNode<K, T> whiteNode) {
        return Objects.nonNull(whiteNode) ? whiteNode.getData() : null;
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, WhiteNode<K, T>> popMin(WhiteNode<K, T> whiteNode) {
        if (isNull(whiteNode)) {
            return new PopResult<>(null, null);
        }

        int smallestIndex = -1;
        WgbData<K, T> nextMin = null;

        for (int i = 0; i < whiteNode.getCapacity(); i++) {
            GreyNode<K, T> greyNode = whiteNode.get(i);
            if (isNull(greyNode)) {
                continue;
            }

            WgbData<K, T> candidate = GreyNodeHandler.getMin(greyNode);

            if (isNull(candidate)) {
                continue;
            }

            if (isNull(nextMin)) {
                nextMin = candidate;
                smallestIndex = i;
                continue;
            }

            if (nextMin.getKey().compareTo(candidate.getKey()) > 0) {
                nextMin = candidate;
                smallestIndex = i;
            }
        }

        WgbData<K, T> min = whiteNode.getData();

        if (smallestIndex == -1) {
            return new PopResult<>(null, min);
        } else {
            PopResult<K, T, GreyNode<K, T>> popResult = GreyNodeHandler.popMin(whiteNode.get(smallestIndex));
            whiteNode.setNode(smallestIndex, popResult.getNode());
            whiteNode.setData(nextMin);

            whiteNode.setDepth(whiteNode
                    .getNodes()
                    .stream()
                    .map(WgbNode::getDepth)
                    .max(Integer::compareTo)
                    .orElse(0) + 1
            );

            return new PopResult<>(whiteNode, min);
        }
    }

    public static <K extends Comparable<K>, T>
    WhiteNode<K, T> delete(WhiteNode<K, T> whiteNode, WgbKey<K> key) throws NotFoundException, UniqueException {
        if (isNull(whiteNode)) {
            throw new NotFoundException();
        }

        int cmp = key.compareTo(whiteNode.getKey());

        if (cmp < 0) {
            throw new NotFoundException();
        } else if (cmp > 0) {
            int index = whiteNode.nextIndex(key);
            whiteNode.setNode(index, GreyNodeHandler.delete(whiteNode.get(index), key));
        } else {
            int smallestIndex = -1;
            WgbData<K, T> nextMin = null;

            for (int i = 0; i < whiteNode.getCapacity(); i++) {
                GreyNode<K, T> greyNode = whiteNode.get(i);
                if (isNull(greyNode)) {
                    continue;
                }

                if (isNull(nextMin)) {
                    nextMin = GreyNodeHandler.getMin(greyNode);
                    smallestIndex = i;
                    continue;
                }

                if (nextMin.getKey().compareTo(greyNode.getKey()) > 0) {
                    nextMin = greyNode.getData();
                    smallestIndex = i;
                }
            }

            if (smallestIndex == -1) {
                return null;
            } else {
                PopResult<K, T, GreyNode<K, T>> popResult = GreyNodeHandler.popMin(whiteNode.get(smallestIndex));
                whiteNode.setNode(smallestIndex, popResult.getNode());
                whiteNode.setData(nextMin);

                whiteNode.setDepth(whiteNode
                        .getNodes()
                        .stream()
                        .map(WgbNode::getDepth)
                        .max(Integer::compareTo)
                        .orElse(0) + 1
                );
            }
        }

        return whiteNode;
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, WhiteNode<K, T>> popMax(WhiteNode<K, T> whiteNode) {
        if (isNull(whiteNode)) {
            return new PopResult<>(null, null);
        }

        int biggestIndex = -1;
        WgbData<K, T> max = null;

        for (int i = 0; i < whiteNode.getCapacity(); i++) {
            WgbData<K, T> newMax = GreyNodeHandler.getMax(whiteNode.get(i));
            if (isNull(newMax)) {
                continue;
            }

            if (isNull(max) || max.getKey().compareTo(newMax.getKey()) < 0) {
                max = newMax;
                biggestIndex = i;
            }
        }

        if (biggestIndex == -1) {
            return new PopResult<>(null, whiteNode.getData());
        } else {
            PopResult<K, T, GreyNode<K, T>> popResult = GreyNodeHandler.popMax(whiteNode.get(biggestIndex));
            whiteNode.setNode(biggestIndex, popResult.getNode());

            whiteNode.setDepth(whiteNode
                    .getNodes()
                    .stream()
                    .map(WgbNode::getDepth)
                    .max(Integer::compareTo)
                    .orElse(0) + 1
            );

            return new PopResult<>(whiteNode, max);
        }
    }

    public static <K extends Comparable<K>, T>
    int depth(WhiteNode<K, T> node) {
        if (isNull(node)) {
            return 0;
        }

        return node.getDepth();
    }

    public static <K extends Comparable<K>, T>
    WhiteNode<K, T> insert(WhiteNode<K, T> whiteNode, int capacity, WgbData<K, T> data) throws UniqueException {
        if (isNull(whiteNode)) {
            return new WhiteNode<>(data, capacity);
        }

        int cmp = data.getKey().compareTo(whiteNode.getKey());

        if (cmp < 0) {
            WgbData<K, T> nodeData = whiteNode.getData();

            GreyNode<K, T> greyNode = GreyNodeHandler.insert(
                    whiteNode.next(nodeData.getKey()),
                    Prime.nextPrime(capacity),
                    nodeData
            );

            if (greyNode.getDepth() >= whiteNode.getDepth()) {
                whiteNode.setDepth(greyNode.getDepth() + 1);
            }

            whiteNode.setNode(whiteNode.nextIndex(greyNode.getKey()), greyNode);
            whiteNode.setData(data);
        } else if (cmp > 0) {
            GreyNode<K, T> greyNode = GreyNodeHandler.insert(
                    whiteNode.next(data.getKey()),
                    Prime.nextPrime(capacity),
                    data
            );

            if (greyNode.getDepth() >= whiteNode.getDepth()) {
                whiteNode.setDepth(greyNode.getDepth() + 1);
            }

            whiteNode.setNode(whiteNode.nextIndex(greyNode.getKey()), greyNode);
        } else {
            throw new UniqueException(String.format("Data %s already exists!", data));
        }

        return whiteNode;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getAsc(WhiteNode<K, T> whiteNode) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode.getNodes()
                .stream()
                .map(o -> (GreyNode<K, T>) o)
                .map(GreyNodeHandler::getAsc)
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = new LinkedList<>();
        result.add(whiteNode.getData());
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getDesc(WhiteNode<K, T> whiteNode) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode.getNodes()
                .stream()
                .map(o -> (GreyNode<K, T>) o)
                .map(GreyNodeHandler::getDesc)
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = flatDesc(inOrderLists);
        result.add(whiteNode.getData());

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getBiggerThanAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            // This node and all nodes below are surely bigger than key
            // therefore return everything asc

            return getAsc(whiteNode);
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBiggerThanAsc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        return flatAsc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            // This node and all nodes below are surely bigger than key
            // therefore return everything desc

            return getDesc(whiteNode);
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBiggerThanDesc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        return flatDesc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp >= 0) {
            // white node is bigger or equals key
            // all child nodes are also bigger or equals key
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getLessThanAsc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = new LinkedList<>();
        result.add(whiteNode.getData());
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getLessThanDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp >= 0) {
            // white node is bigger or equals key
            // all child nodes are also bigger or equals key
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getLessThanDesc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = flatDesc(inOrderLists);
        result.add(whiteNode.getData());

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanEqualsAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp >= 0) {
            // This node and all nodes below are surely bigger than key
            // therefore return everything asc
            return getAsc(whiteNode);
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBiggerThanEqualsAsc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        return flatAsc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanEqualsDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp >= 0) {
            // This node and all nodes below are surely bigger than key
            // therefore return everything asc
            return getDesc(whiteNode);
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBiggerThanEqualsDesc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        return flatDesc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanEqualsAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            // white node is bigger than key
            // all child nodes are also than key
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getLessThanEqualsAsc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = new LinkedList<>();
        result.add(whiteNode.getData());
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanEqualsDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            // white node is bigger than key
            // all child nodes are also than key
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getLessThanEqualsDesc((GreyNode<K, T>) node, key))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = flatDesc(inOrderLists);
        result.add(whiteNode.getData());

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBetweenAsc(WhiteNode<K, T> whiteNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmpHigh = whiteNode.getKey().compareTo(high);

        if (cmpHigh > 0) {
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBetweenAsc((GreyNode<K, T>) node, low, high))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = new LinkedList<>();
        int cmpLow = whiteNode.getKey().compareTo(low);
        if (cmpLow >= 0) {
            result.add(whiteNode.getData());
        }
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBetweenDesc(WhiteNode<K, T> whiteNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmpHigh = whiteNode.getKey().compareTo(high);

        if (cmpHigh > 0) {
            return new LinkedList<>();
        }

        List<List<WgbData<K, T>>> inOrderLists = whiteNode
                .getNodes()
                .stream()
                .map(node -> GreyNodeHandler.getBetweenDesc((GreyNode<K, T>) node, low, high))
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        List<WgbData<K, T>> result = flatDesc(inOrderLists);
        int cmpLow = whiteNode.getKey().compareTo(low);
        if (cmpLow >= 0) {
            result.add(whiteNode.getData());
        }

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getNotEqualsAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            return getAsc(whiteNode);
        }

        int carefulIndex = whiteNode.nextIndex(key);
        List<List<WgbData<K, T>>> inOrderLists = new LinkedList<>();

        for (int i = 0; i < whiteNode.getCapacity(); i++) {
            if (i == carefulIndex) {
                inOrderLists.add(GreyNodeHandler.getNotEqualsAsc(whiteNode.get(i), key));
            } else {
                inOrderLists.add(GreyNodeHandler.getAsc(whiteNode.get(i)));
            }
        }

        List<WgbData<K, T>> result = new LinkedList<>();
        if (cmp != 0) {
            result.add(whiteNode.getData());
        }
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getNotEqualsDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            return getDesc(whiteNode);
        }

        int carefulIndex = whiteNode.nextIndex(key);
        List<List<WgbData<K, T>>> inOrderLists = new LinkedList<>();

        for (int i = 0; i < whiteNode.getCapacity(); i++) {
            if (i == carefulIndex) {
                inOrderLists.add(GreyNodeHandler.getNotEqualsDesc(whiteNode.get(i), key));
            } else {
                inOrderLists.add(GreyNodeHandler.getDesc(whiteNode.get(i)));
            }
        }

        List<WgbData<K, T>> result = flatDesc(inOrderLists);
        if (cmp != 0) {
            result.add(whiteNode.getData());
        }

        return result;
    }
}