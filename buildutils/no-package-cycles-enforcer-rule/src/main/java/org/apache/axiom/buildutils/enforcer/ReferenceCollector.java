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
package org.apache.axiom.buildutils.enforcer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedSubgraph;

final class ReferenceCollector {
    private final Set<Reference> ignoredClassReferences;
    private final Set<Reference> packageReferences = new HashSet<>();
    private final Map<Reference,Reference> classReferenceSamples = new HashMap<>();
    
    ReferenceCollector(Set<Reference> ignoredClassReferences) {
        this.ignoredClassReferences = ignoredClassReferences;
    }

    private static String getPackageName(String className) {
        return className.substring(0, className.lastIndexOf('.'));
    }
    
    void collectClassReference(String from, String to) {
        Reference classReference = new Reference(from, to);
        if (!ignoredClassReferences.contains(classReference)) {
            String fromPackage = getPackageName(from);
            String toPackage = getPackageName(to);
            if (!fromPackage.equals(toPackage)) {
                Reference packageReference = new Reference(fromPackage, toPackage);
                if (packageReferences.add(packageReference)) {
                    classReferenceSamples.put(packageReference, classReference);
                }
            }
        }
    }
    
    Set<Reference> getClassReferencesForPackageCycle() {
        DirectedGraph<String,Reference> graph = new DefaultDirectedGraph<>(Reference.class);
        for (Reference reference : packageReferences) {
            graph.addVertex(reference.getFrom());
            graph.addVertex(reference.getTo());
            graph.addEdge(reference.getFrom(), reference.getTo(), reference);
        }
        List<DirectedSubgraph<String,Reference>> cycles = new StrongConnectivityInspector<String,Reference>(graph).stronglyConnectedSubgraphs();
        for (DirectedSubgraph<String,Reference> cycle : cycles) {
            if (cycle.vertexSet().size() > 1) {
                Set<Reference> classReferences = new HashSet<>();
                for (Reference packageReference : cycle.edgeSet()) {
                    classReferences.add(classReferenceSamples.get(packageReference));
                }
                return classReferences;
            }
        }
        return null;
    }
}
