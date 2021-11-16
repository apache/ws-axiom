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
package org.apache.axiom.ts.jaxp.stax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.axiom.testing.multiton.Instances;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.commons.io.IOUtils;

public abstract class StAXImplementation extends Multiton {
    public static final StAXImplementation JRE = new StAXImplementation("JRE") {
        @Override
        public XMLInputFactory newXMLInputFactory() {
            return XMLInputFactory.newDefaultFactory();
        }

        @Override
        public XMLOutputFactory newXMLOutputFactory() {
            return XMLOutputFactory.newDefaultFactory();
        }
    };

    private final String name;

    private StAXImplementation(String name) {
        this.name = name;
    }

    private static File extractJar(String jarName) throws IOException {
        File jar = File.createTempFile(jarName, ".jar");
        try (InputStream in = StAXImplementation.class.getResourceAsStream(jarName); FileOutputStream out = new FileOutputStream(jar)) {
            IOUtils.copy(in, out);
        }
        return jar;
    }

    private static Properties loadProperties(String jarName) throws IOException {
        int delimiterIndex = jarName.length();
        while (true) {
            while (true) {
                if (delimiterIndex-- == 0) {
                    return null;
                }
                char c = jarName.charAt(delimiterIndex);
                if (c == '.' || c == '_' || c == '-') {
                    break;
                }
            }
            InputStream in = StAXImplementation.class.getResourceAsStream(jarName.substring(0, delimiterIndex) + ".properties");
            if (in != null) {
                try {
                    Properties props = new Properties();
                    props.load(in);
                    return props;
                } finally {
                    in.close();
                }
            }
        }
    }

    @Instances
    private static StAXImplementation[] instances() throws Exception {
        List<StAXImplementation> instances = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                StAXImplementation.class.getResourceAsStream("filelist")))) {
            String jarName;
            while ((jarName = in.readLine()) != null) {
                final File jar = extractJar(jarName);
                final ParentLastURLClassLoader classLoader = new ParentLastURLClassLoader(new URL[] { jar.toURI().toURL() }, StAXImplementation.class.getClassLoader());
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    IOUtils.closeQuietly(classLoader);
                    jar.delete();
                }));
                final Properties props = loadProperties(jarName);
                instances.add(new StAXImplementation(jarName) {
                    private <T> T newFactory(Class<T> type) {
                        if (props != null) {
                            String className = props.getProperty(type.getName());
                            if (className != null) {
                                try {
                                    return classLoader.loadClass(className).asSubclass(type).getDeclaredConstructor().newInstance();
                                } catch (ReflectiveOperationException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                        return ServiceLoader.load(type, classLoader).findFirst().get();
                    }

                    @Override
                    public XMLInputFactory newXMLInputFactory() {
                        return newFactory(XMLInputFactory.class);
                    }

                    @Override
                    public XMLOutputFactory newXMLOutputFactory() {
                        return newFactory(XMLOutputFactory.class);
                    }
                });
            }
        }
        return instances.toArray(new StAXImplementation[instances.size()]);
    }

    public final String getName() {
        return name;
    }

    public abstract XMLInputFactory newXMLInputFactory();
    public abstract XMLOutputFactory newXMLOutputFactory();
}
