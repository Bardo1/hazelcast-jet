/*
 * Copyright (c) 2008-2018, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.function;

import com.hazelcast.jet.impl.util.ExceptionUtil;

import java.io.Serializable;
import java.util.function.BiPredicate;

import static com.hazelcast.util.Preconditions.checkNotNull;

/**
 * {@code Serializable} variant of {@link BiPredicate
 * java.util.function.BiPredicate} which declares checked exception.
 */
@FunctionalInterface
public interface DistributedBiPredicate<T, U> extends BiPredicate<T, U>, Serializable {

    /**
     * Exception-declaring version of {@link BiPredicate#test}.
     */
    boolean testEx(T t, U u) throws Exception;

    @Override
    default boolean test(T t, U u) {
        try {
            return testEx(t, u);
        } catch (Exception e) {
            throw ExceptionUtil.sneakyThrow(e);
        }
    }

    /**
     * {@code Serializable} variant of {@link
     * BiPredicate#and(BiPredicate) java.util.function.BiPredicate#and(BiPredicate)}.
     */
    default DistributedBiPredicate<T, U> and(DistributedBiPredicate<? super T, ? super U> other) {
        checkNotNull(other, "other");
        return (T t, U u) -> test(t, u) && other.test(t, u);
    }

    /**
     * {@code Serializable} variant of {@link
     * BiPredicate#negate() java.util.function.BiPredicate#negate()}.
     */
    @Override
    default DistributedBiPredicate<T, U> negate() {
        return (T t, U u) -> !test(t, u);
    }

    /**
     * {@code Serializable} variant of {@link
     * BiPredicate#or(BiPredicate) java.util.function.BiPredicate#or(BiPredicate)}.
     */
    default DistributedBiPredicate<T, U> or(DistributedBiPredicate<? super T, ? super U> other) {
        checkNotNull(other, "other");
        return (T t, U u) -> test(t, u) || other.test(t, u);
    }
}
