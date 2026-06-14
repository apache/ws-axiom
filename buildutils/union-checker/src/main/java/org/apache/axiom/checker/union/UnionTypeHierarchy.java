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

import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.DefaultTypeHierarchy;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.javacutil.AnnotationUtils;

/**
 * Adds an escape rule to the default subtyping rules: a value of unconstrained type ({@code
 * @UnknownUnion}) may be assigned to a {@code @Union(types = {...})}-annotated location if its
 * underlying (erased) Java type is itself a subtype of one of {@link Union#types()}. This allows,
 * for example, a {@link String}-typed expression to be returned as {@code @Union(types =
 * {String.class, ...}) Object} without an explicit cast.
 */
final class UnionTypeHierarchy extends DefaultTypeHierarchy {

    private final UnionAnnotatedTypeFactory atypeFactory;

    UnionTypeHierarchy(
            BaseTypeChecker checker,
            QualifierHierarchy qualHierarchy,
            boolean ignoreRawTypes,
            boolean invariantArrayComponents,
            UnionAnnotatedTypeFactory atypeFactory) {
        super(checker, qualHierarchy, ignoreRawTypes, invariantArrayComponents);
        this.atypeFactory = atypeFactory;
    }

    @Override
    public boolean isSubtype(AnnotatedTypeMirror subtype, AnnotatedTypeMirror supertype) {
        if (super.isSubtype(subtype, supertype)) {
            return true;
        }
        AnnotationMirror superAnno = supertype.getPrimaryAnnotationInHierarchy(atypeFactory.UNKNOWN_UNION);
        if (superAnno == null || !AnnotationUtils.areSameByName(superAnno, Union.class.getCanonicalName())) {
            return false;
        }
        AnnotationMirror subAnno = subtype.getPrimaryAnnotationInHierarchy(atypeFactory.UNKNOWN_UNION);
        if (subAnno == null || !AnnotationUtils.areSameByName(subAnno, UnknownUnion.class.getCanonicalName())) {
            return false;
        }
        return atypeFactory.isSubtypeOfUnionMember(subtype.getUnderlyingType(), superAnno);
    }
}
