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
package org.apache.axiom.ts.dom.w3c;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.axiom.testutils.suite.MatrixTestNode;
import org.apache.axiom.testutils.suite.ParentNode;
import org.apache.axiom.ts.dom.DocumentBuilderFactoryFactory;
import org.objectweb.asm.ClassReader;
import org.w3c.domts.DOMTestCase;
import org.w3c.domts.DOMTestDocumentBuilderFactory;
import org.w3c.domts.DOMTestIncompatibleException;
import org.w3c.domts.DOMTestSink;
import org.w3c.domts.DOMTestSuite;
import org.w3c.domts.DocumentBuilderSetting;

import com.google.common.collect.ImmutableList;

public final class W3CDOMTestSuite {
    public static MatrixTestNode create(
            DOMTestSuiteFactory domTestSuiteFactory,
            DocumentBuilderFactoryFactory dbff,
            DOMFeature... unsupportedFeatures) {
        Set<DOMFeature> unsupportedFeaturesSet =
                new HashSet<DOMFeature>(Arrays.asList(unsupportedFeatures));

        final DOMTestDocumentBuilderFactory factory;
        try {
            factory =
                    new DOMTestDocumentBuilderFactoryImpl(
                            dbff,
                            new DocumentBuilderSetting[] {
                                DocumentBuilderSetting.notCoalescing,
                                DocumentBuilderSetting.notExpandEntityReferences,
                                DocumentBuilderSetting.notIgnoringElementContentWhitespace,
                                DocumentBuilderSetting.namespaceAware,
                                DocumentBuilderSetting.notValidating
                            });
        } catch (DOMTestIncompatibleException ex) {
            // TODO
            throw new Error(ex);
        }

        DOMTestSuite suite;
        try {
            suite = domTestSuiteFactory.create(factory);
        } catch (Exception ex) {
            // TODO
            throw new Error(ex);
        }

        ImmutableList.Builder<W3CTestNode> testNodes = ImmutableList.builder();
        suite.build(
                new DOMTestSink() {
                    @Override
                    public void addTest(Class<? extends DOMTestCase> testClass) {
                        try {
                            if (!unsupportedFeaturesSet.isEmpty()) {
                                Set<DOMFeature> usedFeatures = new HashSet<DOMFeature>();
                                DOMFeature.matchFeatures(testClass, usedFeatures);
                                ClassReader classReader =
                                        new ClassReader(
                                                testClass.getResourceAsStream(
                                                        testClass.getSimpleName() + ".class"));
                                DOMTSClassVisitor cv = new DOMTSClassVisitor(usedFeatures);
                                classReader.accept(
                                        cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                                usedFeatures.retainAll(unsupportedFeaturesSet);
                                if (!usedFeatures.isEmpty()) {
                                    return;
                                }
                            }
                            Constructor<? extends DOMTestCase> testConstructor =
                                    testClass.getConstructor(DOMTestDocumentBuilderFactory.class);
                            DOMTestCase test;
                            try {
                                test = testConstructor.newInstance(new Object[] {factory});
                            } catch (InvocationTargetException ex) {
                                throw ex.getTargetException();
                            }
                            test.setFramework(JUnitTestFramework.INSTANCE);
                            testNodes.add(new W3CTestNode(test));
                        } catch (Throwable ex) {
                            // TODO
                            throw new Error(ex);
                        }
                    }
                });
        return new ParentNode(testNodes.build());
    }
}
