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
package org.apache.axiom.weaver.clinit;

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.weaver.SimpleImplementationClassNameMapper;
import org.apache.axiom.weaver.Weaver;
import org.junit.Test;

public class StaticInitializerTest {
    @Test
    public void test() throws Exception {
        ClassLoader cl = StaticInitializerTest.class.getClassLoader();
        Weaver weaver = new Weaver(new SimpleImplementationClassNameMapper("impl"));
        weaver.loadWeavablePackage(cl, "org.apache.axiom.weaver.clinit");
        weaver.addInterfaceToImplement(Iface.class);
        Iface instance =
                weaver.toClassLoader(cl)
                        .loadClass("impl.IfaceImpl")
                        .asSubclass(Iface.class)
                        .getConstructor()
                        .newInstance();
        assertThat(instance.getObject()).isNotNull();
        assertThat(instance.getInt()).isEqualTo(-1);
    }
}
