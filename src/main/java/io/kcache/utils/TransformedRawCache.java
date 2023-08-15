/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kcache.utils;

import io.kcache.Cache;
import io.kcache.KeyValueIterator;
import io.kcache.KeyValueIterators;
import org.apache.kafka.common.serialization.Serde;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A cache which wraps a raw cache and uses serdes to transform the bytes.
 */
public class TransformedRawCache<K, V> implements Cache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(TransformedRawCache.class);

    private final Serde<K> keySerde;
    private final Serde<V> valueSerde;
    private final Cache<byte[], byte[]> rawCache;
    private final Comparator<K> comparator;

    public TransformedRawCache(Serde<K> keySerde,
                               Serde<V> valueSerde,
                               Cache<byte[], byte[]> rawCache) {
        this.keySerde = keySerde;
        this.valueSerde = valueSerde;
        this.rawCache = rawCache;
        Comparator<? super byte[]> rawComparator = rawCache.comparator();
        this.comparator = rawComparator == null ? null : (k1, k2) -> {
            byte[] b1 = keySerde.serializer().serialize(null, k1);
            byte[] b2 = keySerde.serializer().serialize(null, k2);
            return rawComparator.compare(b1, b2);
        };
    }

    @Override
    public Comparator<? super K> comparator() {
        return comparator;
    }

    @Override
    public boolean isPersistent() {
        return rawCache.isPersistent();
    }

    @Override
    public void init() {
        rawCache.init();
    }

    @Override
    public void reset() {
        rawCache.reset();
    }

    @Override
    public void sync() {
        rawCache.sync();
    }

    @Override
    public int size() {
        return rawCache.size();
    }

    @Override
    public boolean isEmpty() {
        return rawCache.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        byte[] keyBytes = keySerde.serializer().serialize(null, (K) key);
        return rawCache.containsKey(keyBytes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(Object value) {
        byte[] valueBytes = valueSerde.serializer().serialize(null, (V) value);
        return rawCache.containsValue(valueBytes);
    }

    @Override
    public V put(final K key, final V value) {
        Objects.requireNonNull(key, "key cannot be null");
        byte[] keyBytes = keySerde.serializer().serialize(null, key);
        byte[] valueBytes = valueSerde.serializer().serialize(null, value);
        byte[] originalValueBytes = rawCache.put(keyBytes, valueBytes);
        return valueSerde.deserializer().deserialize(null, originalValueBytes);
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        Objects.requireNonNull(key, "key cannot be null");
        byte[] keyBytes = keySerde.serializer().serialize(null, key);
        byte[] valueBytes = valueSerde.serializer().serialize(null, value);
        byte[] originalValueBytes = rawCache.putIfAbsent(keyBytes, valueBytes);
        return valueSerde.deserializer().deserialize(null, originalValueBytes);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> entries) {
        Map<byte[], byte[]> rawEntries = entries.entrySet().stream()
            .collect(Collectors.toMap(
                e -> keySerde.serializer().serialize(null, e.getKey()),
                e -> valueSerde.serializer().serialize(null, e.getValue())));
        rawCache.putAll(rawEntries);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(final Object key) {
        byte[] keyBytes = keySerde.serializer().serialize(null, (K) key);
        byte[] valueBytes = rawCache.get(keyBytes);
        return valueSerde.deserializer().deserialize(null, valueBytes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(final Object key) {
        Objects.requireNonNull(key, "key cannot be null");
        byte[] keyBytes = keySerde.serializer().serialize(null, (K) key);
        byte[] valueBytes = rawCache.remove(keyBytes);
        return valueSerde.deserializer().deserialize(null, valueBytes);
    }

    @Override
    public void clear() {
        rawCache.clear();
    }

    @Override
    public Set<K> keySet() {
        try (KeyValueIterator<K, V> iter = all()) {
            return Streams.streamOf(iter)
                .map(kv -> kv.key)
                .collect(Collectors.toCollection(() -> new TreeSet<>(comparator())));
        }
    }

    @Override
    public Collection<V> values() {
        try (KeyValueIterator<K, V> iter = all()) {
            return Streams.streamOf(iter)
                .map(kv -> kv.value)
                .collect(Collectors.toList());
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        try (KeyValueIterator<K, V> iter = all()) {
            return Streams.streamOf(iter)
                .map(kv -> new AbstractMap.SimpleEntry<>(kv.key, kv.value))
                .collect(Collectors.toCollection(
                    () -> new TreeSet<>(
                        (e1, e2) -> comparator().compare(e1.getKey(), e2.getKey()))));
        }
    }

    @Override
    public K firstKey() {
        return keySerde.deserializer().deserialize(null, rawCache.firstKey());
    }

    @Override
    public K lastKey() {
        return keySerde.deserializer().deserialize(null, rawCache.lastKey());
    }

    @Override
    public Cache<K, V> subCache(K from, boolean fromInclusive, K to, boolean toInclusive) {
        byte[] fromBytes = keySerde.serializer().serialize(null, from);
        byte[] toBytes = keySerde.serializer().serialize(null, to);
        return new TransformedRawCache<>(
            keySerde,
            valueSerde,
            rawCache.subCache(fromBytes, fromInclusive, toBytes, toInclusive));
    }

    @Override
    public KeyValueIterator<K, V> range(K from, boolean fromInclusive, K to, boolean toInclusive) {
        byte[] fromBytes = keySerde.serializer().serialize(null, from);
        byte[] toBytes = keySerde.serializer().serialize(null, to);

        final KeyValueIterator<byte[], byte[]> rawIterator = rawCache.range(fromBytes, fromInclusive, toBytes, toInclusive);
        return KeyValueIterators.transformRawIterator(keySerde, valueSerde, rawIterator);
    }

    @Override
    public KeyValueIterator<K, V> all() {
        final KeyValueIterator<byte[], byte[]> rawIterator = rawCache.all();
        return KeyValueIterators.transformRawIterator(keySerde, valueSerde, rawIterator);
    }

    @Override
    public Cache<K, V> descendingCache() {
        return new TransformedRawCache<>(
            keySerde,
            valueSerde,
            rawCache.descendingCache());
    }

    @Override
    public void flush() {
        rawCache.flush();
    }

    @Override
    public void close() throws IOException {
        rawCache.close();
    }

    @Override
    public void destroy() throws IOException {
        rawCache.destroy();
    }
}
