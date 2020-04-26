package com.wgbtree.tree.whitegreyblack.node.service;

import com.wgbtree.tree.whitegreyblack.node.exception.NotFoundException;
import com.wgbtree.tree.whitegreyblack.node.exception.UniqueException;
import com.wgbtree.tree.whitegreyblack.node.model.*;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class GreyNodeHandler {

    private GreyNodeHandler() {
    }

    public static <K extends Comparable<K>, T>
    void printDepth(GreyNode<K, T> greyNode, int depth) {
        if (isNull(greyNode)) {
            System.out.println("null");
            return;
        }

        System.out.println(greyNode.getKey());

        if (nonNull(greyNode.getWhiteNode())) {
            for (int i = 0; i < depth + 2; i++) {
                System.out.print(" ");
            }
            System.out.print("L: ");
            WhiteNodeHandler.printDepth(greyNode.getWhiteNode(), depth + 4);
        }

        if (nonNull(greyNode.getBlackNode())) {
            for (int i = 0; i < depth + 2; i++) {
                System.out.print(" ");
            }
            System.out.print("R: ");
            BlackNodeHandler.printDepth(greyNode.getBlackNode(), depth + 4);
        }
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getAsc(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        List<WgbData<K, T>> result = WhiteNodeHandler.getAsc(greyNode.getWhiteNode());
        result.add(greyNode.getData());
        result.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<WgbData<K, T>> getDesc(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        List<WgbData<K, T>> result = BlackNodeHandler.getDesc(greyNode.getBlackNode());
        result.add(greyNode.getData());
        result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));

        return result;
    }

    public static <K extends Comparable<K>, T>
    GreyNode<K, T> delete(GreyNode<K, T> greyNode, WgbKey<K> key) throws NotFoundException, UniqueException {
        if (isNull(greyNode)) {
            throw new NotFoundException();
        }

        int cmp = key.compareTo(greyNode.getKey());

        if (cmp < 0) {
            WhiteNode<K, T> whiteNode = WhiteNodeHandler.delete(greyNode.getWhiteNode(), key);
            greyNode.setWhiteNode(whiteNode);
        } else if (cmp > 0) {
            BlackNode<K, T> blackNode = BlackNodeHandler.delete(greyNode.getBlackNode(), key);
            greyNode.setBlackNode(blackNode);
        } else {
            WhiteNode<K, T> whiteNode = greyNode.getWhiteNode();
            BlackNode<K, T> blackNode = greyNode.getBlackNode();

            if (isNull(whiteNode)) {
                PopResult<K, T, BlackNode<K, T>> popResult = BlackNodeHandler.popMin(blackNode);

                if (isNull(popResult.getData())) {
                    return null;
                }

                greyNode.setData(popResult.getData());
                greyNode.setBlackNode(popResult.getNode());
            } else {
                PopResult<K, T, WhiteNode<K, T>> popResult = WhiteNodeHandler.popMax(whiteNode);

                if (isNull(popResult.getData())) {
                    return null;
                }

                greyNode.setData(popResult.getData());
                greyNode.setWhiteNode(popResult.getNode());
            }
        }

        int leftDepth = WhiteNodeHandler.depth(greyNode.getWhiteNode());
        int rightDepth = BlackNodeHandler.depth(greyNode.getBlackNode());
        greyNode.setDepth(leftDepth > rightDepth ? leftDepth + 1 : rightDepth + 1);

        return rotateIfNeeded(greyNode);
    }

    public static <K extends Comparable<K>, T>
    WgbData<K, T> getMin(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return null;
        }

        WgbData<K, T> min = WhiteNodeHandler.getMin(greyNode.getWhiteNode());

        return nonNull(min) ? min : greyNode.getData();
    }

    public static <K extends Comparable<K>, T>
    WgbData<K, T> getMax(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return null;
        }

        WgbData<K, T> max = BlackNodeHandler.getMax(greyNode.getBlackNode());

        return nonNull(max) ? max : greyNode.getData();
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, GreyNode<K, T>> popMin(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return new PopResult<>(null, null);
        }

        WhiteNode<K, T> whiteNode = greyNode.getWhiteNode();

        if (isNull(whiteNode)) {
            WgbData<K, T> min = greyNode.getData();

            PopResult<K, T, BlackNode<K, T>> popResult = BlackNodeHandler.popMin(greyNode.getBlackNode());

            if (isNull(popResult.getData())) {
                return new PopResult<>(null, min);
            }

            greyNode.setBlackNode(popResult.getNode());
            greyNode.setData(popResult.getData());

            int leftDepth = WhiteNodeHandler.depth(greyNode.getWhiteNode());
            int rightDepth = BlackNodeHandler.depth(greyNode.getBlackNode());
            greyNode.setDepth(leftDepth > rightDepth ? leftDepth + 1 : rightDepth + 1);

            return new PopResult<>(greyNode, min);
        }

        PopResult<K, T, WhiteNode<K, T>> popResult = WhiteNodeHandler.popMin(whiteNode);
        greyNode.setWhiteNode(popResult.getNode());

        int leftDepth = WhiteNodeHandler.depth(greyNode.getWhiteNode());
        int rightDepth = BlackNodeHandler.depth(greyNode.getBlackNode());
        greyNode.setDepth(leftDepth > rightDepth ? leftDepth + 1 : rightDepth + 1);

        return new PopResult<>(greyNode, popResult.getData());
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, GreyNode<K, T>> popMax(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return new PopResult<>(null, null);
        }

        BlackNode<K, T> blackNode = greyNode.getBlackNode();

        if (isNull(blackNode)) {
            WgbData<K, T> max = greyNode.getData();
            PopResult<K, T, WhiteNode<K, T>> popResult = WhiteNodeHandler.popMax(greyNode.getWhiteNode());

            if (isNull(popResult.getData())) {
                return new PopResult<>(null, max);
            }

            greyNode.setWhiteNode(popResult.getNode());
            greyNode.setData(popResult.getData());

            int leftDepth = WhiteNodeHandler.depth(greyNode.getWhiteNode());
            int rightDepth = BlackNodeHandler.depth(greyNode.getBlackNode());
            greyNode.setDepth(leftDepth > rightDepth ? leftDepth + 1 : rightDepth + 1);

            return new PopResult<>(greyNode, max);
        }

        PopResult<K, T, BlackNode<K, T>> popResult = BlackNodeHandler.popMax(blackNode);
        greyNode.setBlackNode(popResult.getNode());

        int leftDepth = WhiteNodeHandler.depth(greyNode.getWhiteNode());
        int rightDepth = BlackNodeHandler.depth(greyNode.getBlackNode());
        greyNode.setDepth(leftDepth > rightDepth ? leftDepth + 1 : rightDepth + 1);

        return new PopResult<>(greyNode, popResult.getData());
    }

    public static <K extends Comparable<K>, T>
    GreyNode<K, T> rotateIfNeeded(GreyNode<K, T> greyNode) throws UniqueException {
        if (isNull(greyNode)) {
            return null;
        }

        int diff;

        do {
            WhiteNode<K, T> whiteNode = greyNode.getWhiteNode();
            BlackNode<K, T> blackNode = greyNode.getBlackNode();

            if (isNull(whiteNode) && isNull(blackNode)) {
                greyNode.setDepth(1);
                return greyNode;
            }

            diff = WhiteNodeHandler.depth(whiteNode) - BlackNodeHandler.depth(blackNode);

            if (diff <= -2) {
                PopResult<K, T, BlackNode<K, T>> popResult = BlackNodeHandler.popMin(blackNode);
                greyNode.setBlackNode(popResult.getNode());
                greyNode.setWhiteNode(WhiteNodeHandler.insert(whiteNode, blackNode.getCapacity(), greyNode.getData()));
                greyNode.setData(popResult.getData());
            } else if (diff >= 2) {
                PopResult<K, T, WhiteNode<K, T>> popResult = WhiteNodeHandler.popMax(whiteNode);
                greyNode.setWhiteNode(popResult.getNode());
                greyNode.setBlackNode(BlackNodeHandler.insert(blackNode, whiteNode.getCapacity(), greyNode.getData()));
                greyNode.setData(popResult.getData());
            } else {
                return greyNode;
            }

            int leftDepth = WhiteNodeHandler.depth(greyNode.getWhiteNode());
            int rightDepth = BlackNodeHandler.depth(greyNode.getBlackNode());
            greyNode.setDepth(leftDepth > rightDepth ? leftDepth + 1 : rightDepth + 1);
        } while (true);
    }

    public static <K extends Comparable<K>, T>
    GreyNode<K, T> insert(GreyNode<K, T> greyNode, int capacity, WgbData<K, T> data) throws UniqueException {
        if (isNull(greyNode)) {
            return new GreyNode<>(data);
        }

        int cmp = data.getKey().compareTo(greyNode.getKey());

        if (cmp < 0) {
            WhiteNode<K, T> whiteNode = WhiteNodeHandler.insert(greyNode.getWhiteNode(), capacity, data);

            if (whiteNode.getDepth() >= greyNode.getDepth()) {
                greyNode.setDepth(whiteNode.getDepth() + 1);
            }

            greyNode.setWhiteNode(whiteNode);
        } else if (cmp > 0) {
            BlackNode<K, T> blackNode = BlackNodeHandler.insert(greyNode.getBlackNode(), capacity, data);

            if (blackNode.getDepth() >= greyNode.getDepth()) {
                greyNode.setDepth(blackNode.getDepth() + 1);
            }

            greyNode.setBlackNode(blackNode);
        } else {
            throw new UniqueException(String.format("Data %s already exists!", data));
        }

        return rotateIfNeeded(greyNode);
    }

    public static <K extends Comparable<K>, T>
    int depth(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return 0;
        }

        return greyNode.getDepth();
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp > 0) {
            List<WgbData<K, T>> result = WhiteNodeHandler.getBiggerThanAsc(greyNode.getWhiteNode(), key);
            result.add(greyNode.getData());
            result.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));
            return result;
        }

        return BlackNodeHandler.getBiggerThanAsc(greyNode.getBlackNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp > 0) {
            List<WgbData<K, T>> result = BlackNodeHandler.getDesc(greyNode.getBlackNode());
            result.add(greyNode.getData());
            result.addAll(WhiteNodeHandler.getBiggerThanDesc(greyNode.getWhiteNode(), key));
            return result;
        }

        return BlackNodeHandler.getBiggerThanDesc(greyNode.getBlackNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp < 0) {
            List<WgbData<K, T>> result = WhiteNodeHandler.getDesc(greyNode.getWhiteNode());
            result.add(greyNode.getData());
            result.addAll(BlackNodeHandler.getLessThanAsc(greyNode.getBlackNode(), key));
            return result;
        }

        return WhiteNodeHandler.getLessThanAsc(greyNode.getWhiteNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp < 0) {
            List<WgbData<K, T>> result = BlackNodeHandler.getLessThanDesc(greyNode.getBlackNode(), key);
            result.add(greyNode.getData());
            result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));
            return result;
        }

        return WhiteNodeHandler.getLessThanDesc(greyNode.getWhiteNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanEqualsAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp >= 0) {
            List<WgbData<K, T>> result = WhiteNodeHandler.getBiggerThanEqualsAsc(greyNode.getWhiteNode(), key);
            result.add(greyNode.getData());
            result.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));
            return result;
        }

        return BlackNodeHandler.getBiggerThanEqualsAsc(greyNode.getBlackNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBiggerThanEqualsDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp >= 0) {
            List<WgbData<K, T>> result = BlackNodeHandler.getDesc(greyNode.getBlackNode());
            result.add(greyNode.getData());
            result.addAll(WhiteNodeHandler.getBiggerThanEqualsDesc(greyNode.getWhiteNode(), key));
            return result;
        }

        return BlackNodeHandler.getBiggerThanEqualsDesc(greyNode.getBlackNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanEqualsAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp <= 0) {
            List<WgbData<K, T>> result = WhiteNodeHandler.getAsc(greyNode.getWhiteNode());
            result.add(greyNode.getData());
            result.addAll(BlackNodeHandler.getLessThanEqualsAsc(greyNode.getBlackNode(), key));
            return result;
        }

        return WhiteNodeHandler.getLessThanEqualsAsc(greyNode.getWhiteNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getLessThanEqualsDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp <= 0) {
            List<WgbData<K, T>> result = BlackNodeHandler.getLessThanEqualsDesc(greyNode.getBlackNode(), key);
            result.add(greyNode.getData());
            result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));
            return result;
        }

        return WhiteNodeHandler.getLessThanEqualsDesc(greyNode.getWhiteNode(), key);

    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBetweenAsc(GreyNode<K, T> greyNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmpLow = greyNode.getKey().compareTo(low);

        if (cmpLow < 0) {
            // go right
            return BlackNodeHandler.getBetweenAsc(greyNode.getBlackNode(), low, high);
        }

        List<WgbData<K, T>> result = WhiteNodeHandler.getBetweenAsc(greyNode.getWhiteNode(), low, high);
        int cmpHigh = greyNode.getKey().compareTo(high);
        if (cmpHigh <= 0) {
            result.add(greyNode.getData());
        }
        result.addAll(BlackNodeHandler.getBetweenAsc(greyNode.getBlackNode(), low, high));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getBetweenDesc(GreyNode<K, T> greyNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmpLow = greyNode.getKey().compareTo(low);

        if (cmpLow < 0) {
            // go right
            return BlackNodeHandler.getBetweenDesc(greyNode.getBlackNode(), low, high);
        }

        List<WgbData<K, T>> result = BlackNodeHandler.getBetweenDesc(greyNode.getBlackNode(), low, high);
        int cmpHigh = greyNode.getKey().compareTo(high);
        if (cmpHigh <= 0) {
            result.add(greyNode.getData());
        }
        result.addAll(WhiteNodeHandler.getBetweenDesc(greyNode.getWhiteNode(), low, high));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getNotEqualsAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp == 0) {
            List<WgbData<K, T>> result = WhiteNodeHandler.getAsc(greyNode.getWhiteNode());
            result.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));
            return result;
        } else if (cmp < 0) {
            List<WgbData<K, T>> result = WhiteNodeHandler.getAsc(greyNode.getWhiteNode());
            result.add(greyNode.getData());
            result.addAll(BlackNodeHandler.getNotEqualsAsc(greyNode.getBlackNode(), key));
            return result;
        } else {
            List<WgbData<K, T>> result = WhiteNodeHandler.getNotEqualsAsc(greyNode.getWhiteNode(), key);
            result.add(greyNode.getData());
            result.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));
            return result;
        }
    }

    public static <T, K extends Comparable<K>>
    List<WgbData<K, T>> getNotEqualsDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp == 0) {
            List<WgbData<K, T>> result = BlackNodeHandler.getDesc(greyNode.getBlackNode());
            result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));
            return result;
        } else if (cmp < 0) {
            List<WgbData<K, T>> result = BlackNodeHandler.getNotEqualsDesc(greyNode.getBlackNode(), key);
            result.add(greyNode.getData());
            result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));
            return result;
        } else {
            List<WgbData<K, T>> result = BlackNodeHandler.getDesc(greyNode.getBlackNode());
            result.add(greyNode.getData());
            result.addAll(WhiteNodeHandler.getNotEqualsDesc(greyNode.getWhiteNode(), key));
            return result;
        }
    }
}