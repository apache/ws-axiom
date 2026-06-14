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
import javax.lang.model.util.Types;
import org.checkerframework.dataflow.analysis.ConditionalTransferResult;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.InstanceOfNode;
import org.checkerframework.dataflow.expression.JavaExpression;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;

/**
 * Refines the type of the tested expression to {@code @Union(types = {T})}, where {@code T} is the
 * type tested against, in the "then" branch of an {@code instanceof} check. Combined with the
 * least upper bound computed by {@link UnionQualifierHierarchy} at control-flow join points, this
 * makes checks such as {@code x instanceof A || x instanceof B} and the equivalent {@code if}/{@code
 * else if} chain refine {@code x} to {@code @Union(types = {A, B})}.
 */
final class UnionTransfer extends CFTransfer {

    private final UnionAnnotatedTypeFactory atypeFactory;
    private final Types types;

    UnionTransfer(CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        super(analysis);
        atypeFactory = (UnionAnnotatedTypeFactory) analysis.getTypeFactory();
        types = atypeFactory.getChecker().getTypeUtils();
    }

    @Override
    public TransferResult<CFValue, CFStore> visitInstanceOf(InstanceOfNode node, TransferInput<CFValue, CFStore> in) {
        TransferResult<CFValue, CFStore> result = super.visitInstanceOf(node, in);

        JavaExpression expr = JavaExpression.fromNode(node.getOperand());
        AnnotationMirror refined = atypeFactory.createUnion(types.erasure(node.getRefType()));

        CFStore thenStore;
        CFStore elseStore;
        if (result.containsTwoStores()) {
            thenStore = result.getThenStore();
            elseStore = result.getElseStore();
        } else {
            thenStore = result.getRegularStore();
            elseStore = thenStore.copy();
        }
        thenStore.insertValue(expr, refined);
        return new ConditionalTransferResult<>(result.getResultValue(), thenStore, elseStore);
    }
}
