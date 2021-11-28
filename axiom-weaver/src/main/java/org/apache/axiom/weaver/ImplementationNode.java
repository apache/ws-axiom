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
package org.apache.axiom.weaver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.axiom.weaver.mixin.ClassDefinition;
import org.apache.axiom.weaver.mixin.Mixin;
import org.apache.axiom.weaver.mixin.TargetContext;
import org.apache.axiom.weaver.mixin.WeavingContext;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.veithen.jrel.References;
import com.github.veithen.jrel.association.ManyToManyAssociation;
import com.github.veithen.jrel.association.MutableReference;
import com.github.veithen.jrel.association.MutableReferences;
import com.github.veithen.jrel.association.Navigability;
import com.github.veithen.jrel.collection.LinkedIdentityHashSet;
import com.github.veithen.jrel.composition.CompositionRelation;
import com.github.veithen.jrel.transitive.TransitiveClosure;

final class ImplementationNode {
    private static final ManyToManyAssociation<ImplementationNode, ImplementationNode> PARENT =
            new ManyToManyAssociation<>(
                    ImplementationNode.class, ImplementationNode.class, Navigability.BIDIRECTIONAL);
    private static final ManyToManyAssociation<ImplementationNode, MixinNode> MIXIN =
            new ManyToManyAssociation<>(
                    ImplementationNode.class, MixinNode.class, Navigability.UNIDIRECTIONAL);
    private static final TransitiveClosure<ImplementationNode> ANCESTOR =
            new TransitiveClosure<>(PARENT, false);
    private static final TransitiveClosure<ImplementationNode> ANCESTOR_OR_SELF =
            new TransitiveClosure<>(PARENT, true);
    private static final CompositionRelation<ImplementationNode, ImplementationNode, MixinNode>
            TRANSITIVE_MIXIN = new CompositionRelation<>(ANCESTOR_OR_SELF, MIXIN);

    private final MutableReference<Weaver> weaver = Relations.WEAVER.newReferenceHolder(this);
    private final int id;
    private final InterfaceNode primaryInterface;
    private final MutableReferences<ImplementationNode> parents = PARENT.newReferenceHolder(this);
    private final MutableReferences<ImplementationNode> children =
            PARENT.getConverse().newReferenceHolder(this);
    private final MutableReferences<InterfaceNode> ifaces =
            Relations.IMPLEMENTS.newReferenceHolder(this);
    private final MutableReferences<MixinNode> mixins = MIXIN.newReferenceHolder(this);
    private final References<ImplementationNode> ancestors = ANCESTOR.newReferenceHolder(this);
    private final References<ImplementationNode> ancestorsOrSelf =
            ANCESTOR_OR_SELF.newReferenceHolder(this);
    private final References<ImplementationNode> descendantsOrSelf =
            ANCESTOR_OR_SELF.getConverse().newReferenceHolder(this);
    private final References<MixinNode> transitiveMixins =
            TRANSITIVE_MIXIN.newReferenceHolder(this);
    private final Supplier<String> className;
    private boolean requireImplementation;

    ImplementationNode(
            int id,
            Set<ImplementationNode> parents,
            InterfaceNode iface,
            Set<MixinNode> mixins,
            Supplier<String> className) {
        this.id = id;
        this.primaryInterface = iface;
        ifaces.add(iface);
        this.mixins.addAll(mixins);
        this.parents.addAll(parents);
        this.className = className;
    }

    InterfaceNode getPrimaryInterface() {
        return primaryInterface;
    }

    void requireImplementation() {
        requireImplementation = true;
    }

    String getClassName() {
        return className.get();
    }

    Set<ImplementationNode> getRequiredDescendantsOrSelf() {
        Set<ImplementationNode> result = new LinkedIdentityHashSet<>();
        for (ImplementationNode node : descendantsOrSelf.asSet()) {
            if (node.requireImplementation) {
                result.add(node);
            }
        }
        return result;
    }

    private int getWeight() {
        int weight = 0;
        for (MixinNode mixin : transitiveMixins) {
            weight += mixin.getWeight();
        }
        return weight;
    }

    private Set<Class<?>> getInterfaces() {
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        for (InterfaceNode ifaceNode : ifaces) {
            Class<?> iface = ifaceNode.getInterface();
            if (interfaces.contains(iface)) {
                continue;
            }
            for (Class<?> i : interfaces) {
                if (iface.isAssignableFrom(i)) {
                    continue;
                }
            }
            for (Iterator<Class<?>> it = interfaces.iterator(); it.hasNext(); ) {
                Class<?> i = it.next();
                if (i.isAssignableFrom(iface)) {
                    it.remove();
                }
            }
            interfaces.add(iface);
        }
        return interfaces;
    }

    void dump(StringBuilder builder, boolean showImplName) {
        builder.append("  n");
        builder.append(id);
        builder.append(" [label=<");
        if (showImplName) {
            String implementationClassName = getClassName();
            builder.append("<b>");
            builder.append(
                    implementationClassName.substring(
                            implementationClassName.lastIndexOf('/') + 1));
            builder.append("</b><br/>");
        }
        for (Class<?> iface : getInterfaces()) {
            builder.append("<i>");
            builder.append(iface.getSimpleName());
            builder.append("</i><br/>");
        }
        for (MixinNode mixin : mixins) {
            builder.append(mixin.getName());
            builder.append("<br/>");
        }
        builder.append("[w:");
        builder.append(getWeight());
        builder.append("]>");
        if (!mixins.isEmpty()) {
            builder.append(", penwidth=2");
        }
        if (requireImplementation) {
            builder.append(", style=filled");
        }
        builder.append("];\n");
        for (ImplementationNode parent : parents) {
            builder.append("  n");
            builder.append(id);
            builder.append(" -> n");
            builder.append(parent.id);
            builder.append(";\n");
        }
    }

    static void merge(Set<ImplementationNode> nodes) {
        ImplementationNode target = null;
        // If the set contains a node requiring implementation, use that as a target (so that the
        // implementation class name is predictable). Otherwise use the first node; this will in
        // general be the interface the highest up in the hierarchy (because super-interfaces are
        // added first).
        for (ImplementationNode node : nodes) {
            if (target == null) {
                target = node;
            }
            if (node.requireImplementation) {
                target = node;
                break;
            }
        }
        for (ImplementationNode node : nodes) {
            node.parents.removeAll(nodes);
            node.children.removeAll(nodes);
            if (node != target) {
                target.ifaces.addAll(node.ifaces);
                target.mixins.addAll(node.mixins);
                target.parents.addAll(node.parents);
                target.children.addAll(node.children);
                node.parents.clear();
                node.children.clear();
                node.ifaces.clear();
                node.weaver.set(null);
            }
        }
    }

    void removeIfNotRequired() {
        if (!requireImplementation && mixins.isEmpty()) {
            for (ImplementationNode child : children) {
                child.ifaces.addAll(ifaces);
                child.parents.addAll(parents);
            }
            parents.clear();
            children.clear();
            ifaces.clear();
            weaver.set(null);
        }
    }

    void reduce() {
        ANCESTOR.reduce(this);
    }

    boolean compact() {
        ANCESTOR.reduce(this);
        if (!requireImplementation && (children.size() <= 1 || mixins.isEmpty())) {
            for (ImplementationNode child : children) {
                child.ifaces.addAll(ifaces);
                child.mixins.addAll(mixins);
                parentLoop:
                for (ImplementationNode parent : parents) {
                    for (ImplementationNode existingParent : child.parents) {
                        if (existingParent != this && existingParent.ancestors.contains(parent)) {
                            continue parentLoop;
                        }
                    }
                    child.parents.add(parent);
                }
            }
            parents.clear();
            children.clear();
            ifaces.clear();
            weaver.set(null);
            return true;
        } else {
            return false;
        }
    }

    void ensureSingleParent() {
        if (parents.size() <= 1) {
            return;
        }
        int maxWeight = -1;
        ImplementationNode parentToKeep = null;
        for (ImplementationNode parent : parents) {
            int weight = parent.getWeight();
            if (weight > maxWeight) {
                maxWeight = weight;
                parentToKeep = parent;
            }
        }
        for (Iterator<ImplementationNode> it = parents.iterator(); it.hasNext(); ) {
            ImplementationNode parent = it.next();
            if (parent == parentToKeep) {
                continue;
            }
            it.remove();
            ifaces.addAll(parent.ifaces);
            mixins.addAll(parent.transitiveMixins.asSet());
        }
        mixins.removeAll(parentToKeep.transitiveMixins.asSet());
    }

    boolean promoteCommonMixins() {
        if (requireImplementation || children.isEmpty()) {
            return false;
        }
        Set<MixinNode> commonMixins = new LinkedHashSet<>();
        boolean first = true;
        for (ImplementationNode child : children) {
            if (first) {
                commonMixins.addAll(child.mixins);
                first = false;
            } else {
                commonMixins.retainAll(child.mixins);
            }
        }
        if (commonMixins.isEmpty()) {
            return false;
        }
        for (MixinNode mixin : commonMixins) {
            mixins.add(mixin);
            ifaces.add(mixin.getTargetInterface());
            for (ImplementationNode child : children) {
                child.mixins.remove(mixin);
                child.ifaces.remove(mixin.getTargetInterface());
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        boolean first = true;
        for (Class<?> iface : getInterfaces()) {
            if (first) {
                first = false;
            } else {
                builder.append(",");
            }
            builder.append(iface.getSimpleName());
        }
        builder.append(">");
        return builder.toString();
    }

    List<ClassDefinition> toClassDefinitions(WeavingContext weavingContext) {
        List<ClassDefinition> classDefinitions = new ArrayList<>();
        TargetContext targetContext = new TargetContextImpl(weavingContext, getClassName());
        int version = 0;
        List<Mixin> mixins = new ArrayList<>();
        for (MixinNode mixinNode : this.mixins) {
            Mixin mixin = mixinNode.getMixin();
            if (version == 0) {
                version = mixin.getBytecodeVersion();
            } else if (mixin.getBytecodeVersion() != version) {
                throw new WeaverException("Inconsistent bytecode versions");
            }
            classDefinitions.addAll(mixin.createInnerClassDefinitions(targetContext));
            mixins.add(mixin);
        }
        if (version == 0) {
            version = Opcodes.V1_7;
        }
        int access = Opcodes.ACC_PUBLIC;
        if (!requireImplementation) {
            access |= Opcodes.ACC_ABSTRACT;
        }
        if (children.isEmpty()) {
            access |= Opcodes.ACC_FINAL;
        }
        List<String> ifaceNames = new ArrayList<>();
        for (Class<?> iface : getInterfaces()) {
            ifaceNames.add(Type.getInternalName(iface));
        }
        classDefinitions.add(
                new ImplementationClassDefinition(
                        targetContext,
                        version,
                        access,
                        parents.isEmpty() ? null : parents.iterator().next().getClassName(),
                        ifaceNames.toArray(new String[ifaceNames.size()]),
                        primaryInterface.isSingleton(),
                        mixins.toArray(new Mixin[mixins.size()])));
        return classDefinitions;
    }
}
