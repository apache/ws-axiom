/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axiom.checker.union;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * Indicates that the annotated type use holds a value whose runtime type is a subtype of one of
 * the {@link #types()}.
 *
 * <p>This annotation is enforced by the {@code UnionChecker}, which ensures that only values whose
 * (static) type is a subtype of one of {@link #types()}, or another {@code @Union} type that is a
 * subtype of this one, can be assigned to a position annotated with this qualifier. {@code
 * instanceof} checks against one of {@link #types()} (including combinations using {@code ||} and
 * {@code else if}) narrow a value of unknown type to {@code @Union}.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE_USE)
@SubtypeOf(UnknownUnion.class)
public @interface Union {
    /**
     * The types that make up the union. The annotated value's runtime type must be a subtype of at
     * least one of these types.
     */
    Class<?>[] types();
}
