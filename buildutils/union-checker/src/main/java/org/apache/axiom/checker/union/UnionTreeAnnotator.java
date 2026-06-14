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

import com.sun.source.tree.ConditionalExpressionTree;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationUtils;

/**
 * Improves the type computed for a conditional expression ({@code cond ? a : b}) where one branch
 * is {@code @Union(types = {...})} and the other is the default {@code @UnknownUnion}.
 *
 * <p>By default, the least upper bound of {@code @Union(S)} and {@code @UnknownUnion} is {@code
 * @UnknownUnion} (the top of the hierarchy), since {@code @UnknownUnion} has no {@link
 * Union#types()} to take the union with. This is overly conservative when the
 * {@code @UnknownUnion}-typed branch's own (erased) type is itself a subtype of a member of {@code
 * S}: in that case, {@link UnionTypeHierarchy}'s escape rule already establishes {@code
 * @UnknownUnion T <: @Union(S)}, so {@code @Union(S)} is itself a valid (and more useful) upper
 * bound for both branches.
 *
 * <p>This allows expressions such as {@code data == null ? "" : data}, where {@code data} is
 * {@code @Union(types = {String.class, CharacterData.class}) Object}, to be used directly (e.g. in
 * a {@code return} statement) without an explicit cast.
 */
final class UnionTreeAnnotator extends TreeAnnotator {

    private final UnionAnnotatedTypeFactory atypeFactory;

    UnionTreeAnnotator(UnionAnnotatedTypeFactory atypeFactory) {
        super(atypeFactory);
        this.atypeFactory = atypeFactory;
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree tree, AnnotatedTypeMirror type) {
        AnnotatedTypeMirror thenType = atypeFactory.getAnnotatedType(tree.getTrueExpression());
        AnnotatedTypeMirror elseType = atypeFactory.getAnnotatedType(tree.getFalseExpression());
        AnnotationMirror promoted = promote(thenType, elseType);
        if (promoted == null) {
            promoted = promote(elseType, thenType);
        }
        if (promoted != null) {
            type.replaceAnnotation(promoted);
        }
        return super.visitConditionalExpression(tree, type);
    }

    /**
     * If {@code unionSide} is {@code @Union(S)} and {@code unknownSide} is {@code @UnknownUnion}
     * with an underlying type that is itself a subtype of some member of {@code S}, returns {@code
     * @Union(S)}. Otherwise returns {@code null}.
     */
    private AnnotationMirror promote(AnnotatedTypeMirror unionSide, AnnotatedTypeMirror unknownSide) {
        AnnotationMirror unionAnno = unionSide.getPrimaryAnnotationInHierarchy(atypeFactory.UNKNOWN_UNION);
        if (unionAnno == null || !AnnotationUtils.areSameByName(unionAnno, Union.class.getCanonicalName())) {
            return null;
        }
        AnnotationMirror unknownAnno = unknownSide.getPrimaryAnnotationInHierarchy(atypeFactory.UNKNOWN_UNION);
        if (unknownAnno == null || !AnnotationUtils.areSameByName(unknownAnno, UnknownUnion.class.getCanonicalName())) {
            return null;
        }
        return atypeFactory.isSubtypeOfUnionMember(unknownSide.getUnderlyingType(), unionAnno) ? unionAnno : null;
    }
}
