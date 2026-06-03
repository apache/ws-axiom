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
package org.apache.axiom.checker;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.apache.axiom.core.stream.annotations.StringOrCharacterData;
import org.apache.axiom.core.stream.annotations.UnknownCharacterDataType;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.type.typeannotator.ListTypeAnnotator;
import org.checkerframework.framework.type.typeannotator.TypeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TypesUtils;

/**
 * The annotated type factory for the {@link StringOrCharacterDataChecker}. This factory
 * automatically adds {@code @StringOrCharacterData} to {@link String} types and to types that
 * implement {@link org.apache.axiom.core.stream.CharacterData}.
 */
public class StringOrCharacterDataAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    private static final String CHARACTER_DATA_NAME = "org.apache.axiom.core.stream.CharacterData";

    private final AnnotationMirror STRING_OR_CHARACTER_DATA;

    public StringOrCharacterDataAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        STRING_OR_CHARACTER_DATA = AnnotationBuilder.fromClass(elements, StringOrCharacterData.class);
        postInit();
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        LinkedHashSet<Class<? extends Annotation>> qualifiers = new LinkedHashSet<>();
        qualifiers.add(StringOrCharacterData.class);
        qualifiers.add(UnknownCharacterDataType.class);
        return qualifiers;
    }

    @Override
    protected TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(super.createTreeAnnotator());
    }

    @Override
    protected TypeAnnotator createTypeAnnotator() {
        return new ListTypeAnnotator(new StringOrCharacterDataTypeAnnotator(this), super.createTypeAnnotator());
    }

    private class StringOrCharacterDataTypeAnnotator extends TypeAnnotator {

        StringOrCharacterDataTypeAnnotator(StringOrCharacterDataAnnotatedTypeFactory factory) {
            super(factory);
        }

        @Override
        public Void visitDeclared(AnnotatedTypeMirror.AnnotatedDeclaredType type, Void p) {
            if (shouldAnnotate(type)) {
                type.addAnnotation(STRING_OR_CHARACTER_DATA);
            }
            return super.visitDeclared(type, null);
        }
    }

    private boolean shouldAnnotate(AnnotatedTypeMirror.AnnotatedDeclaredType type) {
        QualifierHierarchy hierarchy = getQualifierHierarchy();
        Set<AnnotationMirror> annotations = type.getAnnotations();
        // Don't override existing annotations
        for (AnnotationMirror anno : annotations) {
            if (hierarchy.isSubtypeQualifiersOnly(anno, STRING_OR_CHARACTER_DATA)
                    || hierarchy.isSubtypeQualifiersOnly(STRING_OR_CHARACTER_DATA, anno)) {
                return false;
            }
        }
        TypeMirror underlyingType = type.getUnderlyingType();
        if (TypesUtils.isString(underlyingType)) {
            return true;
        }
        return isCharacterData(underlyingType);
    }

    private boolean isCharacterData(TypeMirror type) {
        if (type instanceof DeclaredType declaredType) {
            String qualifiedName = TypesUtils.getQualifiedName(declaredType).toString();
            if (qualifiedName.equals(CHARACTER_DATA_NAME)) {
                return true;
            }
            // Check supertypes
            for (TypeMirror superType : types.directSupertypes(type)) {
                if (isCharacterData(superType)) {
                    return true;
                }
            }
        }
        return false;
    }
}
