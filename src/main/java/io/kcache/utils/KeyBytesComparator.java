/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kcache.utils;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.kafka.common.serialization.Serde;

public class KeyBytesComparator<K> implements Comparator<byte[]>, Serializable {
    private static final long serialVersionUID = -7158035266954550359L;

    private final Serde<K> keySerde;
    private final Comparator<? super K> keyComparator;

    public KeyBytesComparator(Serde<K> keySerde, Comparator<? super K> keyComparator) {
        this.keySerde = keySerde;
        this.keyComparator = keyComparator;
    }

    @Override
    public int compare(byte[] b1, byte[] b2) {
        K key1 = keySerde.deserializer().deserialize(null, b1);
        K key2 = keySerde.deserializer().deserialize(null, b2);
        return keyComparator.compare(key1, key2);
    }
}
