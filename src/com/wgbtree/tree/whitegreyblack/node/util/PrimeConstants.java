package com.wgbtree.tree.whitegreyblack.node.util;

import java.util.Map;

public final class PrimeConstants {

    private PrimeConstants() {
    }

    public static final Map<Integer, Integer> NEXT_PRIME = Map.of(
            2, 3,
            3, 5,
            5, 7,
            7, 11,
            11, 13,
            13, 17,
            17, 19,
            19, 23,
            23, 29,
            29, 31
    );

    public static final int FIRST_PRIME = 2;
}