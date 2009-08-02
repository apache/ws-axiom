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
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ParserTest extends TestSuite {
    private static final FilenameFilter jarFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }
    };
    
    private final ClassLoader parentClassLoader;
    private TestSuite suite;

    public ParserTest() {
        parentClassLoader = ParserTest.class.getClassLoader();
    }
    
    private void loadTestJars(File[] testJars) throws Exception {
        // Build the class loader. We use a single class loader for all test JARs because there
        // are dependencies between them.
        URL[] testJarUrls = new URL[testJars.length];
        for (int i=0; i<testJars.length; i++) {
            testJarUrls[i] = testJars[i].toURL();
        }
        ClassLoader testClassLoader = new URLClassLoader(testJarUrls, parentClassLoader);
        
        // Scan the test JARs for test cases/suites and build a test suite from them
        suite = new TestSuite();
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
    }
    
    private void addParserFromJRE() {
        addTest(new ParserTestSetup(suite, ClassLoader.getSystemClassLoader(), null));
    }
    
    private void addParsersFromDirectory(File dir) throws Exception {
        // Build decorators that execute the test suite using different StAX implementations
        File[] parserJars = dir.listFiles(jarFilter);
        for (int i=0; i<parserJars.length; i++) {
            addParserJar(parserJars[i]);
        }
    }
    
    private void addParserJar(File parserJar) throws Exception {
        Properties props = null;
        
        String name = parserJar.getName();
        int delimiterIndex = name.length();
        outer: while (true) {
            while (true) {
                if (delimiterIndex-- == 0) {
                    break outer;
                }
                char c = name.charAt(delimiterIndex);
                if (c == '.' || c == '_' || c == '-') {
                    break;
                }
            }
            InputStream in = ParserTest.class.getResourceAsStream("/" + name.substring(0, delimiterIndex) + ".properties");
            if (in != null) {
                try {
                    props = new Properties();
                    props.load(in);
                } finally {
                    in.close();
                }
                break;
            }
        }
        
        ClassLoader parserClassLoader = new URLClassLoader(new URL[] { parserJar.toURL() }, parentClassLoader);
        addTest(new ParserTestSetup(suite, parserClassLoader, props));
    }

    public static Test suite() throws Exception {
        ParserTest suite = new ParserTest();
        
        File targetDir = new File("target");
        suite.loadTestJars(new File(targetDir, "test-jars").listFiles(jarFilter));
        
        // On Java 1.6, also add the StAX implementation from the JRE
        // The check is not very clean but it should be enough for a unit test...
        if (System.getProperty("java.version").startsWith("1.6")) {
            suite.addParserFromJRE();
        }
        
        suite.addParsersFromDirectory(new File("parsers"));
        suite.addParsersFromDirectory(new File(targetDir, "parsers"));
        
        return suite;
    }
}
