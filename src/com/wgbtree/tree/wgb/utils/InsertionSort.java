package com.wgbtree.tree.wgb.utils;

import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class InsertionSort {

    public static <K extends Comparable<K>, T> void sortAscFromBack(Map.Entry<K, Set<T>>[] array, int size) {
        for (int i = size - 1; i > 0; i--) {
            if (array[i - 1].getKey().compareTo(array[i].getKey()) > 0) {
                Map.Entry<K, Set<T>> temp = array[i];
                array[i] = array[i - 1];
                array[i - 1] = temp;
            } else {
                break;
            }
        }
    }

    public static <K extends Comparable<K>, T> void sortDescFromBack(Map.Entry<K, Set<T>>[] array, int size) {
        for (int i = size - 1; i > 0; i--) {
            if (array[i - 1].getKey().compareTo(array[i].getKey()) < 0) {
                Map.Entry<K, Set<T>> temp = array[i];
                array[i] = array[i - 1];
                array[i - 1] = temp;
            } else {
                break;
            }
        }
    }

    public static <K extends Comparable<K>, T> void sortAscFromFront(Map.Entry<K, Set<T>>[] array, int size) {
        for (int i = 1; i < size; i++) {
            if (array[i - 1].getKey().compareTo(array[i].getKey()) > 0) {
                Map.Entry<K, Set<T>> temp = array[i];
                array[i] = array[i - 1];
                array[i - 1] = temp;
            } else {
                break;
            }
        }
    }

    public static <K extends Comparable<K>, T> void sortDescFromFront(Map.Entry<K, Set<T>>[] array, int size) {
        for (int i = 1; i < size; i++) {
            if (array[i - 1].getKey().compareTo(array[i].getKey()) < 0) {
                Map.Entry<K, Set<T>> temp = array[i];
                array[i] = array[i - 1];
                array[i - 1] = temp;
            } else {
                break;
            }
        }
    }
}
