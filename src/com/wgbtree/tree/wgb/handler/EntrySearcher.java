package com.wgbtree.tree.wgb.handler;

import com.wgbtree.tree.heap.MinHeapTree;
import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EntrySearcher {

    /**
     * Search for the key in the array
     *
     * @param array sorted array
     * @param key   key to search
     * @param size  size of the array
     * @return index of the key if found, otherwise -1
     */
    public static <K extends Comparable<K>, T>
    int searchClosestAsc(Entry<K, Set<T>>[] array, K key, int size) {
        int low = 0;

        if (key == null) {
            for (int i = 0; i < size; i++) {
                if (array[i].getKey() == null) {
                    low = i;
                    break;
                }
            }
        } else {
            int high = size - 1;

            while (low <= high) {
                int mid = (low + high) >>> 1;
                K midKey = array[mid].getKey();
                int cmp = midKey.compareTo(key);

                if (cmp <= 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }

        return low == 0 ? 0 : low - 1;
    }

    /**
     * Search for the key in the array
     *
     * @param array sorted array
     * @param key   key to search
     * @param size  size of the array
     * @return index of the key if found, otherwise -1
     */
    public static <K extends Comparable<K>, T>
    int searchAsc(Entry<K, Set<T>>[] array, K key, int size) {
        int index = -1;

        if (key == null) {
            for (int i = 0; i < size; i++) {
                if (array[i].getKey() == null) {
                    index = i;
                    break;
                }
            }
        } else {
            index = binarySearchAsc(array, key, size);
        }

        return index;
    }

    /**
     * Search for the key in the array
     *
     * @param array sorted array
     * @param key   key to search
     * @param size  size of the array
     * @return index of the key if found, otherwise -1
     */
    public static <K extends Comparable<K>, T>
    int searchClosestDesc(Entry<K, Set<T>>[] array, K key, int size) {
        int low = 0;

        if (key == null) {
            for (int i = 0; i < size; i++) {
                if (array[i].getKey() == null) {
                    low = i;
                    break;
                }
            }
        } else {
            int high = size - 1;

            while (low <= high) {
                int mid = (low + high) >>> 1;
                K midKey = array[mid].getKey();
                int cmp = midKey.compareTo(key);

                if (cmp >= 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }

        return low;
    }

    /**
     * Binary search for ascending order
     *
     * @param array sorted array
     * @param key   key to search
     * @param size  size of the array
     * @return index of the key if found, otherwise -1
     */
    public static <K extends Comparable<K>, T>
    int searchDesc(Entry<K, Set<T>>[] array, K key, int size) {
        int index = -1;

        if (key == null) {
            for (int i = 0; i < size; i++) {
                if (array[i].getKey() == null) {
                    index = i;
                    break;
                }
            }
        } else {
            index = binarySearchDesc(array, key, size);
        }

        return index;
    }

    /**
     * Binary search for ascending order
     *
     * @param array sorted array
     * @param key   key to search
     * @param size  size of the array
     * @return index of the key if found, otherwise -1
     */
    public static <K extends Comparable<K>, T>
    int binarySearchAsc(Entry<K, Set<T>>[] array, K key, int size) {
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            K midKey = array[mid].getKey();
            int cmp = midKey.compareTo(key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }


    /**
     * Binary search for descending order
     *
     * @param array sorted array
     * @param key   key to search
     * @param size  size of the array
     * @return index of the key if found, otherwise -1
     */
    public static <K extends Comparable<K>, T>
    int binarySearchDesc(Entry<K, Set<T>>[] array, K key, int size) {
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            K midKey = array[mid].getKey();
            int cmp = midKey.compareTo(key);

            if (cmp > 0) {
                low = mid + 1;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }
}
