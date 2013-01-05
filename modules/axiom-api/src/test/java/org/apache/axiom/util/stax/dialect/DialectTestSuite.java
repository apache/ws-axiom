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
package org.apache.axiom.util.stax.dialect;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DialectTestSuite extends TestSuite {
    private static final FilenameFilter jarFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }
    };
    
    public static Test suite() throws Exception {
        DialectTestSuiteBuilder builder = new DialectTestSuiteBuilder();
        
        File targetDir = new File("target");
        
        // On Java 1.6 and above, also add the StAX implementation from the JRE
        if (!System.getProperty("java.version").startsWith("1.5")) {
            Properties props = new Properties();
            props.setProperty("javax.xml.stream.XMLInputFactory", "com.sun.xml.internal.stream.XMLInputFactoryImpl");
            props.setProperty("javax.xml.stream.XMLOutputFactory", "com.sun.xml.internal.stream.XMLOutputFactoryImpl");
            builder.addImplementation(new StAXImplementation("JRE", ClassLoader.getSystemClassLoader(), props));
            // SJSXP doesn't report whitespace in prolog
            builder.exclude(TestGetTextInProlog.class, "(implementation=JRE)");
        }
        
        addParsersFromDirectory(builder, new File("parsers"));
        addParsersFromDirectory(builder, new File(targetDir, "parsers"));
        
        // SJSXP doesn't report whitespace in prolog
        builder.exclude(TestGetTextInProlog.class, "(implementation=sjsxp-1.0.1.jar)");
        
        return builder.build();
    }

    private static void addParsersFromDirectory(DialectTestSuiteBuilder builder, File dir) throws Exception {
        if (dir.exists()) {
            File[] parserJars = dir.listFiles(jarFilter);
            for (int i=0; i<parserJars.length; i++) {
                addParserJar(builder, parserJars[i]);
            }
        }
    }
    
    private static void addParserJar(DialectTestSuiteBuilder builder, File parserJar) throws Exception {
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
            InputStream in = DialectTestSuite.class.getResourceAsStream(name.substring(0, delimiterIndex) + ".properties");
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
        
        ClassLoader parserClassLoader = new ParentLastURLClassLoader(
                new URL[] { parserJar.toURI().toURL() }, DialectTestSuite.class.getClassLoader());
        builder.addImplementation(new StAXImplementation(parserJar.getName(), parserClassLoader, props));
    }
}
