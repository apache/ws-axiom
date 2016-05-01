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

public final class VisibilityRuleBuilder {
    private String[] packages;
    private Boolean allowPublicUsage;

    public String[] getPackages() {
        return packages;
    }

    public void setPackages(String[] packages) {
        this.packages = packages;
    }

    public Boolean getAllowPublicUsage() {
        return allowPublicUsage;
    }

    public void setAllowPublicUsage(Boolean allowPublicUsage) {
        this.allowPublicUsage = allowPublicUsage;
    }

    VisibilityRule build() {
        return new VisibilityRule(PackageMatcher.from(packages), allowPublicUsage);
    }
}
