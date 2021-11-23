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
    private static final ManyToManyAssociation<ImplementationNode,ImplementationNode> PARENT = new ManyToManyAssociation<>(ImplementationNode.class, ImplementationNode.class, Navigability.BIDIRECTIONAL);
    private static final ManyToManyAssociation<ImplementationNode,Mixin> MIXIN = new ManyToManyAssociation<>(ImplementationNode.class, Mixin.class, Navigability.UNIDIRECTIONAL);
    private static final TransitiveClosure<ImplementationNode> ANCESTOR = new TransitiveClosure<>(PARENT, false);
    private static final TransitiveClosure<ImplementationNode> ANCESTOR_OR_SELF = new TransitiveClosure<>(PARENT, true);
    private static final CompositionRelation<ImplementationNode,ImplementationNode,Mixin> TRANSITIVE_MIXIN = new CompositionRelation<>(ANCESTOR_OR_SELF, MIXIN);

    private final MutableReference<Weaver> weaver = Relations.WEAVER.newReferenceHolder(this);
    private final int id;
    private final Class<?> primaryInterface;
    private final MutableReferences<ImplementationNode> parents = PARENT.newReferenceHolder(this);
    private final MutableReferences<ImplementationNode> children = PARENT.getConverse().newReferenceHolder(this);
    private final InterfaceSet ifaces = new InterfaceSet();
    private final MutableReferences<Mixin> mixins = MIXIN.newReferenceHolder(this);
    private final References<ImplementationNode> ancestors = ANCESTOR.newReferenceHolder(this);
    private final References<ImplementationNode> ancestorsOrSelf = ANCESTOR_OR_SELF.newReferenceHolder(this);
    private final References<ImplementationNode> descendantsOrSelf = ANCESTOR_OR_SELF.getConverse().newReferenceHolder(this);
    private final References<Mixin> transitiveMixins = TRANSITIVE_MIXIN.newReferenceHolder(this);
    private boolean requireImplementation;

    ImplementationNode(int id, Set<ImplementationNode> parents, Class<?> iface, Set<Mixin> mixins) {
        this.id = id;
        this.primaryInterface = iface;
        ifaces.add(iface);
        for (Mixin mixin : mixins) {
            // Mixins that only add interfaces have already been applied at this stage.
            if (mixin.contributesCode()) {
                this.mixins.add(mixin);
            }
        }
        for (ImplementationNode parent : parents) {
            this.parents.add(parent);
        }
    }

    void requireImplementation() {
        requireImplementation = true;
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
        for (Mixin mixin : transitiveMixins) {
            weight += mixin.getWeight();
        }
        return weight;
    }

    private boolean isInterfaceInheritance(ImplementationNode parent) {
        for (Class<?> parentInterface : parent.ifaces) {
            for (Class<?> iface : ifaces) {
                if (parentInterface.isAssignableFrom(iface)) {
                    return true;
                }
            }
        }
        return false;
    }

    void dump(StringBuilder builder, ImplementationClassNameMapper implementationClassNameMapper) {
        builder.append("  n");
        builder.append(id);
        builder.append(" [label=<");
        if (implementationClassNameMapper != null) {
            String implementationClassName = implementationClassNameMapper.getImplementationClassName(primaryInterface);
            builder.append("<b>");
            builder.append(implementationClassName.substring(implementationClassName.lastIndexOf('.')+1));
            builder.append("</b><br/>");
        }
        for (Class<?> iface : ifaces) {
            builder.append("<i>");
            builder.append(iface.getSimpleName());
            builder.append("</i><br/>");
        }
        for (Mixin mixin : mixins) {
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
            if (!isInterfaceInheritance(parent)) {
                builder.append(" [style=dashed]");
            }
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
                parentLoop: for (ImplementationNode parent : parents) {
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
        Set<Mixin> commonMixins = new LinkedHashSet<>();
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
        for (Mixin mixin : commonMixins) {
            mixins.add(mixin);
            ifaces.addAll(mixin.getAddedInterfaces());
            for (ImplementationNode child : children) {
                child.mixins.remove(mixin);
                child.ifaces.removeAll(mixin.getAddedInterfaces());
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        boolean first = true;
        for (Class<?> iface : ifaces) {
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

    List<ClassDefinition> toClassDefinitions(ImplementationClassNameMapper implementationClassNameMapper) {
        List<ClassDefinition> classDefinitions = new ArrayList<>();
        String className = implementationClassNameMapper.getImplementationClassName(primaryInterface).replace('.', '/');
        int version = 0;
        for (Mixin mixin : mixins) {
            if (version == 0) {
                version = mixin.getBytecodeVersion();
            } else if (mixin.getBytecodeVersion() != version) {
                throw new WeaverException("Inconsistent bytecode versions");
            }
            classDefinitions.addAll(mixin.createInnerClassDefinitions(className));
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
        for (Class<?> iface : ifaces) {
            ifaceNames.add(Type.getInternalName(iface));
        }
        classDefinitions.add(new ImplementationClassDefinition(
                version,
                access,
                className,
                parents.isEmpty() ? null : implementationClassNameMapper.getImplementationClassName(parents.iterator().next().primaryInterface).replace('.', '/'),
                ifaceNames.toArray(new String[ifaceNames.size()]),
                mixins.toArray(new Mixin[mixins.size()])));
        return classDefinitions;
    }
}
