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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.checkerframework.framework.type.MostlyNoElementQualifierHierarchy;
import org.checkerframework.framework.util.QualifierKind;

/**
 * The qualifier hierarchy for {@link Union}, {@link UnknownUnion} and {@link UnionBottom}.
 *
 * <p>Subtyping between two {@link Union} annotations is subset based: {@code @Union(S1)} is a
 * subtype of {@code @Union(S2)} if every type in {@code S1} is a subtype of some type in {@code
 * S2}. The least upper bound of two {@link Union} annotations is the (deduplicated) union of their
 * {@link Union#types()}. The greatest lower bound is approximated as {@code @Union(types = {})}
 * (a qualifier that is a subtype of every {@code @Union} qualifier) unless the two sets of types
 * are equal.
 */
final class UnionQualifierHierarchy extends MostlyNoElementQualifierHierarchy {

    private final UnionAnnotatedTypeFactory atypeFactory;
    private final Types types;
    private final QualifierKind unionKind;

    UnionQualifierHierarchy(
            Collection<Class<? extends Annotation>> qualifierClasses,
            Elements elements,
            UnionAnnotatedTypeFactory atypeFactory) {
        super(qualifierClasses, elements, atypeFactory);
        this.atypeFactory = atypeFactory;
        this.types = atypeFactory.getChecker().getTypeUtils();
        this.unionKind = getQualifierKind(Union.class.getCanonicalName());
    }

    @Override
    protected boolean isSubtypeWithElements(
            AnnotationMirror subAnno, QualifierKind subKind, AnnotationMirror superAnno, QualifierKind superKind) {
        // Both subKind and superKind are the Union kind: @Union(S1) <: @Union(S2) iff every type
        // in S1 is a subtype of some type in S2.
        List<TypeMirror> superTypes = atypeFactory.getUnionTypes(superAnno);
        for (TypeMirror subType : atypeFactory.getUnionTypes(subAnno)) {
            if (!isSubtypeOfAny(subType, superTypes)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSubtypeOfAny(TypeMirror subType, List<TypeMirror> superTypes) {
        for (TypeMirror superType : superTypes) {
            if (types.isSubtype(subType, superType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected AnnotationMirror leastUpperBoundWithElements(
            AnnotationMirror a1,
            QualifierKind qualifierKind1,
            AnnotationMirror a2,
            QualifierKind qualifierKind2,
            QualifierKind lubKind) {
        // The lub kind has elements, i.e. is the Union kind. Since UnionBottom is the only other
        // kind without elements that can occur here (the lub of @Union and @UnknownUnion is
        // @UnknownUnion, which has no elements and is therefore handled before this method is
        // called), the lub of @Union(S) and @UnionBottom is @Union(S).
        if (qualifierKind1 != unionKind) {
            return a2;
        }
        if (qualifierKind2 != unionKind) {
            return a1;
        }
        List<TypeMirror> types1 = atypeFactory.getUnionTypes(a1);
        List<TypeMirror> union = new ArrayList<>(types1);
        boolean changed = false;
        for (TypeMirror type2 : atypeFactory.getUnionTypes(a2)) {
            if (!containsSameType(union, type2)) {
                union.add(type2);
                changed = true;
            }
        }
        return changed ? atypeFactory.createUnion(union) : a1;
    }

    @Override
    protected AnnotationMirror greatestLowerBoundWithElements(
            AnnotationMirror a1,
            QualifierKind qualifierKind1,
            AnnotationMirror a2,
            QualifierKind qualifierKind2,
            QualifierKind glbKind) {
        // The glb kind has elements, i.e. is the Union kind. @UnionBottom can't occur here because
        // the glb of @Union(S) and @UnionBottom is @UnionBottom, which has no elements and is
        // therefore handled before this method is called. The only other kind without elements is
        // @UnknownUnion, and the glb of @Union(S) and @UnknownUnion is @Union(S).
        if (qualifierKind1 != unionKind) {
            return a2;
        }
        if (qualifierKind2 != unionKind) {
            return a1;
        }
        List<TypeMirror> types1 = atypeFactory.getUnionTypes(a1);
        List<TypeMirror> types2 = atypeFactory.getUnionTypes(a2);
        if (sameTypes(types1, types2)) {
            return a1;
        }
        return atypeFactory.createUnion(List.of());
    }

    private boolean containsSameType(List<TypeMirror> list, TypeMirror type) {
        for (TypeMirror element : list) {
            if (types.isSameType(element, type)) {
                return true;
            }
        }
        return false;
    }

    private boolean sameTypes(List<TypeMirror> types1, List<TypeMirror> types2) {
        if (types1.size() != types2.size()) {
            return false;
        }
        for (TypeMirror type1 : types1) {
            if (!containsSameType(types2, type1)) {
                return false;
            }
        }
        return true;
    }
}
