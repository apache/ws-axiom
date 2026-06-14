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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.TypeHierarchy;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;

/** The annotated type factory for the {@link UnionChecker}. */
public class UnionAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    final AnnotationMirror UNKNOWN_UNION;

    private final ExecutableElement typesElement;
    private final Types types;

    public UnionAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        UNKNOWN_UNION = AnnotationBuilder.fromClass(elements, UnknownUnion.class);
        typesElement = TreeUtils.getMethod(Union.class, "types", 0, getProcessingEnv());
        types = checker.getTypeUtils();
        postInit();
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        LinkedHashSet<Class<? extends Annotation>> qualifiers = new LinkedHashSet<>();
        qualifiers.add(Union.class);
        qualifiers.add(UnknownUnion.class);
        qualifiers.add(UnionBottom.class);
        return qualifiers;
    }

    @Override
    protected QualifierHierarchy createQualifierHierarchy() {
        return new UnionQualifierHierarchy(getSupportedTypeQualifiers(), elements, this);
    }

    @Override
    protected TypeHierarchy createTypeHierarchy() {
        return new UnionTypeHierarchy(
                checker, getQualifierHierarchy(), ignoreRawTypeArguments, checker.hasOption("invariantArrays"), this);
    }

    @Override
    public CFTransfer createFlowTransferFunction(CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        return new UnionTransfer(analysis);
    }

    @Override
    protected TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(super.createTreeAnnotator(), new UnionTreeAnnotator(this));
    }

    /**
     * Returns whether {@code underlyingType} (erased) is a subtype of one of the types listed in
     * {@code unionAnno}'s {@link Union#types()}. This is the same escape rule that {@link
     * UnionTypeHierarchy} applies to allow an {@code @UnknownUnion}-typed expression to be used
     * where {@code @Union(types = {...})} is expected.
     */
    boolean isSubtypeOfUnionMember(TypeMirror underlyingType, AnnotationMirror unionAnno) {
        TypeMirror erased = types.erasure(underlyingType);
        for (TypeMirror memberType : getUnionTypes(unionAnno)) {
            if (types.isSubtype(erased, memberType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the {@link Union#types()} element of the given {@code @Union} annotation.
     *
     * @param unionAnno a {@code @Union(types = {...})} annotation
     * @return the types listed in {@link Union#types()}
     */
    List<TypeMirror> getUnionTypes(AnnotationMirror unionAnno) {
        return AnnotationUtils.getElementValueArray(unionAnno, typesElement, TypeMirror.class);
    }

    /**
     * Creates a {@code @Union(types = {...})} annotation for the given member types.
     *
     * @param memberTypes the types to use for {@link Union#types()}
     * @return the resulting annotation
     */
    AnnotationMirror createUnion(List<TypeMirror> memberTypes) {
        return new AnnotationBuilder(getProcessingEnv(), Union.class)
                .setValue("types", memberTypes)
                .build();
    }

    /**
     * Creates a {@code @Union(types = {memberType})} annotation for a single member type.
     *
     * @param memberType the type to use for {@link Union#types()}
     * @return the resulting annotation
     */
    AnnotationMirror createUnion(TypeMirror memberType) {
        return createUnion(List.of(memberType));
    }
}
