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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ParserTest extends TestCase {
    public static Test suite() throws Exception {
        ClassLoader parentClassLoader = ParserTest.class.getClassLoader();
        File targetDir = new File("target");
        FilenameFilter jarFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        };
        File[] testJars = new File(targetDir, "test-jars").listFiles(jarFilter);
        
        // Build the class loader. We use a single class loader for all test JARs because there
        // are dependencies between them.
        URL[] testJarUrls = new URL[testJars.length];
        for (int i=0; i<testJars.length; i++) {
            testJarUrls[i] = testJars[i].toURL();
        }
        ClassLoader testClassLoader = new URLClassLoader(testJarUrls, parentClassLoader);
        
        // Scan the test JARs for test cases/suites and build a test suite from them
        TestSuite suite = new TestSuite();
        for (int i=0; i<testJars.length; i++) {
            JarInputStream jar = new JarInputStream(new FileInputStream(testJars[i]));
            try {
                JarEntry entry;
                while ((entry = jar.getNextJarEntry()) != null) {
                    String name = entry.getName();
                    if (name.endsWith("Test.class")) {
                        Class testClass = testClassLoader.loadClass(name.substring(0, name.length()-6).replace('/', '.'));
                        if (TestCase.class.isAssignableFrom(testClass) && !Modifier.isAbstract(testClass.getModifiers())) {
                            try {
                                Method suiteMethod = testClass.getMethod("suite", new Class[0]);
                                int modifiers = suiteMethod.getModifiers();
                                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)
                                        && Test.class.isAssignableFrom(suiteMethod.getReturnType())) {
                                    suite.addTest((Test)suiteMethod.invoke(null, new Object[0]));
                                }
                            } catch (NoSuchMethodException e) {
                                suite.addTest(new TestSuite(testClass));
                            }
                        }
                    }
                }
            } finally {
                jar.close();
            }
        }
        
        // Build decorators that execute the test suite using different StAX implementations
        File[] parserJars = new File(targetDir, "parsers").listFiles(jarFilter);
        TestSuite superSuite = new TestSuite();
        for (int i=0; i<parserJars.length; i++) {
            ClassLoader parserClassLoader = new URLClassLoader(new URL[] { parserJars[i].toURL() }, parentClassLoader);
            superSuite.addTest(new SetContextClassLoaderTestWrapper(suite, parserClassLoader));
        }
        return superSuite;
    }
}
