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

import java.util.HashSet;
import java.util.Set;

final class ReferenceFilter extends ReferenceCollector {
    private final ReferenceCollector parent;
    private final Set<Reference<Clazz>> ignoredClassReferences;
    private final Set<Reference<Clazz>> unusedIgnoredClassReferences;
    
    ReferenceFilter(ReferenceCollector parent, Set<Reference<Clazz>> ignoredClassReferences) {
        this.parent = parent;
        this.ignoredClassReferences = ignoredClassReferences;
        unusedIgnoredClassReferences = new HashSet<>(ignoredClassReferences);
    }

    void collectClassReference(Reference<Clazz> classReference) {
        if (ignoredClassReferences.contains(classReference)) {
            unusedIgnoredClassReferences.remove(classReference);
        } else {
            parent.collectClassReference(classReference);
        }
    }

    Set<Reference<Clazz>> getUnusedIgnoredClassReferences() {
        return unusedIgnoredClassReferences;
    }
}
