package com.wgbtree.tree.whitegreyblack;

import com.wgbtree.tree.AsTree;
import com.wgbtree.tree.whitegreyblack.node.exception.NotFoundException;
import com.wgbtree.tree.whitegreyblack.node.exception.UniqueException;
import com.wgbtree.tree.whitegreyblack.node.model.GreyNode;
import com.wgbtree.tree.whitegreyblack.node.model.WgbData;
import com.wgbtree.tree.whitegreyblack.node.model.WgbKey;
import com.wgbtree.tree.whitegreyblack.node.service.GreyNodeHandler;
import com.wgbtree.tree.whitegreyblack.node.service.WgbNodeHandler;
import com.wgbtree.tree.whitegreyblack.node.util.Prime;
import com.wgbtree.tree.whitegreyblack.node.util.PrimeConstants;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class WhiteGreyBlackTree<K extends Comparable<K>, T> implements AsTree<K, T>, Serializable {
    private int count;
    private int firstPrime = PrimeConstants.FIRST_PRIME;
    private GreyNode<K, T> greyNode;

    public WhiteGreyBlackTree(int firstPrime) {
        assert Prime.isPrime(firstPrime) : "The given parameter is not a prime!";
        this.firstPrime = firstPrime;
    }

    public WhiteGreyBlackTree() {
    }

    public WgbData<K, T> getById(K key) {
        return WgbNodeHandler.get(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getBiggerThanAsc(K key) {
        return GreyNodeHandler.getBiggerThanAsc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getBiggerThanDesc(K key) {
        return GreyNodeHandler.getBiggerThanDesc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getLessThanAsc(K key) {
        return GreyNodeHandler.getLessThanAsc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getLessThanDesc(K key) {
        return GreyNodeHandler.getLessThanDesc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getBiggerThanEqualsAsc(K key) {
        return GreyNodeHandler.getBiggerThanEqualsAsc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getBiggerThanEqualsDesc(K key) {
        return GreyNodeHandler.getBiggerThanEqualsDesc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getLessThanEqualsAsc(K key) {
        return GreyNodeHandler.getLessThanEqualsAsc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getLessThanEqualsDesc(K key) {
        return GreyNodeHandler.getLessThanEqualsDesc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getBetweenAsc(K low, K max) {
        return GreyNodeHandler.getBetweenAsc(greyNode, new WgbKey<>(low), new WgbKey<>(max));
    }

    public List<WgbData<K, T>> getBetweenDesc(K low, K max) {
        return GreyNodeHandler.getBetweenDesc(greyNode, new WgbKey<>(low), new WgbKey<>(max));
    }

    public List<WgbData<K, T>> getNotEqualsAsc(K key) {
        return GreyNodeHandler.getNotEqualsAsc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getNotEqualsDesc(K key) {
        return GreyNodeHandler.getNotEqualsDesc(greyNode, new WgbKey<>(key));
    }

    public List<WgbData<K, T>> getAsc() {
        return GreyNodeHandler.getAsc(greyNode);
    }

    public List<WgbData<K, T>> getDesc() {
        return GreyNodeHandler.getDesc(greyNode);
    }

    @Override
    public int getNumberOfNodes() {
        return WgbNodeHandler.getNumberOfNodes(greyNode);
    }

    @Override
    public int getNumberOfEmptyNodes() {
        return WgbNodeHandler.getNumberOfEmptyNodes(greyNode);
    }

    @Override
    public int depth() {
        return GreyNodeHandler.depth(greyNode);
    }

    @Override
    public String getName() {
        return "white_grey_black";
    }

    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return this.count;
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return Objects.isNull(greyNode);
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified
     * key.  More formally, returns {@code true} if and only if
     * this map contains a mapping for a key {@code k} such that
     * {@code Objects.equals(key, k)}.  (There can be
     * at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified
     * key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return Objects.nonNull(WgbNodeHandler.get(greyNode, new WgbKey<>((K) key)));
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the
     * specified value.  More formally, returns {@code true} if and only if
     * this map contains at least one mapping to a value {@code v} such that
     * {@code Objects.equals(value, v)}.  This operation
     * will probably require time linear in the map size for most
     * implementations of the {@code Map} interface.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the
     * specified value
     * @throws ClassCastException   if the value is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified value is null and this
     *                              map does not permit null values
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(Object value) {
        return WgbNodeHandler.containsValue(greyNode, (T) value);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that
     * {@code Objects.equals(key, k)},
     * then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>If this map permits null values, then a return value of
     * {@code null} does not <i>necessarily</i> indicate that the map
     * contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to {@code null}.  The {@link #containsKey
     * containsKey} operation may be used to distinguish these two cases.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     * {@code null} if this map contains no mapping for the key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    @SuppressWarnings("unchecked")
    public T get(Object key) {
        WgbData<K, T> result = WgbNodeHandler.get(greyNode, new WgbKey<>((K) key));

        return Objects.isNull(result) ? null : result.getValue();
    }

    /**
     * Associates the specified value with the specified key in this map
     * (optional operation).  If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.  (A map
     * {@code m} is said to contain a mapping for a key {@code k} if and only
     * if {@link #containsKey(Object) m.containsKey(k)} would return
     * {@code true}.)
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with {@code key},
     * if the implementation supports {@code null} values.)
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the class of the specified key or value
     *                                       prevents it from being stored in this map
     * @throws NullPointerException          if the specified key or value is null
     *                                       and this map does not permit null keys or values
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     */
    @Override
    public T put(K key, T value) {
        WgbData<K, T> data = new WgbData<>(key, value);

        try {
            greyNode = GreyNodeHandler.insert(greyNode, firstPrime, data);
            this.count++;
            return value;
        } catch (UniqueException e) {
            WgbData<K, T> oldData = WgbNodeHandler.get(greyNode, data.getKey());

            try {
                greyNode = GreyNodeHandler.delete(greyNode, data.getKey());
                greyNode = GreyNodeHandler.insert(greyNode, firstPrime, data);
            } catch (NotFoundException | UniqueException ignored) {
            }

            assert oldData != null;
            return oldData.getValue();
        }
    }

    /**
     * Removes the mapping for a key from this map if it is present
     * (optional operation).   More formally, if this map contains a mapping
     * from key {@code k} to value {@code v} such that
     * {@code Objects.equals(key, k)}, that mapping
     * is removed.  (The map can contain at most one such mapping.)
     *
     * <p>Returns the value to which this map previously associated the key,
     * or {@code null} if the map contained no mapping for the key.
     *
     * <p>If this map permits null values, then a return value of
     * {@code null} does not <i>necessarily</i> indicate that the map
     * contained no mapping for the key; it's also possible that the map
     * explicitly mapped the key to {@code null}.
     *
     * <p>The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}.
     * @throws UnsupportedOperationException if the {@code remove} operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the key is of an inappropriate type for
     *                                       this map
     *                                       (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if the specified key is null and this
     *                                       map does not permit null keys
     *                                       (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    @SuppressWarnings("unchecked")
    public T remove(Object key) {
        WgbKey<K> wgbKey = new WgbKey<>((K) key);
        WgbData<K, T> oldData = WgbNodeHandler.get(greyNode, wgbKey);

        if (Objects.isNull(oldData)) {
            return null;
        }

        try {
            greyNode = GreyNodeHandler.delete(greyNode, wgbKey);
            this.count--;
        } catch (NotFoundException | UniqueException ignored) {
        }

        return oldData.getValue();
    }

    /**
     * Copies all of the mappings from the specified map to this map
     * (optional operation).  The effect of this call is equivalent to that
     * of calling {@link #put(K, T) put(k, v)} on this map once
     * for each mapping from key {@code k} to value {@code v} in the
     * specified map.  The behavior of this operation is undefined if the
     * specified map is modified while the operation is in progress.
     *
     * @param m mappings to be stored in this map
     * @throws UnsupportedOperationException if the {@code putAll} operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the class of a key or value in the
     *                                       specified map prevents it from being stored in this map
     * @throws NullPointerException          if the specified map is null, or if
     *                                       this map does not permit null keys or values, and the
     *                                       specified map contains null keys or values
     * @throws IllegalArgumentException      if some property of a key or value in
     *                                       the specified map prevents it from being stored in this map
     */
    @Override
    public void putAll(Map<? extends K, ? extends T> m) {
        m.forEach(this::put);
    }

    /**
     * Removes all of the mappings from this map (optional operation).
     * The map will be empty after this call returns.
     *
     * @throws UnsupportedOperationException if the {@code clear} operation
     *                                       is not supported by this map
     */
    @Override
    public void clear() {
        this.greyNode = null;
        this.count = 0;
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations.  It does not support the {@code add} or {@code addAll}
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    @Override
    public Set<K> keySet() {
        return WgbNodeHandler.keySet(greyNode);
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own {@code remove} operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Collection.remove}, {@code removeAll},
     * {@code retainAll} and {@code clear} operations.  It does not
     * support the {@code add} or {@code addAll} operations.
     *
     * @return a collection view of the values contained in this map
     */
    @Override
    public Collection<T> values() {
        return GreyNodeHandler.getAsc(greyNode)
                .stream()
                .map(WgbData::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation, or through the
     * {@code setValue} operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and
     * {@code clear} operations.  It does not support the
     * {@code add} or {@code addAll} operations.
     *
     * @return a set view of the mappings contained in this map
     */
    @Override
    public Set<Map.Entry<K, T>> entrySet() {
        return WgbNodeHandler.entrySet(greyNode);
    }

    public K getMax() {
        WgbData<K, T> data = GreyNodeHandler.getMax(greyNode);

        return Objects.isNull(data) ? null : data.getKey().getValue();
    }

    public K getMin() {
        WgbData<K, T> data = GreyNodeHandler.getMin(greyNode);

        return Objects.isNull(data) ? null : data.getKey().getValue();
    }
}